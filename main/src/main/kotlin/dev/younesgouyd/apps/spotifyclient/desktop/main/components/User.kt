package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.User
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.UserState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class User(
    private val id: UserId,
    private val repoStore: RepoStore,
    showPlaylistDetails: (PlaylistId) -> Unit
) : Component() {
    override val title: String = "User"
    private val state: MutableStateFlow<UserState> = MutableStateFlow(UserState.Loading)
    private val playlists: MutableStateFlow<List<User.Playlist>> = MutableStateFlow(emptyList())
    private val loadingPlaylists: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var offset = 0
    private var reachedTheEnd = false

    init {
        coroutineScope.launch {
            state.update {
                UserState.State(
                    user = repoStore.userRepo.getUser(id),
                    playlists = playlists.asStateFlow(),
                    loadingPlaylists = loadingPlaylists.asStateFlow(),
                    onLoadPlaylists = ::loadPlaylists,
                    onPlaylistClick = showPlaylistDetails
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        User(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private fun loadPlaylists() {
        coroutineScope.launch {
            if (!reachedTheEnd && !loadingPlaylists.value) {
                loadingPlaylists.update { true }
                val result = repoStore.playlistRepo.getUserPlaylists(id, 20, offset)
                offset += result.size
                reachedTheEnd = result.isEmpty()
                playlists.update { it + result.filterNotNull() }
                loadingPlaylists.update { false }
            }
        }
    }
}