package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album

sealed class AlbumListState {
    object Loading : AlbumListState()

    data class State(
        val albums: List<Album>,
        val onAlbumClick: (AlbumId) -> Unit
    ) : AlbumListState()
}
