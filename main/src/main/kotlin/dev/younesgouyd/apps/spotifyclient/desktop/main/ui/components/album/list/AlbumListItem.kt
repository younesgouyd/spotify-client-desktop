package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

data class AlbumListItem(
    val id: AlbumId,
    val name: String?,
    val images: Images,
    val releaseDate: String?,
    val totalTracks: Int?,
    val artists: List<Artist>,
    val genres: List<String>,
    val popularity: Int?
) {
    data class Artist(
        val id: ArtistId,
        val name: String?
    )
}