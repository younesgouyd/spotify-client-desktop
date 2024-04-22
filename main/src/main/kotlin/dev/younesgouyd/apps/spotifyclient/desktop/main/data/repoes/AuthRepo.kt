package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.LoginResult
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.Token
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.awt.Desktop
import java.math.BigInteger
import java.net.URI
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class AuthRepo(
    private val httpClient: HttpClient,
    private val appDataRepo: AppDataRepo
) {
    private var token: Token? = null
    private val mutex = Mutex()

    suspend fun isAuthorized(): Boolean {
        try {
            loadTokenFromCache()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun getToken(): String {
        mutex.withLock {
            if (token == null) {
                loadTokenFromCache()
            }
            if (token!!.expired()) {
                refreshToken()
                loadTokenFromCache()
            }
            return token!!.accessToken
        }
    }

    suspend fun login(spotifyClientId: String): LoginResult {
        val result = LoginDataSource.login(httpClient, spotifyClientId)
        return if (result.isSuccess) {
            val tokenJson = JSONObject(result.getOrThrow())
                .put("received_at", Instant.now().epochSecond)
            token = Token(
                accessToken = tokenJson.getString("access_token"),
                expiresIn = tokenJson.getInt("expires_in"),
                receivedAt = tokenJson.getLong("received_at"),
                refreshToken = tokenJson.getString("refresh_token")
            )
            val data = JSONObject()
                .put("client_id", spotifyClientId)
                .put("token", tokenJson)
            appDataRepo.save(data)
            LoginResult.Success
        } else LoginResult.Failure
    }

    suspend fun logout() {
        token = null
        appDataRepo.clear()
    }

    suspend fun refreshTokenIfNeeded() {
        token.let {
            if (it == null || it.expired()) {
                refreshToken()
            }
        }
    }

    private suspend fun refreshToken() {
        val data = appDataRepo.getData()
        val newTokenJson = LoginDataSource.getNewToken(
            httpClient = httpClient,
            spotifyClientId = data.getString("client_id"),
            refreshToken = data.getJSONObject("token").getString("refresh_token")
        ).put("received_at", Instant.now().epochSecond)
        data.put("token", newTokenJson)
        appDataRepo.save(data)
    }

    private suspend fun loadTokenFromCache() {
        val tokenJson = appDataRepo.getData().getJSONObject("token")
        token = Token(
            accessToken = tokenJson.getString("access_token"),
            expiresIn = tokenJson.getInt("expires_in"),
            receivedAt = tokenJson.getLong("received_at"),
            refreshToken = tokenJson.getString("refresh_token")
        )
    }

    private object LoginDataSource {
        private const val DOMAIN = "accounts.spotify.com"
        private const val REDIRECT_URI_PORT = 5789
        private const val REDIRECT_URI = "http://localhost:$REDIRECT_URI_PORT/callback"
        private const val SCOPE = "playlist-read-private" +
                " playlist-read-collaborative" +
                " user-follow-read" +
                " user-top-read" +
                " user-library-read" +
                " user-read-email" +
                " user-read-playback-state" +
                " user-read-currently-playing" +
                // write-access
                " user-follow-modify" +
                " user-modify-playback-state"

        private val encodedRedirectUri get() =  URLEncoder.encode(REDIRECT_URI, Charsets.UTF_8)

        suspend fun login(httpClient: HttpClient, spotifyClientId: String): Result<String> {
            return withContext(Dispatchers.IO) {
                val verifier = createVerifier()
                val challenge = createChallenge(verifier)

                val encodedScope = URLEncoder.encode(SCOPE, Charsets.UTF_8)
                val state = BigInteger(130, SecureRandom()).toString(32)
                val url = "https://$DOMAIN/authorize?" +
                        "client_id=$spotifyClientId" +
                        "&response_type=code" +
                        "&redirect_uri=$encodedRedirectUri" +
                        "&state=$state" +
                        "&scope=$encodedScope" +
                        "&show_dialog=false" +
                        "&code_challenge_method=S256" +
                        "&code_challenge=$challenge"

                var server: NettyApplicationEngine? = null

                val code = suspendCancellableCoroutine { continuation ->
                    server = embeddedServer(Netty, port = REDIRECT_URI_PORT) {
                        routing {
                            get ("/callback") {
                                val code = call.parameters["code"] ?: throw RuntimeException("Received a response with no code")
                                val receivedState = call.parameters["state"]
                                if (receivedState == null) {
                                    throw RuntimeException("Received a response with no state")
                                } else if (receivedState != state) {
                                    throw RuntimeException("Received state doesn't match sent state")
                                }
                                call.respondText("OK")

                                continuation.resume(code)
                            }
                        }
                    }.start(wait = false)
                    Desktop.getDesktop().browse(URI(url))
                }

                server!!.stop(1, 5, TimeUnit.SECONDS)

                return@withContext getToken(
                    httpClient = httpClient,
                    spotifyClientId = spotifyClientId,
                    code = code,
                    verifier = verifier
                )
            }
        }

        suspend fun getNewToken(httpClient: HttpClient, spotifyClientId: String, refreshToken: String): JSONObject {
            return JSONObject(
                httpClient.post {
                    url("https://$DOMAIN/api/token")
                    header("Content-Type", "application/x-www-form-urlencoded")
                    parameter("grant_type", "refresh_token")
                    parameter("refresh_token", refreshToken)
                    parameter("client_id", spotifyClientId)
                }.bodyAsText()
            )
        }

        private suspend fun getToken(httpClient: HttpClient, spotifyClientId: String, code: String, verifier: String): Result<String> {
            val response = httpClient.post {
                url("https://$DOMAIN/api/token")
                header("Content-Type", "application/x-www-form-urlencoded")
                val body = "grant_type=authorization_code" +
                        "&code=${URLEncoder.encode(code, Charsets.UTF_8)}" +
                        "&redirect_uri=$encodedRedirectUri" +
                        "&client_id=$spotifyClientId" +
                        "&code_verifier=${URLEncoder.encode(verifier, Charsets.UTF_8)}"
                setBody(body)
            }

            return try {
                val body = response.bodyAsText()
                if (response.status.isSuccess()) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body))
                }
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }

        private fun createVerifier(): String {
            val sr = SecureRandom()
            val code = ByteArray(32)
            sr.nextBytes(code)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(code)
        }

        private fun createChallenge(verifier: String): String {
            val bytes: ByteArray = verifier.toByteArray(Charsets.US_ASCII)
            val md = MessageDigest.getInstance("SHA-256")
            md.update(bytes, 0, bytes.size)
            val digest = md.digest()
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
        }
    }
}
