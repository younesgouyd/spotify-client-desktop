package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.AlbumListItem

sealed class AlbumListState {
    data object Loading : AlbumListState()

    data class State(
        val albums: List<AlbumListItem>,
        val onAlbumClick: (AlbumId) -> Unit
    ) : AlbumListState()
}
