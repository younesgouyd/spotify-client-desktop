package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.flow.StateFlow

sealed class ArtistDetailsState {
    data object Loading : ArtistDetailsState()

    data class State(
        val artist: StateFlow<Artist>,
        val followButtonEnabledState: StateFlow<Boolean>,
        val topTracks: List<Artist.Track>,
        val albums: StateFlow<List<Artist.Album>>,
        val loadingAlbums: StateFlow<Boolean>,
        val onLoadAlbums: () -> Unit,
        val onPlayClick: () -> Unit,
        val onPlayTrackClick: (TrackId) -> Unit,
        val onAlbumClick: (AlbumId) -> Unit,
        val onPlayAlbumClick: (AlbumId) -> Unit,
        val onArtistFollowStateChange: (state: Boolean) -> Unit
    ) : ArtistDetailsState()
}
