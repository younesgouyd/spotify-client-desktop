package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details.PlaylistDetailsState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetails(
    private val id: PlaylistId,
    private val repoStore: RepoStore
) : Component() {
    private val state: MutableStateFlow<PlaylistDetailsState> = MutableStateFlow(PlaylistDetailsState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                PlaylistDetailsState.State(
                    playlist = repoStore.playlistRepo.get(id),
                    tracks = repoStore.trackRepo.getPlaylistTracks(id, 50, 0),
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
}
