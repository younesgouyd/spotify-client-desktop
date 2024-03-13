package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.user

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.SpotifyUri
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Followers
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentUser(
    @SerialName("display_name")
    val displayName: String?,
    val email: String,
    val followers: Followers,
    val href: String,
    val id: UserId,
    val images: List<Image>,
    val uri: SpotifyUri
)
