package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.AlbumListItem
import kotlinx.coroutines.flow.StateFlow

sealed class AlbumListState {
    data object Loading : AlbumListState()

    data class State(
        val albums: StateFlow<List<AlbumListItem>>,
        val loadingAlbums: StateFlow<Boolean>,
        val onLoadAlbums: () -> Unit,
        val onAlbumClick: (AlbumId) -> Unit
    ) : AlbumListState()
}
