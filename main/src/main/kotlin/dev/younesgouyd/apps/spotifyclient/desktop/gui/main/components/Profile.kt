package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.profile.Profile
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.profile.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Profile(
    private val repoStore: RepoStore,
    private val onLogout: () -> Unit
) : Component() {
    private val state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                ProfileState.State(
                    user = repoStore.userRepo.getCurrentUser(),
                    onLogoutClick = {
                        coroutineScope.launch {
                            onLogout()
                        }
                    }
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        Profile(state)
    }
}