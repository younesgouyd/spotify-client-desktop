package dev.younesgouyd.apps.spotifyclient.desktop.main.components.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaylistListItem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistList(
    private val repoStore: RepoStore,
    private val showPlaylistDetails: (PlaylistId) -> Unit,
    playPlaylist: (PlaylistId) -> Unit
) : Component() {
    override val title: String = "Playlists"
    private val state: MutableStateFlow<PlaylistListState> = MutableStateFlow(PlaylistListState.Loading)
    private val playlists: MutableStateFlow<List<PlaylistListItem>> = MutableStateFlow(emptyList())
    private val loadingPlaylists: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false
    private var offset = 0

    init {
        coroutineScope.launch {
            state.update {
                PlaylistListState.State(
                    playlists = playlists.asStateFlow(),
                    loadingPlaylists = loadingPlaylists.asStateFlow(),
                    onLoadPlaylists = ::loadPlaylists,
                    onPlaylistClick = showPlaylistDetails,
                    onPlayPlaylistClick = playPlaylist
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        PlaylistList(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private fun loadPlaylists() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingPlaylists.value) {
                loadingPlaylists.update { true }
                val result = repoStore.playlistRepo.getCurrentUserPlaylists(20, offset)
                offset += result.size
                reachedTheEnd = result.isEmpty()
                playlists.update { it + result.filterNotNull() }
                loadingPlaylists.update { false }
            }
        }
    }
}