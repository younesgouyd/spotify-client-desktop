package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details.AlbumDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumDetails(
    private val id: AlbumId,
    private val repoStore: RepoStore
) : Component() {
    private val state: MutableStateFlow<AlbumDetailsState> = MutableStateFlow(AlbumDetailsState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                AlbumDetailsState.State(
                    album = repoStore.albumRepo.getAlbum(id),
                    tracks = repoStore.trackRepo.getAlbumTracks(id, 50, 0),
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
}