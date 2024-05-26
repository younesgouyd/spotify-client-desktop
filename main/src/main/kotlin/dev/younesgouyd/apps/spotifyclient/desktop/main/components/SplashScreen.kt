package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.DarkThemeOptions
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Theme
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SplashScreen(
    private val repoStore: RepoStore,
    private val showLogin: () -> Unit,
    private val showContent: () -> Unit
) : Component() {
    override val title: String = ""

    init {
        coroutineScope.launch {
            System.setProperty("sun.java2d.uiScale", "1.0")
            repoStore.init()
            if (repoStore.authRepo.isAuthorized()) {
                repoStore.authRepo.refreshTokenIfNeeded()
                showContent()
            } else {
                showLogin()
            }
        }
    }

    @Composable
    override fun show() {
        Theme(
            darkTheme = DarkThemeOptions.SystemDefault,
            content = {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading...")
                    }
                }
            }
        )
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}