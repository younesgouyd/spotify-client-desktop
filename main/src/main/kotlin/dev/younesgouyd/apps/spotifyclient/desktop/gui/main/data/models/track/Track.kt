package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.track

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.SpotifyUri
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.Album
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.artist.Artist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val album: Album,
    val artists: List<Artist>,
    @SerialName("disc_number")
    val dicNumber: Int,
    @SerialName("duration_ms")
    val durationMs: Int,
    val explicit: Boolean,
    val href: String,
    val id: TrackId,
    @SerialName("is_playable")
    val isPlayabale: Boolean,
    val name: String,
    val popularity: Int,
    @SerialName("preview_url")
    val previewUrl: String,
    @SerialName("track_number")
    val trackNumber: Int,
    val uri: SpotifyUri
)
