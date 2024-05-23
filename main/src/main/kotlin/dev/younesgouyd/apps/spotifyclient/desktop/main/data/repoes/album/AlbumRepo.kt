package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album

import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class AlbumRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /me/albums
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items.
     */
    suspend fun getSavedAlbums(offset: Offset.Index): SavedAlbums {
        return client.get("me/albums") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<SavedAlbums>()
    }

    /**
     * GET /artists/{id}/albums
     * @param id The Spotify ID of the artist
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items
     */
    suspend fun getArtistAlbums(id: ArtistId, offset: Offset.Index): ArtistAlbums {
        return client.get("artists/$id/albums") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<ArtistAlbums>()
    }

    /**
     * GET /albums/{id}
     * @param id The Spotify ID of the album
     */
    suspend fun getAlbum(id: AlbumId): Album {
        return client.get("albums/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<Album>()
    }

    suspend fun saveAlbum(id: AlbumId) {
        client.put("me/albums") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("ids", id)
        }
    }

    suspend fun removeAlbum(id: AlbumId) {
        client.delete("me/albums") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("ids", id)
        }
    }

    suspend fun isAlbumSaved(id: AlbumId): Boolean {
        return client.get("me/albums/contains") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("ids", id)
        }.body<List<Boolean>>().first()
    }
}
