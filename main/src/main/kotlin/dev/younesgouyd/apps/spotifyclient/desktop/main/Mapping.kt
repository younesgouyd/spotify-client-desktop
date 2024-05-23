package dev.younesgouyd.apps.spotifyclient.desktop.main

import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.ImageOfFloatSize
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images

fun List<Image>.toImages(): Images {
    return Images(
        large = try { this[0].url } catch (ignored: Exception) { null },
        medium = try { this[1].url } catch (ignored: Exception) { null },
        small = try { this[2].url } catch (ignored: Exception) { null }
    )
}

fun List<ImageOfFloatSize>.toImages2(): Images {
    return Images(
        large = try { this[0].url } catch (ignored: Exception) { null },
        medium = try { this[1].url } catch (ignored: Exception) { null },
        small = try { this[2].url } catch (ignored: Exception) { null }
    )
}