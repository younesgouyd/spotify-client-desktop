package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset

sealed class ArtistListState {
    data object Loading : ArtistListState()

    data class State(
        val artists: LazilyLoadedItems<ArtistItem, Offset.Uri>,
        val onArtistClick: (ArtistId) -> Unit
    ) : ArtistListState()
}
