package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.details

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Artist

sealed class ArtistDetailsState {
    data object Loading : ArtistDetailsState()

    data class State(
        val artist: Artist,
        val topTracks: List<Artist.Track>,
        val albums: List<Album>,
        val onPlayClick: () -> Unit,
        val onAlbumClick: (AlbumId) -> Unit,
        val onPlayTrackClick: (TrackId) -> Unit
    ) : ArtistDetailsState()
}
