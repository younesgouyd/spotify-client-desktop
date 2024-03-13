package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.LoginResult
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Login(
    private val repoStore: RepoStore,
    private val toCurrentUserProfile: () -> Unit
) : Component() {
    private val state: MutableStateFlow<LoginResult?> = MutableStateFlow(null)

    init {
        coroutineScope.launch {
            val result = repoStore.loginRepo.login()
            state.update { result }
            if (result == LoginResult.Success) {
                toCurrentUserProfile()
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        if (state == null) {
            Text("Login in...")
        } else Text(state.toString())
    }
}
