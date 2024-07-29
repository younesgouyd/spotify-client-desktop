package dev.younesgouyd.apps.spotifyclient.desktop.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details.Playlist
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details.PlaylistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistDetails(
    private val id: PlaylistId,
    private val repoStore: RepoStore,
    showUserDetails: (UserId) -> Unit,
    showArtistDetails: (ArtistId) -> Unit,
    showAlbumDetails: (AlbumId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState
) : Component() {
    override val title: String = "Playlist"
    private val state: MutableStateFlow<PlaylistDetailsState> = MutableStateFlow(PlaylistDetailsState.Loading)
    private val playlist: MutableStateFlow<Playlist?> = MutableStateFlow(null)
    private val followButtonEnabledState: MutableStateFlow<Boolean> = MutableStateFlow(true)

    init {
        coroutineScope.launch {
            reloadPlaylist()
            state.update {
                PlaylistDetailsState.State(
                    playlist = playlist.asStateFlow().mapLatest { requireNotNull(it) }.stateIn(coroutineScope), // todo - !
                    followButtonEnabledState = followButtonEnabledState.asStateFlow(),
                    tracks = LazilyLoadedItems<Playlist.Track, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = {
                            val data = repoStore.trackRepo.getPlaylistTracks(id, it)
                            LazilyLoadedItems.Page(
                                nextOffset = Offset.Index.fromUrl(data.next),
                                items = data.items?.filterNotNull()?.filter { it.track != null }?.map { playlistTrackObject ->
                                    Playlist.Track(
                                        id = playlistTrackObject.track!!.id,
                                        name = playlistTrackObject.track.name,
                                        artists = playlistTrackObject.track.artists?.map {
                                            Playlist.Track.Artist(id = it.id, name = it.name)
                                        } ?: emptyList(),
                                        album = playlistTrackObject.track.album?.let { album ->
                                            Playlist.Track.Album(
                                                id = album.id,
                                                name = album.name,
                                                images = album.images?.toImages() ?: Images.empty()
                                            )
                                        },
                                        duration = playlistTrackObject.track.durationMs?.milliseconds,
                                        popularity = playlistTrackObject.track.popularity,
                                        addedAt = run {
                                            if (playlistTrackObject.addedAt != null) {
                                                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                                val zonedDateTime = ZonedDateTime.parse(playlistTrackObject.addedAt)
                                                zonedDateTime.format(formatter)
                                            } else ""
                                        }
                                    )
                                } ?: emptyList()
                            )
                        },
                        initialOffset = Offset.Index.initial()
                    ),
                    addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                    addTrackToFolderDialogState = addTrackToFolderDialogState,
                    onOwnerClick = showUserDetails,
                    onPlaylistFollowStateChange = {
                        coroutineScope.launch {
                            followButtonEnabledState.update { false }
                            repoStore.playlistRepo.changePlaylistFollowState(id, it)
                            reloadPlaylist()
                            followButtonEnabledState.update { true }
                        }
                    },
                    onPlayClick = play,
                    onTrackClick = playTrack,
                    onArtistClick = showArtistDetails,
                    onAlbumClick = showAlbumDetails
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        PlaylistDetails(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private suspend fun reloadPlaylist() {
        playlist.update {
            val data = repoStore.playlistRepo.get(id)
            Playlist(
                id = data.id,
                name = data.name,
                description = data.description,
                images = data.images?.toImages() ?: Images.empty(),
                owner = data.owner?.let { Playlist.Owner(id = it.id, name = it.displayName) },
                followerCount = data.followers?.total,
                followed = repoStore.playlistRepo.isPlaylistFollowedByCurrentUser(id)
            )
        }
    }
}
