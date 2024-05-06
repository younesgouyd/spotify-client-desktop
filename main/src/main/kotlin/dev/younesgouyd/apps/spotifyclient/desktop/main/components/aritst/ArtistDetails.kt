package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtistDetails(
    private val id: ArtistId,
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit,
    playAlbum: (AlbumId) -> Unit
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
                    artist = artist.asStateFlow().filterNotNull().stateIn(coroutineScope),
                    followButtonEnabledState = followButtonEnabledState.asStateFlow(),
                    topTracks = repoStore.trackRepo.getArtistTopTracks(id),
                    albums = LazilyLoadedItems<Artist.Album, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { repoStore.albumRepo.getArtistAlbums(id, it) },
                        initialOffset = Offset.Index.initial()
                    ),
                    onPlayClick = play,
                    onPlayTrackClick = playTrack,
                    onAlbumClick = showAlbumDetails,
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
        artist.update { repoStore.artistRepo.get(id) }
    }
}
