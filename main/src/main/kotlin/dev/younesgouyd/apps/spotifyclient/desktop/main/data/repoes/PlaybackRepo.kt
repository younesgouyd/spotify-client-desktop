package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset.Companion.toJson
import dev.younesgouyd.apps.spotifyclient.desktop.main.SpotifyUri
import dev.younesgouyd.apps.spotifyclient.desktop.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaybackState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PlaybackRepo internal constructor(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /me/player
     */
    suspend fun getPlaybackState(): PlaybackState {
        return withContext(Dispatchers.IO) {
            client.get("me/player") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }.body<dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState>().toModel()
        }
    }

    /**
     * GET /me/player/play
     * @param contextUri Optional. Spotify URI of the context to play. Valid contexts are albums, artists & playlists.
     *        {context_uri:"spotify:album:1Je1IMUlBXcx1Fz0WE7oPT"}
     * @param uris Optional. A JSON array of the Spotify track URIs to play.
     *        For example: {"uris": ["spotify:track:4iV5W9uYEdYUVa79Axb7Rh", "spotify:track:1301WleyT98MSxVHPZCA6M"]}
     * @param offset Optional. Indicates from where in the context playback should start. Only available when
     *        context_uri corresponds to an album or playlist object "position" is zero based and can’t be negative.
     *        Example: "offset": {"position": 5} "uri" is a string representing the uri of the item to start at.
     *        Example: "offset": {"uri": "spotify:track:1301WleyT98MSxVHPZCA6M"}
     *        supports free form additional properties
     * @param positionMs integer
     */
    suspend fun play(
        contextUri: SpotifyUri?,
        uris: List<SpotifyUri>,
        offset: Offset?,
        positionMs: Long?
    ) {
        withContext(Dispatchers.IO) {
            client.put("me/player/play") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                header("Content-Type", "application/json")
                setBody<String>(
                    JSONObject().apply {
                        if (contextUri != null) put("context_uri", contextUri)
                        if (uris.isNotEmpty()) { put("uris", JSONArray().apply { for (uri in uris) put(uri) }) }
                        if (offset != null) put("offset", offset.toJson())
                        if (positionMs != null) put("position_ms", positionMs)
                    }.toString()
                )
            }
        }
    }

    /**
     * GET /me/player/pause
     */
    suspend fun pause() {
        withContext(Dispatchers.IO) {
            client.put("me/player/pause") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }
        }
    }

    /**
     * GET /me/player/next
     */
    suspend fun next() {
        withContext(Dispatchers.IO) {
            client.post("me/player/next") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }
        }
    }

    /**
     * GET /me/player/previous
     */
    suspend fun previous() {
        withContext(Dispatchers.IO) {
            client.post("me/player/previous") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }
        }
    }

    /**
     * GET /me/player/seek
     * @param positionMs The position in milliseconds to seek to. Must be a positive number. Passing in a position that is greater than the length of the track will cause the player to start playing the next song. Example: position_ms=25000
     */
    suspend fun seek(positionMs: Long) {
        withContext(Dispatchers.IO) {
            client.put("me/player/seek") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                parameter("position_ms", positionMs.toString())
            }
        }
    }

    /**
     * GET /me/player/repeat
     * @param state track, context or off.
     *        track will repeat the current track.
     *        context will repeat the current context.
     *        off will turn repeat off.
     *        Example: state=context
     */
    suspend fun repeat(state: dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState) {
        withContext(Dispatchers.IO) {
            client.put("me/player/repeat") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                parameter("state", state.name.lowercase())
            }
        }
    }

    /**
     * GET /me/player/shuffle
     * @param state true : Shuffle user's playback.
     *        false : Do not shuffle user's playback.
     *        Example: state=true
     */
    suspend fun shuffle(state: Boolean) {
        withContext(Dispatchers.IO) {
            client.put("me/player/shuffle") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
                parameter("state", state)
            }
        }
    }
}