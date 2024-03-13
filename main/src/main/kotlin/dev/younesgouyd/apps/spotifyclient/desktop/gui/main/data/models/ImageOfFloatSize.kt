package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageOfFloatSize(
    val url: String,
    val height: Float?,
    val width: Float?
)
