package dev.younesgouyd.apps.spotifyclient.desktop.main

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.Content
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.Login
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.SplashScreen
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object Application {
    private val coroutineScope = CoroutineScope(SupervisorJob())
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
                content = { currentComponent.show() }
            )
        }
    }

    private fun showLogin() {
        currentComponent.update {
            it.clear()
            Login(
                repoStore = repoStore,
                onDone = ::showContent
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