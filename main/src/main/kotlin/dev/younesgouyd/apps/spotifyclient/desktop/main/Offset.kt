package dev.younesgouyd.apps.spotifyclient.desktop.main

import org.json.JSONObject

sealed class Offset {
    data class Uri(val value: SpotifyUri) : Offset()

    data class Index(val value: Int): Offset()

    companion object {
        fun Offset.toJson(): JSONObject {
            return JSONObject().apply {
                when (this@toJson) {
                    is Uri -> put("uri", this@toJson.value)
                    is Index -> put("position", this@toJson.value)
                }
            }
        }
    }
}