package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistList(
    private val repoStore: RepoStore,
    private val showArtistDetails: (ArtistId) -> Unit
) : Component() {
    override val title: String = "Artists"
    private val state: MutableStateFlow<ArtistListState> = MutableStateFlow(ArtistListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                ArtistListState.State(
                    artists = LazilyLoadedItems<Artist, Offset.Uri>(
                        coroutineScope = coroutineScope,
                        load = { repoStore.artistRepo.getCurrentUserFollowedArtists(it) },
                        initialOffset = Offset.Uri.initial()
                    ),
                    onArtistClick = showArtistDetails
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        ArtistList(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}
