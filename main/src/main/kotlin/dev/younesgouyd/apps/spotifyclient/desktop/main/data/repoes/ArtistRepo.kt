package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.artist.FollowedArtists
import dev.younesgouyd.apps.spotifyclient.desktop.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

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
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50
     */
    suspend fun getCurrentUserFollowedArtists(after: ArtistId?, limit: Int?): List<Artist> {
        return client.get("me/following") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            parameter("after", after)
            parameter("limit", limit)
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