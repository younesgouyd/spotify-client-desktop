package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.list.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.list.PlaylistListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistList(
    private val repoStore: RepoStore,
    private val showPlaylistDetails: (PlaylistId) -> Unit,
) : Component() {
    private val state: MutableStateFlow<PlaylistListState> = MutableStateFlow(PlaylistListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                PlaylistListState.State(
                    playlists = repoStore.playlistRepo.getCurrentUserPlaylists(50, 0),
                    onPlaylistClick = showPlaylistDetails
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        PlaylistList(state)
    }
}