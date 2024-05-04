package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.CurrentUser
import dev.younesgouyd.apps.spotifyclient.desktop.main.toModel
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject

class UserRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo,
    private val appDataRepo: AppDataRepo
) {
    companion object {
        private val mutex = Mutex()
    }

    /**
     * GET /me
     * @param
     */
    suspend fun getCurrentUser(): User {
        mutex.withLock {
            var data = appDataRepo.getData()
            if (!data.has("current_user")) {
                val response = client.get("me") {
                    header("Authorization", "Bearer ${authRepo.getToken()}")
                }.body<CurrentUser>().toModel()
                data.put(
                    "current_user",
                    JSONObject().apply {
                        put("id", response.id.value)
                        put("display_name", response.displayName ?: JSONObject.NULL)
                        put("follower_count", response.followerCount ?: JSONObject.NULL)
                        put("profile_picture_url", response.profilePictureUrl ?: JSONObject.NULL)
                    }
                )
                appDataRepo.save(data)
                data = appDataRepo.getData()
            }
            val json = data.getJSONObject("current_user")
            return User(
                id = UserId(json.getString("id")),
                displayName = if (!json.isNull("display_name")) json.getString("display_name") else null,
                followerCount = if (!json.isNull("follower_count")) json.getInt("follower_count") else null,
                profilePictureUrl = if (!json.isNull("profile_picture_url")) json.getString("profile_picture_url") else null
            )
        }
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