package dev.younesgouyd.apps.spotifyclient.desktop.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details.PlaylistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Playlist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlaylistDetails(
    private val id: PlaylistId,
    private val repoStore: RepoStore,
    showUserDetails: (UserId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit
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
                    playlist = playlist.asStateFlow().filterNotNull().stateIn(coroutineScope),
                    followButtonEnabledState = followButtonEnabledState.asStateFlow(),
                    tracks = LazilyLoadedItems<Playlist.Track, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { repoStore.trackRepo.getPlaylistTracks(id, it) },
                        initialOffset = Offset.Index.initial()
                    ),
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
                    onTrackClick = playTrack
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
        playlist.update { repoStore.playlistRepo.get(id) }
    }
}
