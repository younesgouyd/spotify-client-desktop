package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

sealed class PlayerState {
    data object Loading : PlayerState()

    data object Unavailable : PlayerState()

    data class Available(
        val enabled: StateFlow<Boolean>,
        val playbackState: PlaybackState,
        val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
        val addTrackToFolderDialogState: AddTrackToFolderDialogState,
        val onAlbumClick: (AlbumId) -> Unit,
        val onArtistClick: (ArtistId) -> Unit,
        val onValueChange: (Duration) -> Unit,
        val onPreviousClick: () -> Unit,
        val onPlayClick: () -> Unit,
        val onPauseClick: () -> Unit,
        val onNextClick: () -> Unit,
        val onCompleted: () -> Unit,
        val onRepeatClick: (PlaybackState.RepeatState) -> Unit,
        val onShuffleClick: (Boolean) -> Unit
    ) : PlayerState()
}