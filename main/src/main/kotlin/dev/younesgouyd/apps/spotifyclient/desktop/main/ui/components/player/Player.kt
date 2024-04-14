package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Player(
    modifier: Modifier = Modifier,
    state: PlayerState
) {
    Player(
        modifier = modifier,
        enabled = state.enabled,
        playbackState = state.playbackState,
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
    modifier: Modifier = Modifier,
    enabled: Boolean,
    playbackState: PlaybackState,
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
        modifier = modifier,
        enabled = enabled,
        playbackState = playbackState.copy(elapsedTime = animatableElapsedTime.value),
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
    modifier: Modifier = Modifier,
    enabled: Boolean,
    playbackState: PlaybackState,
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
    val duration = playbackState.track?.duration ?: 0.milliseconds

    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier.padding(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LaunchedEffect(playbackState.track?.images) {
                println("new playback state: ${playbackState.track?.images}")
            }
            Image(modifier = Modifier.size(170.dp), url = playbackState.track?.images?.preferablyMedium())
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (playbackState.track != null) {
                    Text(playbackState.track.title ?: "", style = MaterialTheme.typography.titleMedium)
                    TextButton(
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Album, null)
                                Text(text = playbackState.track.album?.name ?: "", style = MaterialTheme.typography.labelMedium)
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
                                content = { Text(text = artist.name ?: "", style = MaterialTheme.typography.labelMedium) },
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
                        enabled = enabled,
                        onClick = onPreviousClick,
                        content = { Icon(Icons.Default.SkipPrevious, null) }
                    )
                    when (playbackState.playing) {
                        true -> IconButton(
                            enabled = enabled,
                            onClick = onPauseClick,
                            content = { Icon(Icons.Default.PauseCircle, null) }
                        )
                        false, null -> IconButton(
                            enabled = enabled,
                            onClick = onPlayClick,
                            content = { Icon(Icons.Default.PlayCircle, null) }
                        )
                    }
                    IconButton(
                        enabled = enabled,
                        onClick = onNextClick,
                        content = { Icon(Icons.Default.SkipNext, null) }
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
}

private fun Duration?.formatted(): String {
    return this?.let {
        it.toComponents { minutes, seconds, _ ->
            minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
        }
    } ?: ""
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