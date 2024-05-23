package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.profile

import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId

data class User(
    val id: UserId,
    val displayName: String?,
    val followerCount: Int?,
    val profilePictureUrl: String?
)