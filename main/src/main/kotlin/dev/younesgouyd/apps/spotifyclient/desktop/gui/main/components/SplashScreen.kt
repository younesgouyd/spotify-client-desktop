package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import kotlinx.coroutines.launch

class SplashScreen(
    private val repoStore: RepoStore,
    private val showLogin: () -> Unit,
    private val showContent: () -> Unit
) : Component() {

    init {
        coroutineScope.launch {
            repoStore.appDataRepo.init()
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
    }
}