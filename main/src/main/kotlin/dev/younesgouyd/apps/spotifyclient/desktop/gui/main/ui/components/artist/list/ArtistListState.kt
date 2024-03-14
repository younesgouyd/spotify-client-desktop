package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.list

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Artist

sealed class ArtistListState {
    data object Loading : ArtistListState()

    data class State(
        val artists: List<Artist>,
        val onArtistClick: (ArtistId) -> Unit
    ) : ArtistListState()
}
