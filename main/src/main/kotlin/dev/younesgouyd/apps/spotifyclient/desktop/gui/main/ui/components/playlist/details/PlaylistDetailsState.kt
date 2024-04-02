package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist
import kotlinx.coroutines.flow.StateFlow

sealed class PlaylistDetailsState {
    data object Loading : PlaylistDetailsState()

    data class State(
        val playlist: Playlist,
        val tracks: StateFlow<List<Playlist.Track>>,
        val loadingTracks: StateFlow<Boolean>,
        val onLoadTracks: () -> Unit,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit,
    ) : PlaylistDetailsState()
}
