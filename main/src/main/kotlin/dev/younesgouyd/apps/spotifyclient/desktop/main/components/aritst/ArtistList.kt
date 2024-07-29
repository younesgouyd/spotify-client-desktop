package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistItem
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
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
                    artists = LazilyLoadedItems<ArtistItem, Offset.Uri>(
                        coroutineScope = coroutineScope,
                        load = { offset ->
                            val data = repoStore.artistRepo.getCurrentUserFollowedArtists(offset)
                            LazilyLoadedItems.Page(
                                nextOffset = Offset.Uri.fromUrl(data.artists?.next),
                                items = data.artists?.items?.map {
                                    ArtistItem(
                                        id = it.id,
                                        name = it.name,
                                        images = it.images?.toImages() ?: Images.empty()
                                    )
                                } ?: emptyList()
                            )
                        },
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
