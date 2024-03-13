package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
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
            header("Authorization", "Bearer ${LoginRepo.token}")
        }
    }

    private val authClient = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val albumRepo by lazy { AlbumRepo(client) }
    val artistRepo by lazy { ArtistRepo(client) }
    val loginRepo by lazy { LoginRepo(authClient) }
    val playlistRepo by lazy { PlaylistRepo(client) }
    val trackRepo by lazy { TrackRepo(client) }
    val userRepo by lazy { UserRepo(client) }
}