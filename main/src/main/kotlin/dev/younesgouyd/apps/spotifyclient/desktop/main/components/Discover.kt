package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistListItem
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list.PlaylistListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Discover(
    private val repoStore: RepoStore,
    showPlaylistDetails: (PlaylistId) -> Unit,
    playPlaylist: (PlaylistId) -> Unit
) : Component() {
    override val title: String = "Discover"
    private val state: MutableStateFlow<PlaylistListState> = MutableStateFlow(PlaylistListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                PlaylistListState.State(
                    playlists = LazilyLoadedItems<PlaylistListItem, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { offset ->
                            val data = repoStore.playlistRepo.getDiscoverPlaylists(offset)
                            LazilyLoadedItems.Page(
                                nextOffset = Offset.Index.fromUrl(data.playlists?.next),
                                items = data.playlists?.let { playlists ->
                                    playlists.items?.filterNotNull()?.map {
                                        PlaylistListItem(
                                            id = it.id,
                                            name = it.name,
                                            images = it.images?.toImages() ?: Images.empty()
                                        )
                                    }
                                } ?: emptyList()
                            )
                        },
                        initialOffset = Offset.Index.initial()
                    ),
                    onPlaylistClick = showPlaylistDetails,
                    onPlayPlaylistClick = playPlaylist
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        PlaylistList(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}