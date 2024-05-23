package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth

import java.time.Instant

data class Token(
    val accessToken: String,
    val expiresIn: Int,
    val receivedAt: Long,
    val refreshToken: String
) {
    fun expired() = Instant.now().epochSecond >= receivedAt + expiresIn
}
