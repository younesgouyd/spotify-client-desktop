package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.artist

import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
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
     */
    suspend fun getCurrentUserFollowedArtists(after: Offset.Uri): FollowedArtists {
        return client.get("me/following") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            after.value?.let { parameter("after", it.id) }
            parameter("limit", 20)
        }.body<FollowedArtists>()
    }

    /**
     * GET /artists/{id}
     * @param id The Spotify ID of the artist
     */
    suspend fun get(id: ArtistId): Artist {
        return client.get("artists/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<Artist>()
    }

    suspend fun changeArtistFollowState(id: ArtistId, state: Boolean) {
        if (state) { followArtist(id) } else { unFollowArtist(id) }
    }

    suspend fun isArtistFollowed(id: ArtistId): Boolean {
        return client.get("me/following/contains") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("type", ID_TYPE)
            parameter("ids", "$id")
        }.body<List<Boolean>>().first()
    }

    suspend fun getRelatedArtists(id: ArtistId): RelatedArtists {
        return client.get("artists/$id/related-artists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<RelatedArtists>()
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
}