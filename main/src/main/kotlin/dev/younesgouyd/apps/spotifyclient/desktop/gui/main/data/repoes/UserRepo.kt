package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.CurrentUser
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.toModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /me
     * @param
     */
    suspend fun getCurrentUser(): dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.User {
        return withContext(Dispatchers.IO) {
            client.get("me") {
                header("Authorization", "Bearer ${authRepo.getToken()}")
            }.body<CurrentUser>().toModel()
        }
    }
}