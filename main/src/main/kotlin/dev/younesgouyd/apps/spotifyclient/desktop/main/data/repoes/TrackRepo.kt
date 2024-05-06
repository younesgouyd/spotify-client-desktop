package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.ArtistTopTracks
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.AlbumTracks
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.PlaylistTracks
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Album
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Playlist
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TrackRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /playlists/{id}/tracks
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items. Default: offset=0. Example: offset=5
     */
    suspend fun getPlaylistTracks(playlistId: PlaylistId, offset: Offset.Index): LazilyLoadedItems.Page<Playlist.Track, Offset.Index> {
        return client.get("playlists/$playlistId/tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<PlaylistTracks>().toModel()
    }

    /**
     * GET /albums/{id}/tracks
     * Get Spotify catalog information about an album’s tracks. Optional parameters can be used to limit the number
     * of tracks returned.
     */
    suspend fun getAlbumTracks(id: AlbumId, offset: Offset.Index): LazilyLoadedItems.Page<Album.Track, Offset.Index> {
        return client.get("albums/$id/tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<AlbumTracks>().toModel()
    }

    /**
     * GET /artists/{id}/top-tracks
     * Get Spotify catalog information about an artist's top tracks by country.
     */
    suspend fun getArtistTopTracks(id: ArtistId): List<Artist.Track> {
        return client.get("artists/$id/top-tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<ArtistTopTracks>().toModel()
    }
}
