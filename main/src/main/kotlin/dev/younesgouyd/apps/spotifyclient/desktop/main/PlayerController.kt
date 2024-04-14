package dev.younesgouyd.apps.spotifyclient.desktop.main

import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration

class PlayerController(repoStore: RepoStore) {
    companion object { private const val DELAY = 1000L }

    private val repo = repoStore.playbackRepo
    private val _state: MutableStateFlow<PlaybackState> = MutableStateFlow(PlaybackState.empty())
    private val _enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val state: StateFlow<PlaybackState> get() = _state.asStateFlow()
    val enabled: StateFlow<Boolean> get() = _enabled.asStateFlow()

    suspend fun refresh() {
        _state.update { repo.getPlaybackState() }
    }

    suspend fun play(
        contextUri: SpotifyUri? = null,
        uris: List<SpotifyUri> = emptyList(),
        offset: Offset? = null,
        positionMs: Long? = null
    ) {
        _enabled.update { false }
        repo.play(
            contextUri = contextUri,
            uris = uris,
            offset = offset,
            positionMs = positionMs
        )
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }

    suspend fun pause() {
        _enabled.update { false }
        repo.pause()
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }

    suspend fun seek(position: Duration) {
        _enabled.update { false }
        repo.seek(position.inWholeMilliseconds)
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }

    suspend fun next() {
        _enabled.update { false }
        repo.next()
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }

    suspend fun previous() {
        _enabled.update { false }
        repo.previous()
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }

    suspend fun repeat(repeatState: PlaybackState.RepeatState) {
        _enabled.update { false }
        val stateData = when (repeatState) {
            PlaybackState.RepeatState.Off -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Off
            PlaybackState.RepeatState.Track -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Track
            PlaybackState.RepeatState.List -> dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Context
        }
        repo.repeat(stateData)
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }

    suspend fun shuffle(state: Boolean) {
        _enabled.update { false }
        repo.shuffle(state)
        delay(DELAY)
        refresh()
        _enabled.update { true }
    }
}