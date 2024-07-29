package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.Artist
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistDetails(
    private val id: ArtistId,
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit,
    private val showArtistDetails: (ArtistId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit,
    playAlbum: (AlbumId) -> Unit,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState
) : Component() {
    override val title: String = "Artist"
    private val state: MutableStateFlow<ArtistDetailsState> = MutableStateFlow(ArtistDetailsState.Loading)
    private val artist: MutableStateFlow<Artist?> = MutableStateFlow(null)
    private val followButtonEnabledState: MutableStateFlow<Boolean> = MutableStateFlow(true)

    init {
        coroutineScope.launch {
            reloadArtist()
            state.update {
                ArtistDetailsState.State(
                    artist = artist.asStateFlow().mapLatest { requireNotNull(it) }.stateIn(coroutineScope), // todo - !
                    followButtonEnabledState = followButtonEnabledState.asStateFlow(),
                    topTracks = run {
                        val data = repoStore.trackRepo.getArtistTopTracks(id)
                        data.tracks?.map {
                            Artist.Track(
                                id = it.id,
                                name = it.name,
                                images = it.album?.images?.toImages2() ?: Images.empty()
                            )
                        } ?: emptyList()
                    },
                    albums = LazilyLoadedItems<Artist.Album, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { offset ->
                            val data = repoStore.albumRepo.getArtistAlbums(id, offset)
                            LazilyLoadedItems.Page(
                                nextOffset = Offset.Index.fromUrl(data.next),
                                items = data.items?.filterNotNull()?.map { simplifiedAlbum ->
                                    Artist.Album(
                                        id = simplifiedAlbum.id,
                                        name = simplifiedAlbum.name,
                                        images = simplifiedAlbum.images?.toImages() ?: Images.empty(),
                                        releaseDate = simplifiedAlbum.releaseDate,
                                        totalTracks = simplifiedAlbum.totalTracks,
                                        artists = simplifiedAlbum.artists?.map {
                                            Artist.Album.Artist(id = it.id, name = it.name)
                                        } ?: emptyList()
                                    )
                                } ?: emptyList()
                            )
                        },
                        initialOffset = Offset.Index.initial()
                    ),
                    addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                    addTrackToFolderDialogState = addTrackToFolderDialogState,
                    relatedArtists = repoStore.artistRepo.getRelatedArtists(id).artists?.filterNotNull()?.map {
                        Artist.RelatedArtist(
                            id = it.id,
                            name = it.name,
                            images = it.images?.toImages2() ?: Images.empty(),
                            genres = it.genres ?: emptyList(),
                            followerCount = it.followers?.total,
                            popularity = it.popularity
                        )
                    } ?: emptyList(),
                    onPlayClick = play,
                    onPlayTrackClick = playTrack,
                    onAlbumClick = showAlbumDetails,
                    onArtistClick = showArtistDetails,
                    onPlayAlbumClick = playAlbum,
                    onArtistFollowStateChange = {
                        coroutineScope.launch {
                            followButtonEnabledState.update { false }
                            repoStore.artistRepo.changeArtistFollowState(id, it)
                            reloadArtist()
                            followButtonEnabledState.update { true }
                        }
                    }
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        ArtistDetails(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private suspend fun reloadArtist() {
        artist.update {
            val data = repoStore.artistRepo.get(id)
            Artist(
                id = data.id,
                name = data.name,
                images = data.images?.toImages2() ?: Images.empty(),
                genres = data.genres ?: emptyList(),
                followerCount = data.followers?.total,
                popularity = data.popularity,
                followed = repoStore.artistRepo.isArtistFollowed(id)
            )
        }
    }
}
