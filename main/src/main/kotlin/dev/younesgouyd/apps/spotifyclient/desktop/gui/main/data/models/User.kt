package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET users/{user_id}
 */
@Serializable
data class User(
    val id: UserId,
    @SerialName("display_name")
    val displayName: String,
    val followers: Followers?,
    val images: List<Image>?
) {
    @Serializable
    data class Followers(
        val total: Int?
    )
}
