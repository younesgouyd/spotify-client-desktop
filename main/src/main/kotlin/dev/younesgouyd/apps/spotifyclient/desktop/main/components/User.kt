package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.User
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.UserState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class User(
    private val id: UserId,
    private val repoStore: RepoStore,
    showPlaylistDetails: (PlaylistId) -> Unit,
    playPlaylist: (PlaylistId) -> Unit
) : Component() {
    override val title: String = "User"
    private val state: MutableStateFlow<UserState> = MutableStateFlow(UserState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                UserState.State(
                    user = repoStore.userRepo.getUser(id),
                    playlists = LazilyLoadedItems<User.Playlist, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { repoStore.playlistRepo.getUserPlaylists(id, it) },
                        initialOffset = Offset.Index.initial()
                    ),
                    onPlaylistClick = showPlaylistDetails,
                    onPlayPlaylistClick = playPlaylist,
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
}