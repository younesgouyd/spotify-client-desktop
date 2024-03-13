package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.LoginResult
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
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class LoginRepo internal constructor(
    private val client: HttpClient,
) {
    companion object {
        var token: String? = null
    }

    suspend fun login(): LoginResult {
        val result = LoginDataSource.login(client)
        return if (result.isSuccess) {
            token = JSONObject(result.getOrThrow()).getString("access_token")
            LoginResult.Success
        } else LoginResult.Failure
    }

    internal object LoginDataSource {
        private const val DOMAIN = "accounts.spotify.com"
        private const val REDIRECT_URI_PORT = 5789
        private const val REDIRECT_URI = "http://localhost:$REDIRECT_URI_PORT/callback"
        private const val SCOPE = "playlist-read-private" +
                " playlist-read-collaborative" +
                " user-follow-read" +
                " user-top-read" +
                " user-library-read" +
                " user-read-email"

        private val clientId = System.getenv("SP_CLNT_ID")

        private val encodedClientId get() = URLEncoder.encode(clientId, Charsets.UTF_8)
        private val encodedRedirectUri get() =  URLEncoder.encode(REDIRECT_URI, Charsets.UTF_8)

        suspend fun login(client: HttpClient): Result<String> {
            return withContext(Dispatchers.IO) {
                val verifier = createVerifier()
                val challenge = createChallenge(verifier)

                val encodedScope = URLEncoder.encode(SCOPE, Charsets.UTF_8)
                val url = "https://$DOMAIN/authorize?" +
                        "client_id=$clientId" +
                        "&response_type=code" +
                        "&redirect_uri=$encodedRedirectUri" +
                        "&state=${UUID.randomUUID()}" +
                        "&scope=$encodedScope" +
                        "&show_dialog=false" +
                        "&code_challenge_method=S256" +
                        "&code_challenge=$challenge"

                Desktop.getDesktop().browse(URI(url))

                val code = waitForCallback()

                return@withContext getToken(client, code, verifier)
            }
        }

        private suspend fun waitForCallback(): String {
            var server: NettyApplicationEngine? = null

            val code = suspendCancellableCoroutine { continuation ->
                server = embeddedServer(Netty, port = REDIRECT_URI_PORT) {
                    routing {
                        get ("/callback") {
                            val code = call.parameters["code"] ?: throw RuntimeException("Received a response with no code")
                            if (call.parameters["state"] == null) {
                                throw RuntimeException("Received a response with no state")
                            }
                            call.respondText("OK")

                            continuation.resume(code)
                        }
                    }
                }.start(wait = false)
            }

            server!!.stop(1, 5, TimeUnit.SECONDS)

            return code
        }

        private suspend fun getToken(client: HttpClient, code: String, verifier: String): Result<String> {
            val response = client.post {
                url("https://$DOMAIN/api/token")
                header("Content-Type", "application/x-www-form-urlencoded")
                val body = "grant_type=authorization_code" +
                        "&code=${URLEncoder.encode(code, Charsets.UTF_8)}" +
                        "&redirect_uri=$encodedRedirectUri" +
                        "&client_id=$encodedClientId" +
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
