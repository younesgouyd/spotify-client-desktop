package dev.younesgouyd.apps.spotifyclient.desktop.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
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
    private val tracks: MutableStateFlow<List<Playlist.Track>> = MutableStateFlow(emptyList())
    private val loadingTracks: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false
    private var offset = 0

    init {
        coroutineScope.launch {
            reloadPlaylist()
            state.update {
                PlaylistDetailsState.State(
                    playlist = playlist.asStateFlow().filterNotNull().stateIn(coroutineScope),
                    followButtonEnabledState = followButtonEnabledState.asStateFlow(),
                    tracks = tracks.asStateFlow(),
                    loadingTracks = loadingTracks.asStateFlow(),
                    onOwnerClick = showUserDetails,
                    onPlaylistFollowStateChange = {
                        coroutineScope.launch {
                            followButtonEnabledState.update { false }
                            repoStore.playlistRepo.changePlaylistFollowState(id, it)
                            reloadPlaylist()
                            followButtonEnabledState.update { true }
                        }
                    },
                    onLoadTracks = ::loadTracks,
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

    private fun loadTracks() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingTracks.value) {
                loadingTracks.update { true }
                val result = repoStore.trackRepo.getPlaylistTracks(id, 20, offset)
                offset += result.size
                reachedTheEnd = result.isEmpty()
                tracks.update { it + result.filterNotNull() }
                loadingTracks.update { false }
            }
        }
    }

    private suspend fun reloadPlaylist() {
        playlist.update { repoStore.playlistRepo.get(id) }
    }
}
