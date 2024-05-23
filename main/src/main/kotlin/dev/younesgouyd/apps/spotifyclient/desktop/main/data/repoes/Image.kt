package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val url: String?,
    val height: Int?,
    val width: Int?
)