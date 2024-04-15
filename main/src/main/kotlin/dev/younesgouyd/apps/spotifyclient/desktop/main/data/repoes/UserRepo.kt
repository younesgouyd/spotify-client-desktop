package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.CurrentUser
import dev.younesgouyd.apps.spotifyclient.desktop.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /me
     * @param
     */
    suspend fun getCurrentUser(): User {
        return client.get("me") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<CurrentUser>().toModel()
    }

    /**
     * GET /users/{user_id}
     */
    suspend fun getUser(id: UserId): User {
        return client.get("users/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.User>().toModel()
    }
}