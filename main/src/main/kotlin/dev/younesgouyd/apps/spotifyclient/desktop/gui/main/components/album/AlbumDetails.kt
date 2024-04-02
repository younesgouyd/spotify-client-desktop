package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details.AlbumDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumDetails(
    private val id: AlbumId,
    private val repoStore: RepoStore
) : Component() {
    private val state: MutableStateFlow<AlbumDetailsState> = MutableStateFlow(AlbumDetailsState.Loading)
    private val tracks: MutableStateFlow<List<Album.Track>> = MutableStateFlow(emptyList())
    private val loadingTracks: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false

    init {
        coroutineScope.launch {
            state.update {
                AlbumDetailsState.State(
                    album = repoStore.albumRepo.getAlbum(id),
                    tracks = tracks.asStateFlow(),
                    loadingTracks = loadingTracks.asStateFlow(),
                    onLoadTracks = ::loadTracks,
                    onPlayClick = {}, // todo
                    onTrackClick = {} // todo
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        AlbumDetails(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private fun loadTracks() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingTracks.value) {
                loadingTracks.update { true }
                val result = repoStore.trackRepo.getAlbumTracks(id, 50, tracks.value.size)
                reachedTheEnd = result.isEmpty()
                tracks.update { it + result }
                loadingTracks.update { false }
            }
        }
    }
}