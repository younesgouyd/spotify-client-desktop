package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI

typealias ImageUrl = String

private val cache = mutableMapOf<ImageUrl, ImageBitmap>()

@Composable
fun Image(
    modifier: Modifier = Modifier,
    url: ImageUrl?,
) {
    var loading by remember { mutableStateOf(true) }
    var img by remember { mutableStateOf<ImageBitmap?>(null) }

    if (url == null) {
        BrokenImage(modifier)
    } else {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                loading = true
                val fromCache = cache[url]
                if (fromCache == null) {
                    try {
                        URI(url).toURL().openStream().use {
                            val imageBitmap = loadImageBitmap(it)
                            img = imageBitmap
                            cache += url to imageBitmap
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    img = fromCache
                }
                loading = false
            }
        }

        when (loading) {
            true -> LoadingImage(modifier)
            false -> {
                img?.let {
                    androidx.compose.foundation.Image(
                        modifier = modifier,
                        bitmap = it,
                        contentDescription = null
                    )
                } ?: BrokenImage(modifier)
            }
        }
    }
}

@Composable
fun BrokenImage(
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Image(
        modifier = modifier,
        imageVector = Icons.Default.BrokenImage,
        contentDescription = null
    )
}

@Composable
private fun LoadingImage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
