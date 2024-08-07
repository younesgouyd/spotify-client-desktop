package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset

sealed class AlbumListState {
    data object Loading : AlbumListState()

    data class State(
        val albums: LazilyLoadedItems<AlbumListItem, Offset.Index>,
        val onAlbumClick: (AlbumId) -> Unit,
        val onArtistClick: (ArtistId) -> Unit,
        val onPlayAlbumClick: (AlbumId) -> Unit
    ) : AlbumListState()
}
