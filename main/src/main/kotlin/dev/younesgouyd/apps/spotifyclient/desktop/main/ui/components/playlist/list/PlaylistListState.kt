package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId

sealed class PlaylistListState {
    data object Loading : PlaylistListState()

    data class State(
        val playlists: LazilyLoadedItems<PlaylistListItem, Offset.Index>,
        val onPlaylistClick: (PlaylistId) -> Unit,
        val onPlayPlaylistClick: (PlaylistId) -> Unit
    ) : PlaylistListState()
}