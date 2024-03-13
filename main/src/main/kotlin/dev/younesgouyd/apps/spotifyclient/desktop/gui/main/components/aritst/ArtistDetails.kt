package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.details.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.details.ArtistDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistDetails(
    private val id: ArtistId,
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit
) : Component() {
    private val state: MutableStateFlow<ArtistDetailsState> = MutableStateFlow(ArtistDetailsState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                ArtistDetailsState.State(
                    artist = repoStore.artistRepo.get(id),
                    topTracks = repoStore.trackRepo.getArtistTopTracks(id),
                    albums = repoStore.albumRepo.getArtistAlbums(id, 50, 0),
                    onAlbumClick = showAlbumDetails,
                    onPlayClick = {}, // todo
                    onPlayTrackClick = {} // todo
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        ArtistDetails(state)
    }
}
