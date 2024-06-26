package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.sqldelight.FolderTrackCrossRefQueries
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class TrackRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo,
    private val folderTrackCrossRefQueries: FolderTrackCrossRefQueries
) {
    /**
     * GET /playlists/{id}/tracks
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items. Default: offset=0. Example: offset=5
     */
    suspend fun getPlaylistTracks(playlistId: PlaylistId, offset: Offset.Index): PlaylistTracks {
        return client.get("playlists/$playlistId/tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<PlaylistTracks>()
    }

    /**
     * GET /albums/{id}/tracks
     * Get Spotify catalog information about an album’s tracks. Optional parameters can be used to limit the number
     * of tracks returned.
     */
    suspend fun getAlbumTracks(id: AlbumId, offset: Offset.Index): AlbumTracks {
        return client.get("albums/$id/tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("limit", 20)
            parameter("offset", offset.value)
        }.body<AlbumTracks>()
    }

    /**
     * GET /artists/{id}/top-tracks
     * Get Spotify catalog information about an artist's top tracks by country.
     */
    suspend fun getArtistTopTracks(id: ArtistId): ArtistTopTracks {
        return client.get("artists/$id/top-tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<ArtistTopTracks>()
    }

    private suspend fun getSeveralTracks(ids: List<TrackId>): Tracks {
        return client.get("tracks") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("ids", ids.joinToString(",") { it.value })
        }.body<Tracks>()
    }

    /**
     * Get Several Tracks
     * GET /tracks
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFolderTracks(folderId: FolderId): Flow<Tracks?> {
        return folderTrackCrossRefQueries.selectFolderTrackIds(folderId.value)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .mapLatest { it.map { TrackId(it) } }
            .mapLatest { ids ->
                if (ids.isNotEmpty()) {
                    getSeveralTracks(ids)
                } else {
                    null
                }
            }
    }
}
