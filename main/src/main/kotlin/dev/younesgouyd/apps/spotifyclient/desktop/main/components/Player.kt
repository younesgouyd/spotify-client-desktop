package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlayerController
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player.Player
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player.PlayerState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class Player(
    private val playerController: PlayerController,
    private val showAlbumDetails: (AlbumId) -> Unit,
    private val showArtistDetails: (ArtistId) -> Unit,
    private val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    private val addTrackToFolderDialogState: AddTrackToFolderDialogState
) : Component() {
    override val title: String = ""

    init {
        coroutineScope.launch {
            playerController.refresh()
        }
    }

    @Composable
    override fun show() {
        val enabled by playerController.enabled.collectAsState()
        val playbackState by playerController.state.collectAsState()

        Player(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            state = PlayerState(
                enabled = enabled,
                playbackState = playbackState,
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
        )
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}