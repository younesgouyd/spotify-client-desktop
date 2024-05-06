package dev.younesgouyd.apps.spotifyclient.desktop.main

import io.ktor.http.*
import org.json.JSONObject

sealed class Offset {
    data class Uri(val value: SpotifyUri?) : Offset() {
        companion object {
            fun initial(): Uri = Uri(null)

            fun fromUrl(url: String?): Uri? {
                return url?.let { Url(url).parameters["after"]?.let { Uri(SpotifyUri(it)) } }
            }
        }
    }

    data class Index(val value: Int): Offset() {
        companion object {
            fun initial(): Index = Index(0)

            fun fromUrl(url: String?): Index? {
                return url?.let { Url(url).parameters["offset"]?.toInt()?.let { Index(it) } }
            }
        }
    }

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