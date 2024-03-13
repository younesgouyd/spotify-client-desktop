package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.Playlists
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.SimplifiedPlaylist
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepo internal constructor(
    private val client: HttpClient,
) {
    /**
     * GET /me/playlists
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50
     * @param offset The index of the first playlist to return. Default: 0 (the first object). Maximum offset: 100.000. Use with limit to get the next set of playlists
     */
    suspend fun getCurrentUserPlaylists(limit: Int, offset: Int): List<SimplifiedPlaylist> {
        return withContext(Dispatchers.IO) {
            client.get("me/playlists") {
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
            }.body<dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.Playlist>().toModel()
        }
    }
}