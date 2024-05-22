package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlayerController
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player.Player
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player.PlayerState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class Player(
    private val playerController: PlayerController,
    private val showAlbumDetails: (AlbumId) -> Unit,
    private val showArtistDetails: (ArtistId) -> Unit,
    private val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    private val addTrackToFolderDialogState: AddTrackToFolderDialogState
) : Component() {
    override val title: String = ""
    private val state: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Loading)

    init {
        coroutineScope.launch {
            playerController.refresh()
            state.emitAll(
                playerController.state.mapLatest {
                    if (!it.isSuccess) {
                        PlayerState.Unavailable
                    } else {
                        PlayerState.Available(
                            enabled = playerController.enabled,
                            playbackState = it.getOrThrow(),
                            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                            addTrackToFolderDialogState = addTrackToFolderDialogState,
                            onAlbumClick = showAlbumDetails,
                            onArtistClick = showArtistDetails,
                            onValueChange = { coroutineScope.launch { playerController.seek(it) } },
                            onPreviousClick = { coroutineScope.launch { playerController.previous() } },
                            onPlayClick = { coroutineScope.launch { playerController.play() } },
                            onPauseClick = { coroutineScope.launch { playerController.pause() } },
                            onNextClick = { coroutineScope.launch { playerController.next() } },
                            onCompleted = { coroutineScope.launch { playerController.refresh() } },
                            onRepeatClick = { coroutineScope.launch { playerController.repeat(it) } },
                            onShuffleClick = { coroutineScope.launch { playerController.shuffle(it) } }
                        )
                    }
                }
            )
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        Player(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}