package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
import kotlinx.coroutines.flow.StateFlow

sealed class AlbumDetailsState {
    data object Loading : AlbumDetailsState()

    data class State(
        val album: Album,
        val tracks: StateFlow<List<Album.Track>>,
        val loadingTracks: StateFlow<Boolean>,
        val onLoadTracks: () -> Unit,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit
    ) : AlbumDetailsState()
}
