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
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object Application {
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val repoStore = RepoStore()
    private val currentComponent: MutableStateFlow<Component>
    private val darkTheme: StateFlow<DarkThemeOptions?>

    init {
        darkTheme = repoStore.settingsRepo.getDarkThemeFlow().stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
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
            val darkTheme by darkTheme.collectAsState()

            Window(
                state = rememberWindowState(
                    placement = WindowPlacement.Maximized,
                    position = WindowPosition(Alignment.Center)
                ),
                onCloseRequest = ::exitApplication,
            ) {
                Theme(
                    darkTheme = darkTheme ?: DarkThemeOptions.SystemDefault,
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
        currentComponent.update {
            it.clear()
            Login(
                repoStore = repoStore,
                onDone = { currentComponent.value = Content(repoStore, ::logout) }
            )
        }
    }

    private fun showContent() {
        currentComponent.update {
            it.clear()
            Content(repoStore, ::logout)
        }
    }

    private fun logout() {
        coroutineScope.launch {
            repoStore.authRepo.logout()
            showLogin()
        }
    }
}
