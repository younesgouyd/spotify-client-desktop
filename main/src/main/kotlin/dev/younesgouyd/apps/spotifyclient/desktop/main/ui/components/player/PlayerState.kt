package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import kotlin.time.Duration

data class PlayerState(
    val enabled: Boolean,
    val playbackState: PlaybackState,
    val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
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
)