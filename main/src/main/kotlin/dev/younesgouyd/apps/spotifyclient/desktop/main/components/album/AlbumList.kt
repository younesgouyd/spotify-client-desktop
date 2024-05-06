package dev.younesgouyd.apps.spotifyclient.desktop.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list.AlbumListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.AlbumListItem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumList(
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit,
    playAlbum: (AlbumId) -> Unit
) : Component() {
    override val title: String = "Albums"
    private val state: MutableStateFlow<AlbumListState> = MutableStateFlow(AlbumListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                AlbumListState.State(
                    albums = LazilyLoadedItems<AlbumListItem, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { repoStore.albumRepo.getSavedAlbums(it) },
                        initialOffset = Offset.Index.initial()
                    ),
                    onAlbumClick = showAlbumDetails,
                    onPlayAlbumClick = playAlbum
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
}