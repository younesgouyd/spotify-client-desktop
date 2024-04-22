package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Playlist
import kotlinx.coroutines.flow.StateFlow

sealed class PlaylistDetailsState {
    data object Loading : PlaylistDetailsState()

    data class State(
        val playlist: StateFlow<Playlist>,
        val followButtonEnabledState: StateFlow<Boolean>,
        val tracks: StateFlow<List<Playlist.Track>>,
        val loadingTracks: StateFlow<Boolean>,
        val onOwnerClick: (UserId) -> Unit,
        val onPlaylistFollowStateChange: (state: Boolean) -> Unit,
        val onLoadTracks: () -> Unit,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit
    ) : PlaylistDetailsState()
}
