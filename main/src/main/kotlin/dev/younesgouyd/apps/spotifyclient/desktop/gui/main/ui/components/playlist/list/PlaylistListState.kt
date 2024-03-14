package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.list

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.SimplifiedPlaylist

sealed class PlaylistListState {
    data object Loading : PlaylistListState()

    data class State(
        val playlists: List<SimplifiedPlaylist>,
        val onPlaylistClick: (PlaylistId) -> Unit
    ) : PlaylistListState()
}