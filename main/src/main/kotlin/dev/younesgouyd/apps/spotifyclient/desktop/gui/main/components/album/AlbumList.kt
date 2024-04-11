package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list.AlbumListState
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.AlbumListItem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumList(
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit,
) : Component() {
    override val title: String = "Albums"
    private val state: MutableStateFlow<AlbumListState> = MutableStateFlow(AlbumListState.Loading)
    private val albums: MutableStateFlow<List<AlbumListItem>> = MutableStateFlow(emptyList())
    private val loadingAlbums: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false
    private var offset = 0

    init {
        coroutineScope.launch {
            state.update {
                AlbumListState.State(
                    albums = albums.asStateFlow(),
                    loadingAlbums = loadingAlbums.asStateFlow(),
                    onLoadAlbums = ::loadAlbums,
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

    override fun clear() {
        coroutineScope.cancel()
    }

    private fun loadAlbums() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingAlbums.value) {
                loadingAlbums.update { true }
                val result = repoStore.albumRepo.getSavedAlbums(20, offset)
                offset += result.size
                reachedTheEnd = result.isEmpty()
                albums.update { it + result.filterNotNull() }
                loadingAlbums.update { false }
            }
        }
    }
}