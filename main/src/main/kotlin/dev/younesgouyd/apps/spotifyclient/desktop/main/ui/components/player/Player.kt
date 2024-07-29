package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.formatted
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Player(state: PlayerState) {
    when (state) {
        is PlayerState.Available -> Player(state)
        else -> { /* todo */ }
    }
}

@Composable
private fun Player(state: PlayerState.Available) {
    Player(
        enabled = state.enabled,
        playbackState = state.playbackState,
        addTrackToPlaylistDialogState = state.addTrackToPlaylistDialogState,
        addTrackToFolderDialogState = state.addTrackToFolderDialogState,
        onAlbumClick = state.onAlbumClick,
        onArtistClick = state.onArtistClick,
        onValueChange = state.onValueChange,
        onPreviousClick = state.onPreviousClick,
        onPlayClick = state.onPlayClick,
        onPauseClick = state.onPauseClick,
        onNextClick = state.onNextClick,
        onCompleted = state.onCompleted,
        onRepeatClick = state.onRepeatClick,
        onShuffleClick = state.onShuffleClick
    )
}

@Composable
private fun Player(
    enabled: StateFlow<Boolean>,
    playbackState: PlaybackState,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onAlbumClick: (AlbumId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onValueChange: (Duration) -> Unit,
    onPreviousClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onCompleted: () -> Unit,
    onRepeatClick: (PlaybackState.RepeatState) -> Unit,
    onShuffleClick: (Boolean) -> Unit
) {
    val duration = playbackState.track?.duration ?: 0.milliseconds
    val animatableElapsedTime = remember { Animatable(0.milliseconds) }

    _Player(
        enabled = enabled,
        playbackState = playbackState.copy(elapsedTime = animatableElapsedTime.value),
        addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
        addTrackToFolderDialogState = addTrackToFolderDialogState,
        onAlbumClick = onAlbumClick,
        onArtistClick = onArtistClick,
        onValueChange = onValueChange,
        onPreviousClick = onPreviousClick,
        onPlayClick = onPlayClick,
        onPauseClick = onPauseClick,
        onNextClick = onNextClick,
        onRepeatClick = onRepeatClick,
        onShuffleClick = onShuffleClick
    )

    LaunchedEffect(duration) {
        animatableElapsedTime.stop()
        animatableElapsedTime.snapTo(0.milliseconds)
        animatableElapsedTime.updateBounds(lowerBound = 0.milliseconds, upperBound = duration)
        if (playbackState.playing != null && playbackState.playing) {
            animatableElapsedTime.animateTo(
                targetValue = duration,
                animationSpec = linearAnimation(duration)
            )
            onCompleted()
        }
    }

    LaunchedEffect(playbackState.playing) {
        if (playbackState.playing != null && playbackState.playing) {
            animatableElapsedTime.animateTo(
                targetValue = duration,
                animationSpec = linearAnimation(duration - animatableElapsedTime.value)
            )
            onCompleted()
        } else {
            animatableElapsedTime.stop()
        }
    }

    LaunchedEffect(playbackState.elapsedTime) {
        if (playbackState.elapsedTime != null) { animatableElapsedTime.snapTo(playbackState.elapsedTime) }
        if (playbackState.playing != null && playbackState.elapsedTime != null && playbackState.playing) {
            animatableElapsedTime.animateTo(
                targetValue = duration,
                animationSpec = linearAnimation(duration - playbackState.elapsedTime)
            )
            onCompleted()
        }
    }
}

@Composable
private fun _Player(
    enabled: StateFlow<Boolean>,
    playbackState: PlaybackState,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onAlbumClick: (AlbumId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onValueChange: (Duration) -> Unit,
    onPreviousClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: (PlaybackState.RepeatState) -> Unit,
    onShuffleClick: (Boolean) -> Unit
) {
    val enabled by enabled.collectAsState()
    val duration = playbackState.track?.duration ?: 0.milliseconds
    var addTrackToPlaylistDialogVisible by remember { mutableStateOf(false) }
    var addTrackToFolderDialogVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier.padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(modifier = Modifier.size(170.dp), url = playbackState.track?.images?.preferablyMedium())
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (playbackState.track != null) {
                    Text(
                        text = playbackState.track.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    TextButton(
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Album, null)
                                Text(
                                    text = playbackState.track.album?.name ?: "",
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        onClick = { if (playbackState.track.album != null) { onAlbumClick(playbackState.track.album.id) } }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (artist in playbackState.track.artists) {
                            TextButton(
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Person, null)
                                        Text(
                                            text = artist.name ?: "",
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                onClick = { onArtistClick(artist.id) }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${playbackState.elapsedTime.formatted()}/${duration.formatted()}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    IconButton(
                        content = { Icon(Icons.Default.SkipPrevious, null) },
                        enabled = enabled,
                        onClick = onPreviousClick
                    )
                    when (playbackState.playing) {
                        true -> IconButton(
                            content = { Icon(Icons.Default.PauseCircle, null) },
                            enabled = enabled,
                            onClick = onPauseClick
                        )
                        false, null -> IconButton(
                            content = { Icon(Icons.Default.PlayCircle, null) },
                            enabled = enabled,
                            onClick = onPlayClick
                        )
                    }
                    IconButton(
                        content = { Icon(Icons.Default.SkipNext, null) },
                        enabled = enabled,
                        onClick = onNextClick
                    )
                    IconButton(
                        content = { Icon(Icons.Default.Save, null) },
                        enabled = enabled,
                        onClick = { addTrackToPlaylistDialogVisible = true }
                    )
                    IconButton(
                        content = { Icon(Icons.Default.Folder, null) },
                        enabled = enabled,
                        onClick = { addTrackToFolderDialogVisible = true }
                    )
                    when (playbackState.repeatState) {
                        PlaybackState.RepeatState.Off, null -> {
                            IconButton(
                                enabled = enabled,
                                onClick = { onRepeatClick(PlaybackState.RepeatState.List) },
                                content = { Icon(Icons.Default.Repeat, null) }
                            )
                        }
                        PlaybackState.RepeatState.List -> {
                            IconButton(
                                enabled = enabled,
                                onClick = { onRepeatClick(PlaybackState.RepeatState.Track) },
                                content = { Icon(Icons.Default.RepeatOn, null) }
                            )
                        }
                        PlaybackState.RepeatState.Track -> {
                            IconButton(
                                enabled = enabled,
                                onClick = { onRepeatClick(PlaybackState.RepeatState.Off) },
                                content = { Icon(Icons.Default.RepeatOneOn, null) }
                            )
                        }
                    }
                    when (playbackState.shuffleState) {
                        true, null -> {
                            IconButton(
                                enabled = enabled,
                                onClick = { onShuffleClick(false) },
                                content = { Icon(Icons.Default.ShuffleOn, null) }
                            )
                        }
                        false -> {
                            IconButton(
                                enabled = enabled,
                                onClick = { onShuffleClick(true) },
                                content = { Icon(Icons.Default.Shuffle, null) }
                            )
                        }
                    }
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        value = playbackState.elapsedTime?.inWholeMilliseconds?.toFloat() ?: 0f,
                        valueRange = 0f..duration.inWholeMilliseconds.toFloat(),
                        onValueChange = { onValueChange(it.toLong().milliseconds) }
                    )
                }
            }
        }
    }

    if (addTrackToPlaylistDialogVisible && playbackState.track != null) {
        AddTrackToPlaylistDialog(
            state = addTrackToPlaylistDialogState,
            track = Track(playbackState.track.id, playbackState.track.title, playbackState.track.images.preferablySmall()),
            onDismissRequest = { addTrackToPlaylistDialogVisible = false }
        )
    }

    if (addTrackToFolderDialogVisible && playbackState.track != null) {
        AddTrackToFolderDialog(
            track = Track(playbackState.track.id, playbackState.track.title, playbackState.track.images.preferablySmall()),
            state = addTrackToFolderDialogState,
            onDismissRequest = { addTrackToFolderDialogVisible = false  }
        )
    }
}

private fun Animatable(
    initialValue: Duration
): Animatable<Duration, AnimationVector1D> {
    return Animatable(
        initialValue = initialValue,
        typeConverter = TwoWayConverter(
            convertToVector = { AnimationVector1D(it.inWholeMilliseconds.toFloat()) },
            convertFromVector = { it.value.toLong().milliseconds }
        )
    )
}

private fun linearAnimation(duration: Duration): TweenSpec<Duration> {
    return tween(
        durationMillis = duration.inWholeMilliseconds.toInt(),
        easing = LinearEasing
    )
}