package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.Playlists
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.PlaylistListItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /me/playlists
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50
     * @param offset The index of the first playlist to return. Default: 0 (the first object). Maximum offset: 100.000. Use with limit to get the next set of playlists
     */
    suspend fun getCurrentUserPlaylists(limit: Int, offset: Int): List<PlaylistListItem?> {
        return withContext(Dispatchers.IO) {
            client.get("me/playlists") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<Playlists>().toModel()
        }
    }

    /**
     * GET /playlists/{playlist_id}
     * @param playlistId The Spotify ID of the playlist
     */
    suspend fun get(playlistId: PlaylistId): Playlist {
        return withContext(Dispatchers.IO) {
            client.get("playlists/$playlistId") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }.body<dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.Playlist>().toModel()
        }
    }
}