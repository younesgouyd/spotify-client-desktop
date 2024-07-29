package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.User
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.UserDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user.UserDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDetails(
    id: UserId,
    repoStore: RepoStore,
    showPlaylistDetails: (PlaylistId) -> Unit,
    showUserDetails: (UserId) -> Unit,
    playPlaylist: (PlaylistId) -> Unit
) : Component() {
    override val title: String = "User"
    private val state: MutableStateFlow<UserDetailsState> = MutableStateFlow(UserDetailsState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                UserDetailsState.State(
                    user = kotlin.run {
                        val data = repoStore.userRepo.getUser(id)
                        User(
                            id = data.id,
                            displayName = data.displayName,
                            followerCount = data.followers?.total,
                            profilePictureUrl = data.images?.toImages()?.preferablyMedium()
                        )
                    },
                    playlists = LazilyLoadedItems<User.Playlist, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { offset ->
                            val data = repoStore.playlistRepo.getUserPlaylists(id, offset)
                            LazilyLoadedItems.Page<User.Playlist, Offset.Index>(
                                nextOffset = Offset.Index.fromUrl(data.next),
                                items = data.items?.filterNotNull()?.map { simplifiedPlaylist ->
                                    User.Playlist(
                                        id = simplifiedPlaylist.id,
                                        name = simplifiedPlaylist.name,
                                        images = simplifiedPlaylist.images?.toImages() ?: Images.empty(),
                                        owner = simplifiedPlaylist.owner?.let { User.Playlist.Owner(id = it.id, displayName = it.displayName) }
                                    )
                                } ?: emptyList()
                            )
                        },
                        initialOffset = Offset.Index.initial()
                    ),
                    onPlaylistClick = showPlaylistDetails,
                    onOwnerClick = showUserDetails,
                    onPlayPlaylistClick = playPlaylist,
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        UserDetails(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}