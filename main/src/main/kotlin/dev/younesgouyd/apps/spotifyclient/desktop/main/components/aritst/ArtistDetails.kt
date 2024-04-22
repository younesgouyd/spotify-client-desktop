package dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details.ArtistDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtistDetails(
    private val id: ArtistId,
    private val repoStore: RepoStore,
    private val showAlbumDetails: (AlbumId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit,
    playAlbum: (AlbumId) -> Unit
) : Component() {
    override val title: String = "Artist"
    private val state: MutableStateFlow<ArtistDetailsState> = MutableStateFlow(ArtistDetailsState.Loading)
    private val artist: MutableStateFlow<Artist?> = MutableStateFlow(null)
    private val followButtonEnabledState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val albums: MutableStateFlow<List<Artist.Album>> = MutableStateFlow(emptyList())
    private val loadingAlbums: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false

    init {
        coroutineScope.launch {
            reloadArtist()
            state.update {
                ArtistDetailsState.State(
                    artist = artist.asStateFlow().filterNotNull().stateIn(coroutineScope),
                    followButtonEnabledState = followButtonEnabledState.asStateFlow(),
                    topTracks = repoStore.trackRepo.getArtistTopTracks(id),
                    albums = albums.asStateFlow(),
                    loadingAlbums = loadingAlbums.asStateFlow(),
                    onLoadAlbums = ::loadAlbums,
                    onPlayClick = play,
                    onPlayTrackClick = playTrack,
                    onAlbumClick = showAlbumDetails,
                    onPlayAlbumClick = playAlbum,
                    onArtistFollowStateChange = {
                        coroutineScope.launch {
                            followButtonEnabledState.update { false }
                            repoStore.artistRepo.changeArtistFollowState(id, it)
                            reloadArtist()
                            followButtonEnabledState.update { true }
                        }
                    }
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

    private suspend fun reloadArtist() {
        artist.update { repoStore.artistRepo.get(id) }
    }
}
