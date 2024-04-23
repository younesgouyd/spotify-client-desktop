package dev.younesgouyd.apps.spotifyclient.desktop.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details.AlbumDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Album
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlbumDetails(
    private val id: AlbumId,
    private val repoStore: RepoStore,
    private val showArtistDetails: (ArtistId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit
) : Component() {
    override val title: String = "Album"
    private val state: MutableStateFlow<AlbumDetailsState> = MutableStateFlow(AlbumDetailsState.Loading)
    private val saved: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val saveRemoveButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val tracks: MutableStateFlow<List<Album.Track>> = MutableStateFlow(emptyList())
    private val loadingTracks: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var reachedTheEnd = false

    init {
        coroutineScope.launch {
            reloadSaved()
            state.update {
                AlbumDetailsState.State(
                    album = repoStore.albumRepo.getAlbum(id),
                    saved = saved.asStateFlow().filterNotNull().stateIn(coroutineScope),
                    saveRemoveButtonEnabled = saveRemoveButtonEnabled.asStateFlow(),
                    tracks = tracks.asStateFlow(),
                    loadingTracks = loadingTracks.asStateFlow(),
                    onSaveClick = {
                        coroutineScope.launch {
                            saveRemoveButtonEnabled.update { false }
                            repoStore.albumRepo.saveAlbum(id)
                            reloadSaved()
                            saveRemoveButtonEnabled.update { true }
                        }
                    },
                    onRemoveClick = {
                        coroutineScope.launch {
                            saveRemoveButtonEnabled.update { false }
                            repoStore.albumRepo.removeAlbum(id)
                            reloadSaved()
                            saveRemoveButtonEnabled.update { true }
                        }
                    },
                    onArtistClick = showArtistDetails,
                    onLoadTracks = ::loadTracks,
                    onPlayClick = play,
                    onTrackClick = playTrack
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

    private suspend fun reloadSaved() {
        saved.update { repoStore.albumRepo.isAlbumSaved(id) }
    }
}