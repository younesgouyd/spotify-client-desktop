package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Album
import kotlinx.coroutines.flow.StateFlow

sealed class AlbumDetailsState {
    data object Loading : AlbumDetailsState()

    data class State(
        val album: Album,
        val saved: StateFlow<Boolean>,
        val saveRemoveButtonEnabled: StateFlow<Boolean>,
        val tracks: LazilyLoadedItems<Album.Track, Offset.Index>,
        val onSaveClick: () -> Unit,
        val onRemoveClick: () -> Unit,
        val onArtistClick: (ArtistId) -> Unit,
        val onPlayClick: () -> Unit,
        val onTrackClick: (TrackId) -> Unit
    ) : AlbumDetailsState()
}
