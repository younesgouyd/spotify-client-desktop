package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list.ArtistListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistList(
    private val repoStore: RepoStore,
    private val showArtistDetails: (ArtistId) -> Unit
) : Component() {
    override val title: String = "Artists"
    private val state: MutableStateFlow<ArtistListState> = MutableStateFlow(ArtistListState.Loading)
    private val artists: MutableStateFlow<List<Artist>> = MutableStateFlow(emptyList())
    private val loadingArtists: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false

    init {
        coroutineScope.launch {
            state.update {
                ArtistListState.State(
                    artists = artists.asStateFlow(),
                    loadingArtists = loadingArtists.asStateFlow(),
                    onLoadArtists = ::loadArtists,
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

    private fun loadArtists() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingArtists.value) {
                loadingArtists.update { true }
                val result = repoStore.artistRepo.getCurrentUserFollowedArtists(
                    after = artists.value.lastOrNull()?.id,
                    limit = 20
                )
                reachedTheEnd = result.isEmpty()
                artists.update { it + result }
                loadingArtists.update { false }
            }
        }
    }
}
