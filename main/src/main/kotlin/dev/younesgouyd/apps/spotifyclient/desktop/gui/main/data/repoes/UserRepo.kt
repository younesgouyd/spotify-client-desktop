package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.user.CurrentUser
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.user.User
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.toModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo internal constructor(
    private val client: HttpClient
) {
    /**
     * GET /me
     * @param
     */
    suspend fun getCurrentUser(): dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.User {
        return withContext(Dispatchers.IO) {
            client.get("me") {
            }.body<CurrentUser>().toModel()
        }
    }

    /**
     * GET /users/{user_id}
     * @param userId The user's Spotify user ID
     */
    suspend fun get(userId: UserId): User {
        return client.get("/users/$userId").body<User>()
    }
}