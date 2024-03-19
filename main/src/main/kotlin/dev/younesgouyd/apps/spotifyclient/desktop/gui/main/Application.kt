package dev.younesgouyd.apps.spotifyclient.desktop.gui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.Content
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.Login
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.SplashScreen
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.DARK_MODE_SETTING
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Theme
import kotlinx.coroutines.flow.MutableStateFlow

object Application {
    private val repoStore = RepoStore()
    private val currentComponent: MutableStateFlow<Component>

    init {
        currentComponent = MutableStateFlow(
            SplashScreen(
                repoStore = repoStore,
                showLogin = ::showLogin,
                showContent = ::showContent
            )
        )
    }

    fun start() {
        application {
            val currentComponent by currentComponent.collectAsState()

            Window(
                state = rememberWindowState(
                    placement = WindowPlacement.Maximized,
                    position = WindowPosition(Alignment.Center)
                ),
                onCloseRequest = ::exitApplication,
            ) {
                Theme(
                    darkTheme = DARK_MODE_SETTING,
                    content = {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            currentComponent.show()
                        }
                    }
                )
            }
        }
    }

    private fun showLogin() {
        currentComponent.value = Login(
            repoStore = repoStore,
            onDone = { currentComponent.value = Content(repoStore) }
        )
    }

    private fun showContent() {
        currentComponent.value = Content(repoStore)
    }
}
