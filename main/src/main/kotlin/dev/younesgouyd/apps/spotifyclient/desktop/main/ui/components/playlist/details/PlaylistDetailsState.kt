package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import kotlinx.coroutines.flow.StateFlow

sealed class PlaylistDetailsState {
    data object Loading : PlaylistDetailsState()

    data class State(
        val playlist: StateFlow<Playlist>,
        val followButtonEnabledState: StateFlow<Boolean>,
        val tracks: LazilyLoadedItems<Playlist.Track, Offset.Index>,
        val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
        val addTrackToFolderDialogState: AddTrackToFolderDialogState,
        val onOwnerClick: (UserId) -> Unit,
        val onPlaylistFollowStateChange: (state: Boolean) -> Unit,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit
    ) : PlaylistDetailsState()
}
