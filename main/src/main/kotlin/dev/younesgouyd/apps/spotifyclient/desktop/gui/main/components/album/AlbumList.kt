package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list.AlbumListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumList(
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit,
) : Component() {
    private val state: MutableStateFlow<AlbumListState> = MutableStateFlow(AlbumListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                AlbumListState.State(
                    albums = repoStore.albumRepo.getSavedAlbums(50, 0),
                    onAlbumClick = showAlbumDetails,
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        AlbumList(state)
    }
}