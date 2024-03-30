package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET /me
 */
@Serializable
data class CurrentUser(
    @SerialName("display_name")
    val displayName: String?,
    val followers: Followers?,
    val id: UserId,
    val images: List<Image>?,
) {
    @Serializable
    data class Followers(
        val total: Int?
    )
}
