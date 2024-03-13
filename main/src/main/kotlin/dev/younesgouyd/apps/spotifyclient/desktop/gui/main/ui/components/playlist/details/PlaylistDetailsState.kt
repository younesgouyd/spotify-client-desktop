package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist

sealed class PlaylistDetailsState {
    object Loading : PlaylistDetailsState()

    data class State(
        val playlist: Playlist,
        val tracks: List<Playlist.Track>,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit,
    ) : PlaylistDetailsState()
}
