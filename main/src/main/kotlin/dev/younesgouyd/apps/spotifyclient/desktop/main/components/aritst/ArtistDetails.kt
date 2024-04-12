package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistDetails(
    private val id: ArtistId,
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit
) : Component() {
    override val title: String = "Artist"
    private val state: MutableStateFlow<ArtistDetailsState> = MutableStateFlow(ArtistDetailsState.Loading)
    private val albums: MutableStateFlow<List<Artist.Album>> = MutableStateFlow(emptyList())
    private val loadingAlbums: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false

    init {
        coroutineScope.launch {
            state.update {
                ArtistDetailsState.State(
                    artist = repoStore.artistRepo.get(id),
                    topTracks = repoStore.trackRepo.getArtistTopTracks(id),
                    albums = albums.asStateFlow(),
                    loadingAlbums = loadingAlbums.asStateFlow(),
                    onLoadAlbums = ::loadAlbums,
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

    override fun clear() {
        coroutineScope.cancel()
    }

    private fun loadAlbums() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingAlbums.value) {
                loadingAlbums.update { true }
                val result = repoStore.albumRepo.getArtistAlbums(id, 20, albums.value.size)
                reachedTheEnd = result.isEmpty()
                albums.update { it + result }
                loadingAlbums.update { false }
            }
        }
    }
}
