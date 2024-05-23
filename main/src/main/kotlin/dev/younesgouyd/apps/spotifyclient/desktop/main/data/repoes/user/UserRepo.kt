package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.user

import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.AppDataRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo,
    private val appDataRepo: AppDataRepo
) {
    companion object {
        private val mutex = Mutex()
    }

    suspend fun getCurrentUserId(): UserId {
        return mutex.withLock {
            val data = appDataRepo.getData()
            if (data.has("current_user_id")) {
                UserId(data.getString("current_user_id"))
            } else {
                data.put("current_user_id", getCurrentUser().id)
                appDataRepo.save(data)
                UserId(appDataRepo.getData().getString("current_user_id"))
            }
        }
    }

    /**
     * GET /me
     * @param
     */
    suspend fun getCurrentUser(): CurrentUser {
        return client.get("me") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<CurrentUser>()
    }

    /**
     * GET /users/{user_id}
     */
    suspend fun getUser(id: UserId): User {
        return client.get("users/$id") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
        }.body<User>()
    }
}