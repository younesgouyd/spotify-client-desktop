package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaylistListItem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Discover(
    private val repoStore: RepoStore,
    showPlaylistDetails: (PlaylistId) -> Unit,
    playPlaylist: (PlaylistId) -> Unit
) : Component() {
    override val title: String = "Discover"
    private val state: MutableStateFlow<PlaylistListState> = MutableStateFlow(PlaylistListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                PlaylistListState.State(
                    playlists = LazilyLoadedItems<PlaylistListItem, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { repoStore.playlistRepo.getDiscoverPlaylists(it) },
                        initialOffset = Offset.Index.initial()
                    ),
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
}