package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist

import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaylistOption

data class AddTrackToPlaylistDialogState(
    val playlists: LazilyLoadedItems<PlaylistOption, Offset.Index>,
    val onAddTrackTopPlaylist: (TrackId, PlaylistId) -> Unit
)
