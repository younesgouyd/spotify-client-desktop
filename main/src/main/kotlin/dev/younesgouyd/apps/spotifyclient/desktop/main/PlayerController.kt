package dev.younesgouyd.apps.spotifyclient.desktop.main

import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.PlaybackRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

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
            _state.update { this.playbackRepo.getPlaybackState() }
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
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }

    suspend fun pause() {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.pause()
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }

    suspend fun seek(position: Duration) {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.seek(position.inWholeMilliseconds)
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }

    suspend fun next() {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.next()
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }

    suspend fun previous() {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.previous()
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }

    suspend fun repeat(repeatState: PlaybackState.RepeatState) {
        mutex.withLock {
            _enabled.update { false }
            val stateData = when (repeatState) {
                PlaybackState.RepeatState.Off -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Off
                PlaybackState.RepeatState.Track -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Track
                PlaybackState.RepeatState.List -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Context
            }
            this.playbackRepo.repeat(stateData)
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }

    suspend fun shuffle(state: Boolean) {
        mutex.withLock {
            _enabled.update { false }
            this.playbackRepo.shuffle(state)
            delay(DELAY)
            _state.update { this.playbackRepo.getPlaybackState() }
            _enabled.update { true }
        }
    }
}