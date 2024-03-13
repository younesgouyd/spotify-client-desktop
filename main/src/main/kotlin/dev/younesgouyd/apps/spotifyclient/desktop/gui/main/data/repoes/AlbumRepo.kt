package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.ArtistAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.SavedAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepo internal constructor(
    private val client: HttpClient,
) {
    /**
     * GET /me/albums
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items.
     */
    suspend fun getSavedAlbums(limit: Int, offset: Int): List<Album> {
        return withContext(Dispatchers.IO) {
            client.get("me/albums") {
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<SavedAlbums>().toModel()
        }
    }

    /**
     * GET /artists/{id}/albums
     * @param id The Spotify ID of the artist
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items
     */
    suspend fun getArtistAlbums(id: ArtistId, limit: Int, offset: Int): List<Album> {
        return withContext(Dispatchers.IO) {
            client.get("artists/$id/albums") {
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<ArtistAlbums>().toModel()
        }
    }

    /**
     * GET /albums/{id}
     * @param id The Spotify ID of the album
     */
    suspend fun getAlbum(id: AlbumId): Album {
        return withContext(Dispatchers.IO) {
            client.get("albums/$id").body<dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.Album>().toModel()
        }
    }
}
