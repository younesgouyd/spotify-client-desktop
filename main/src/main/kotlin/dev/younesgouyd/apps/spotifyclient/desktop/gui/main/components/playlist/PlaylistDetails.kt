package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details.PlaylistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetails(
    private val id: PlaylistId,
    private val repoStore: RepoStore
) : Component() {
    private val state: MutableStateFlow<PlaylistDetailsState> = MutableStateFlow(PlaylistDetailsState.Loading)
    private val tracks: MutableStateFlow<List<Playlist.Track>> = MutableStateFlow(emptyList())
    private val loadingTracks: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false
    private var offset = 0

    init {
        coroutineScope.launch {
            state.update {
                PlaylistDetailsState.State(
                    playlist = repoStore.playlistRepo.get(id),
                    tracks = tracks.asStateFlow(),
                    loadingTracks = loadingTracks.asStateFlow(),
                    onLoadTracks = ::loadTracks,
                    onPlayClick = {}, // todo
                    onTrackClick = {}, // todo
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
}
