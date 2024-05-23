package dev.younesgouyd.apps.spotifyclient.desktop.main.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.AppDataRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.FolderRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.SettingsRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.album.AlbumRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.artist.ArtistRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playback.PlaybackRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.playlist.PlaylistRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.search.SearchRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.track.TrackRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.user.UserRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.sqldelight.SpotifyClientDesktop
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class RepoStore {
    private lateinit var client: HttpClient
    private lateinit var authClient: HttpClient
    private lateinit var database: SpotifyClientDesktop

    private lateinit var appDataRepo: AppDataRepo
    lateinit var settingsRepo: SettingsRepo private set
    lateinit var authRepo: AuthRepo private set
    lateinit var userRepo: UserRepo private set
    lateinit var albumRepo: AlbumRepo private set
    lateinit var artistRepo: ArtistRepo private set
    lateinit var playlistRepo: PlaylistRepo private set
    lateinit var trackRepo: TrackRepo private set
    lateinit var playbackRepo: PlaybackRepo private set
    lateinit var searchRepo: SearchRepo private set
    lateinit var folderRepo: FolderRepo private set

    suspend fun init() {
        client = HttpClient(CIO) {
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

        authClient = HttpClient(CIO) {
            install(Logging) { level = LogLevel.ALL }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val driver = JdbcSqliteDriver(
            url = "jdbc:sqlite:data.db",
            properties = Properties().apply { put("foreign_keys", "true") }
        )
        if (!File("data.db").exists()) {
            SpotifyClientDesktop.Schema.create(driver)
        }
        database = SpotifyClientDesktop(driver)

        appDataRepo = AppDataRepo()
        settingsRepo = SettingsRepo(appDataRepo)
        authRepo = AuthRepo(authClient, appDataRepo)
        userRepo = UserRepo(client, authRepo, appDataRepo)
        albumRepo = AlbumRepo(client, authRepo)
        artistRepo = ArtistRepo(client, authRepo)
        playlistRepo = PlaylistRepo(client, authRepo, userRepo)
        trackRepo = TrackRepo(client, authRepo, database.folderTrackCrossRefQueries)
        playbackRepo = PlaybackRepo(client, authRepo)
        searchRepo = SearchRepo(client, authRepo)
        folderRepo = FolderRepo(database.folderQueries, database.folderTrackCrossRefQueries)

        appDataRepo.init()
        settingsRepo.init()
    }
}
