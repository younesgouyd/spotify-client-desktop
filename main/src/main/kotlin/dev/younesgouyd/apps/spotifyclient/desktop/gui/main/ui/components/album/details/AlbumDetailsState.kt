package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album

sealed class AlbumDetailsState {
    data object Loading : AlbumDetailsState()

    data class State(
        val album: Album,
        val tracks: List<Album.Track>,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit
    ) : AlbumDetailsState()
}
