package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.toImages
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.profile.Profile
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.profile.ProfileState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.profile.User
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Profile(
    private val repoStore: RepoStore,
    private val onLogout: () -> Unit
) : Component() {
    override val title: String = "Profile"
    private val state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                ProfileState.State(
                    user = kotlin.run {
                        val data = repoStore.userRepo.getCurrentUser()
                        User(
                            id = data.id,
                            displayName = data.displayName,
                            followerCount = data.followers?.total,
                            profilePictureUrl = (data.images?.toImages() ?: Images.empty()).preferablyMedium()
                        )
                    },
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

    override fun clear() {
        coroutineScope.cancel()
    }
}