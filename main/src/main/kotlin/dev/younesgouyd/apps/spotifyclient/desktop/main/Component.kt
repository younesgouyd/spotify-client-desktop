package dev.younesgouyd.apps.spotifyclient.desktop.main

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

abstract class Component {
    protected val coroutineScope = CoroutineScope(SupervisorJob())
    abstract val title: String

    @Composable
    abstract fun show()

    abstract fun clear()
}
