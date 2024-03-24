package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.list.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.list.ArtistListState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistList(
    private val repoStore: RepoStore,
    private val showArtistDetails: (ArtistId) -> Unit
) : Component() {
    private val state: MutableStateFlow<ArtistListState> = MutableStateFlow(ArtistListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                ArtistListState.State(
                    artists = repoStore.artistRepo.getCurrentUserFollowedArtists(null, 50),
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
