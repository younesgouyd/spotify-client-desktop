package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /albums/{id}
 */
@Serializable
data class Album(
    val id: AlbumId,
    val images: List<Image>?,
    val name: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    val artists: List<Artist>?,
    val genres: List<String>?,
    val popularity: Int?
) {
    @Serializable
    data class Artist(
        val id: ArtistId,
        val name: String?
    )
}
