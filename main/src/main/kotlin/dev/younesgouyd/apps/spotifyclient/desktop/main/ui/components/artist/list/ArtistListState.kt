package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist

sealed class ArtistListState {
    data object Loading : ArtistListState()

    data class State(
        val artists: LazilyLoadedItems<Artist, Offset.Uri>,
        val onArtistClick: (ArtistId) -> Unit
    ) : ArtistListState()
}
