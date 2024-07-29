package dev.younesgouyd.apps.spotifyclient.desktop.main.ui

import kotlin.time.Duration

fun Duration?.formatted(): String {
    return this?.let {
        it.toComponents { minutes, seconds, _ ->
            minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
        }
    } ?: ""
}