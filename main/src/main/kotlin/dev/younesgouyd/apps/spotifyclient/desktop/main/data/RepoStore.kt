package dev.younesgouyd.apps.spotifyclient.desktop.main.data

import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RepoStore {
    private val client = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.spotify.com"
                path("v1/")
            }
        }
    }

    private val authClient = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val appDataRepo by lazy { AppDataRepo() }
    val settingsRepo by lazy { SettingsRepo(appDataRepo) }
    val authRepo by lazy { AuthRepo(authClient, appDataRepo) }
    val albumRepo by lazy { AlbumRepo(client, authRepo) }
    val artistRepo by lazy { ArtistRepo(client, authRepo) }
    val playlistRepo by lazy { PlaylistRepo(client, authRepo) }
    val trackRepo by lazy { TrackRepo(client, authRepo) }
    val userRepo by lazy { UserRepo(client, authRepo) }
    val playbackRepo by lazy { PlaybackRepo(client, authRepo) }
}
