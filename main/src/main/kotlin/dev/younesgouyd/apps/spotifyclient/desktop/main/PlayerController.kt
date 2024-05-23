package dev.younesgouyd.apps.spotifyclient.desktop.main

import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlayerController(private val playbackRepo: PlaybackRepo) {
    companion object { private const val DELAY = 1000L }

    private val mutex = Mutex()
    private val _state: MutableStateFlow<Result<PlaybackState>> = MutableStateFlow(Result.success(PlaybackState.empty()))
    private val _enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val state: StateFlow<Result<PlaybackState>> get() = _state.asStateFlow()
    val enabled: StateFlow<Boolean> get() = _enabled.asStateFlow()

    suspend fun refresh() {
        mutex.withLock {
            _enabled.update { false }
            _state.update { playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun play(
        contextUri: SpotifyUri? = null,
        uris: List<SpotifyUri> = emptyList(),
        offset: Offset? = null,
        positionMs: Long? = null
    ) {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.play(
                contextUri = contextUri,
                uris = uris,
                offset = offset,
                positionMs = positionMs
            )
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun pause() {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.pause()
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun seek(position: Duration) {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.seek(position.inWholeMilliseconds)
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun next() {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.next()
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun previous() {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.previous()
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun repeat(repeatState: PlaybackState.RepeatState) {
        mutex.withLock {
            _enabled.update { false }
            val stateData = when (repeatState) {
                PlaybackState.RepeatState.Off -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState.RepeatState.Off
                PlaybackState.RepeatState.Track -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState.RepeatState.Track
                PlaybackState.RepeatState.List -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState.RepeatState.Context
            }
            this.playbackRepo.repeat(stateData)
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    suspend fun shuffle(state: Boolean) {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.shuffle(state)
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState().toModel() }
            _enabled.update { true }
        }
    }

    private fun Result<dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState>.toModel(): Result<PlaybackState> {
        return this.map { data ->
            PlaybackState(
                track = data.item?.let { trackObject ->
                    PlaybackState.Track(
                        id = trackObject.id,
                        title = trackObject.name,
                        artists = trackObject.artists?.filterNotNull()?.map {
                            PlaybackState.Track.Artist(
                                id = it.id,
                                name = it.name
                            )
                        } ?: emptyList(),
                        album = trackObject.album?.let {
                            PlaybackState.Track.Album(
                                id = it.id,
                                name = it.name
                            )
                        },
                        images = trackObject.album?.images?.toImages() ?: Images.empty(),
                        duration = trackObject.durationMs?.milliseconds
                    )
                },
                elapsedTime = data.progressMs?.milliseconds,
                playing = data.isPlaying,
                repeatState = when (data.repeatState) {
                    dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState.RepeatState.Off -> PlaybackState.RepeatState.Off
                    dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState.RepeatState.Track -> PlaybackState.RepeatState.Track
                    dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackState.RepeatState.Context -> PlaybackState.RepeatState.List
                    null -> null
                },
                shuffleState = data.shuffleState
            )
        }
    }
}