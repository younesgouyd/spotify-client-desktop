package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaybackState(
    @SerialName("repeat_state")
    val repeatState: RepeatState?,
    @SerialName("shuffle_state")
    val shuffleState: Boolean?,
    val timestamp: Long?,
    @SerialName("progress_ms")
    val progressMs: Long?,
    @SerialName("is_playing")
    val isPlaying: Boolean?,
    val item: TrackObject?,
) {
    @Serializable
    enum class RepeatState {
        @SerialName("off") Off,
        @SerialName("track") Track,
        @SerialName("context") Context
    }

    @Serializable
    data class TrackObject(
        val album: Album?,
        val artists: List<Artist?>?,
        @SerialName("duration_ms")
        val durationMs: Long?,
        val id: TrackId,
        val name: String?
    ) {
        @Serializable
        data class Album(
            val id: AlbumId,
            val images: List<Image>?,
            val name: String?
        )

        @Serializable
        data class Artist(
            val id: ArtistId,
            val name: String?
        )
    }
}
