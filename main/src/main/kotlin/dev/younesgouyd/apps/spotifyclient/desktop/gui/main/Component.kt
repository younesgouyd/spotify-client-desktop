package dev.younesgouyd.apps.spotifyclient.desktop.gui.main

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

abstract class Component {
    protected val coroutineScope = CoroutineScope(SupervisorJob())

    @Composable
    abstract fun show()

    abstract fun clear()
}
