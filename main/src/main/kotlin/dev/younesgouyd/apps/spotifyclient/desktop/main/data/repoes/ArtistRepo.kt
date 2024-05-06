package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.artist.FollowedArtists
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ArtistRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    companion object {
        const val ID_TYPE = "artist"
    }

    /**
     * GET /me/following
     *
     * @param after The last artist ID retrieved from the previous request
     */
    suspend fun getCurrentUserFollowedArtists(after: Offset.Uri): LazilyLoadedItems.Page<Artist, Offset.Uri> {
        return client.get("me/following") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            after.value?.let { parameter("after", it.id) }
            parameter("limit", 20)
        }.body<FollowedArtists>().toModel()
    }

    /**
     * GET /artists/{id}
     * @param id The Spotify ID of the artist
     */
    suspend fun get(id: ArtistId): Artist {
        val artist = client.get("artists/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.artist.Artist>()
        return artist.toModel(followed = isArtistFollowed(id))
    }

    suspend fun changeArtistFollowState(id: ArtistId, state: Boolean) {
        if (state) { followArtist(id) } else { unFollowArtist(id) }
    }

    private suspend fun followArtist(id: ArtistId) {
        client.put("me/following") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            parameter("ids", "$id")
        }
    }

    private suspend fun unFollowArtist(id: ArtistId) {
        client.delete("me/following") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            parameter("ids", "$id")
        }
    }

    private suspend fun isArtistFollowed(id: ArtistId): Boolean {
        return client.get("me/following/contains") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            parameter("ids", "$id")
        }.body<List<Boolean>>().first()
    }
}