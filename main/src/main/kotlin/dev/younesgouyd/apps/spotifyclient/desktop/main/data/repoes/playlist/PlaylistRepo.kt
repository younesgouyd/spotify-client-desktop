package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playlist

import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.SpotifyUri
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.user.UserRepo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.json.JSONArray
import org.json.JSONObject

class PlaylistRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
) {
    /**
     * GET /me/playlists
     * @param offset The index of the first playlist to return. Default: 0 (the first object). Maximum offset: 100.000. Use with limit to get the next set of playlists
     */
    suspend fun getCurrentUserPlaylists(offset: Offset.Index): Playlists {
        return client.get("me/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<Playlists>()
    }

    /**
     * GET /playlists/{playlist_id}
     * @param id The Spotify ID of the playlist
     */
    suspend fun get(id: PlaylistId): Playlist {
        return client.get("playlists/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<Playlist>()
    }

    /**
     * GET /users/{user_id}/playlists
     */
    suspend fun getUserPlaylists(userId: UserId, offset: Offset.Index): UserPlaylists {
        return client.get("users/$userId/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<UserPlaylists>()
    }

    /**
     * GET /browse/categories/{category_id}/playlists
     */
    suspend fun getDiscoverPlaylists(offset: Offset.Index): CategoryPlaylists {
        return client.get("browse/categories/discover/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<CategoryPlaylists>()
    }

    suspend fun changePlaylistFollowState(id: PlaylistId, state: Boolean) {
        if (state) { followPlaylist(id) } else { unfollowPlaylist(id) }
    }

    suspend fun addItems(id: PlaylistId, uris: List<SpotifyUri>) {
        client.post("playlists/$id/tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            contentType(ContentType.Application.Json)
            setBody(
                JSONObject().put("uris", JSONArray().apply { uris.forEach(::put) }).toString()
            )
        }
    }

    data class PlaylistOptions(
        val items: List<PlaylistOption>,
        val nextOffset: Offset.Index?
    ) {
        data class PlaylistOption(
            val id: PlaylistId,
            val name: String?,
            val images: List<Image>?
        )
    }

    suspend fun getPlaylistOptions(offset: Offset.Index): PlaylistOptions {
        val currentUserId = userRepo.getCurrentUserId()
        val response = client.get("me/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<Playlists>()
        return PlaylistOptions(
            items = response.items?.let { items ->
                items.filterNotNull().filter { it.owner != null && it.owner.id == currentUserId }.map {
                    PlaylistOptions.PlaylistOption(
                        id = it.id,
                        name = it.name,
                        images = it.images
                    )
                }
            } ?: emptyList(),
            nextOffset = Offset.Index.fromUrl(response.next)
        )
    }

    private suspend fun followPlaylist(id: PlaylistId) {
        client.put("playlists/$id/followers") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            contentType(ContentType.Application.Json)
            setBody(JSONObject().put("public", false).toString())
        }
    }

    private suspend fun unfollowPlaylist(id: PlaylistId) {
        client.delete("playlists/$id/followers") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }
    }

    suspend fun isPlaylistFollowedByCurrentUser(id: PlaylistId): Boolean {
        val currentUserId = userRepo.getCurrentUserId()
        return client.get("playlists/$id/followers/contains") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("ids", currentUserId)
        }.body<List<Boolean>>().first()
    }
}