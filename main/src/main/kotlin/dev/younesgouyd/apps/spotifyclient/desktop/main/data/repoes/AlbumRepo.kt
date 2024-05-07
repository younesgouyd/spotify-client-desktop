package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.ArtistAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.SavedAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Album
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.AlbumListItem
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
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
    suspend fun getSavedAlbums(offset: Offset.Index): LazilyLoadedItems.Page<AlbumListItem, Offset.Index> {
        return client.get("me/albums") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<SavedAlbums>().toModel()
    }

    /**
     * GET /artists/{id}/albums
     * @param id The Spotify ID of the artist
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items
     */
    suspend fun getArtistAlbums(id: ArtistId, offset: Offset.Index): LazilyLoadedItems.Page<Artist.Album, Offset.Index> {
        return client.get("artists/$id/albums") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<ArtistAlbums>().toModel()
    }

    /**
     * GET /albums/{id}
     * @param id The Spotify ID of the album
     */
    suspend fun getAlbum(id: AlbumId): Album {
        return client.get("albums/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.Album>().toModel()
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
