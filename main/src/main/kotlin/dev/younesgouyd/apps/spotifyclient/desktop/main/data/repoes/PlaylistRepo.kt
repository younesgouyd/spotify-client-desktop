package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.CategoryPlaylists
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.Playlists
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.UserPlaylists
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.*
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
    suspend fun getCurrentUserPlaylists(offset: Offset.Index): LazilyLoadedItems.Page<PlaylistListItem, Offset.Index> {
        return client.get("me/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<Playlists>().toModel()
    }

    /**
     * GET /playlists/{playlist_id}
     * @param id The Spotify ID of the playlist
     */
    suspend fun get(id: PlaylistId): Playlist {
        val playlist = client.get("playlists/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.Playlist>()
        return playlist.toModel(followed = isPlaylistFollowedByCurrentUser(id))
    }

    /**
     * GET /users/{user_id}/playlists
     */
    suspend fun getUserPlaylists(userId: UserId, offset: Offset.Index): LazilyLoadedItems.Page<User.Playlist, Offset.Index> {
        return client.get("users/$userId/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<UserPlaylists>().toModel()
    }

    /**
     * GET /browse/categories/{category_id}/playlists
     */
    suspend fun getDiscoverPlaylists(offset: Offset.Index): LazilyLoadedItems.Page<PlaylistListItem, Offset.Index> {
        return client.get("browse/categories/discover/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<CategoryPlaylists>().toModel()
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

    suspend fun getPlaylistOptions(offset: Offset.Index): LazilyLoadedItems.Page<PlaylistOption, Offset.Index> {
        val owner = userRepo.getCurrentUser()
        val response = client.get("me/playlists") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<Playlists>()
        return LazilyLoadedItems.Page(
            nextOffset = Offset.Index.fromUrl(response.next),
            items = response.items?.let { items ->
                items.filterNotNull().filter { it.owner != null && it.owner.id == owner.id }.map {
                    PlaylistOption(
                        id = it.id,
                        name = it.name,
                        image = Images.fromStandardImages(it.images).preferablySmall()
                    )
                }
            } ?: emptyList()
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

    private suspend fun isPlaylistFollowedByCurrentUser(id: PlaylistId): Boolean {
        val currentUserId = userRepo.getCurrentUser().id
        return client.get("playlists/$id/followers/contains") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("ids", currentUserId)
        }.body<List<Boolean>>().first()
    }
}