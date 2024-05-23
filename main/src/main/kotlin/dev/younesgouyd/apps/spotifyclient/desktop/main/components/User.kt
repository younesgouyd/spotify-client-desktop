package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.User
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.UserState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
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
                    user = kotlin.run {
                        val data = repoStore.userRepo.getUser(id)
                        UserState.User(
                            id = data.id,
                            displayName = data.displayName,
                            followerCount = data.followers?.total,
                            profilePictureUrl = data.images?.toImages()?.preferablyMedium()
                        )
                    },
                    playlists = LazilyLoadedItems<UserState.User.Playlist, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = {
                            val data = repoStore.playlistRepo.getUserPlaylists(id, it)
                            LazilyLoadedItems.Page<UserState.User.Playlist, Offset.Index>(
                                nextOffset = Offset.Index.fromUrl(data.next),
                                items = data.items?.filterNotNull()?.map {
                                    UserState.User.Playlist(
                                        id = it.id,
                                        name = it.name,
                                        images = it.images?.toImages() ?: Images.empty()
                                    )
                                } ?: emptyList()
                            )
                        },
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