package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.flow.StateFlow

sealed class ArtistListState {
    data object Loading : ArtistListState()

    data class State(
        val artists: StateFlow<List<Artist>>,
        val loadingArtists: StateFlow<Boolean>,
        val onLoadArtists: () -> Unit,
        val onArtistClick: (ArtistId) -> Unit
    ) : ArtistListState()
}
