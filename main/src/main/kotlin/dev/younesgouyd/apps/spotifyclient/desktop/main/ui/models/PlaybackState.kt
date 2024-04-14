package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class PlaybackState(
    val track: Track?,
    val elapsedTime: Duration?,
    val playing: Boolean?,
    val repeatState: RepeatState?,
    val shuffleState: Boolean?
) {
    companion object {
        fun empty() = PlaybackState(
            track = null,
            elapsedTime = 0.milliseconds,
            playing = false,
            repeatState = RepeatState.Off,
            shuffleState = false
        )
    }

    data class Track(
        val id: TrackId,
        val title: String?,
        val artists: List<Artist>,
        val album: Album?,
        val images: Images,
        val duration: Duration?
    ) {
        data class Artist(
            val id: ArtistId,
            val name: String?
        )

        data class Album(
            val id: AlbumId,
            val name: String?
        )
    }

    enum class RepeatState {
        Off, Track, List
    }
}