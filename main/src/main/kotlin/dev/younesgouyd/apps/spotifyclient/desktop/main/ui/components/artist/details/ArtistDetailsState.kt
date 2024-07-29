package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import kotlinx.coroutines.flow.StateFlow

sealed class ArtistDetailsState {
    data object Loading : ArtistDetailsState()

    data class State(
        val artist: StateFlow<Artist>,
        val followButtonEnabledState: StateFlow<Boolean>,
        val topTracks: List<Artist.Track>,
        val albums: LazilyLoadedItems<Artist.Album, Offset.Index>,
        val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
        val addTrackToFolderDialogState: AddTrackToFolderDialogState,
        val relatedArtists: List<Artist.RelatedArtist>,
        val onPlayClick: () -> Unit,
        val onPlayTrackClick: (TrackId) -> Unit,
        val onArtistClick: (ArtistId) -> Unit,
        val onAlbumClick: (AlbumId) -> Unit,
        val onPlayAlbumClick: (AlbumId) -> Unit,
        val onArtistFollowStateChange: (state: Boolean) -> Unit
    ) : ArtistDetailsState()
}
