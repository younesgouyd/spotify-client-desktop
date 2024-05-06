package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.AlbumListItem

sealed class AlbumListState {
    data object Loading : AlbumListState()

    data class State(
        val albums: LazilyLoadedItems<AlbumListItem, Offset.Index>,
        val onAlbumClick: (AlbumId) -> Unit,
        val onPlayAlbumClick: (AlbumId) -> Unit
    ) : AlbumListState()
}
