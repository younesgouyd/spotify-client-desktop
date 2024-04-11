package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.LoginResult
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.Login
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Login(
    private val repoStore: RepoStore,
    private val onDone: () -> Unit
) : Component() {
    override val title: String = ""
    private val errorMessage = MutableStateFlow("")
    private val enabled = MutableStateFlow(true)

    @Composable
    override fun show() {
        val errorMessage by errorMessage.collectAsState()
        val enabled by enabled.collectAsState()

        Login(
            errorMessage = errorMessage,
            enabled = enabled,
            onLoginClick = ::onLoginClick
        )
    }

    private fun onLoginClick(clientId: String) {
        coroutineScope.launch {
            enabled.value = false
            val result = repoStore.authRepo.login(clientId)
            when (result) {
                LoginResult.Success -> onDone()
                LoginResult.Failure -> errorMessage.value = "Something went wrong"
            }
            enabled.value = true
        }
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}
