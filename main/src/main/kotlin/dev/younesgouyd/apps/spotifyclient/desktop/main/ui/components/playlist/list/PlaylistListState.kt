package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaylistListItem
import kotlinx.coroutines.flow.StateFlow

sealed class PlaylistListState {
    data object Loading : PlaylistListState()

    data class State(
        val playlists: StateFlow<List<PlaylistListItem>>,
        val loadingPlaylists: StateFlow<Boolean>,
        val onLoadPlaylists: () -> Unit,
        val onPlaylistClick: (PlaylistId) -> Unit
    ) : PlaylistListState()
}