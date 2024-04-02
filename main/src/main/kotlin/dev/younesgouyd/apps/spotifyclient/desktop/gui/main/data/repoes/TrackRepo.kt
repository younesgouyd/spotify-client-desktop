package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.ArtistTopTracks
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.AlbumTracks
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.PlaylistTracks
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Artist
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /playlists/{id}/tracks
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50. Default: limit=20. Range: 0 - 50. Example: limit=10
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items. Default: offset=0. Example: offset=5
     */
    suspend fun getPlaylistTracks(playlistId: PlaylistId, limit: Int?, offset: Int?): List<Playlist.Track?> {
        return withContext(Dispatchers.IO) {
            client.get("playlists/$playlistId/tracks") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<PlaylistTracks>().toModel()
        }
    }

    /**
     * GET /albums/{id}/tracks
     * Get Spotify catalog information about an album’s tracks. Optional parameters can be used to limit the number
     * of tracks returned.
     */
    suspend fun getAlbumTracks(id: AlbumId, limit: Int?, offset: Int?): List<Album.Track> {
        return withContext(Dispatchers.IO) {
            client.get("albums/$id/tracks") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                parameter("limit", limit)
                parameter("offset", offset)
            }.body<AlbumTracks>().toModel()
        }
    }

    /**
     * GET /artists/{id}/top-tracks
     * Get Spotify catalog information about an artist's top tracks by country.
     */
    suspend fun getArtistTopTracks(id: ArtistId): List<Artist.Track> {
        return withContext(Dispatchers.IO) {
            client.get("artists/$id/top-tracks") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }.body<ArtistTopTracks>().toModel()
        }
    }
}
