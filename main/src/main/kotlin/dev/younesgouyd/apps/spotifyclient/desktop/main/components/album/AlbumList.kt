package dev.younesgouyd.apps.spotifyclient.desktop.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list.AlbumListItem
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list.AlbumListState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumList(
    repoStore: RepoStore,
    showAlbumDetails: (AlbumId) -> Unit,
    showArtistDetails: (ArtistId) -> Unit,
    playAlbum: (AlbumId) -> Unit
) : Component() {
    override val title: String = "Albums"
    private val state: MutableStateFlow<AlbumListState> = MutableStateFlow(AlbumListState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                AlbumListState.State(
                    albums = LazilyLoadedItems<AlbumListItem, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { offset ->
                            val data = repoStore.albumRepo.getSavedAlbums(offset)
                            LazilyLoadedItems.Page(
                                nextOffset = Offset.Index.fromUrl(data.next),
                                items = data.items?.filterNotNull()?.filter { it.album != null }?.map { savedAlbum ->
                                    AlbumListItem(
                                        id = savedAlbum.album!!.id,
                                        name = savedAlbum.album.name,
                                        images = savedAlbum.album.images?.toImages() ?: Images.empty(),
                                        releaseDate = savedAlbum.album.releaseDate,
                                        totalTracks = savedAlbum.album.totalTracks,
                                        artists = savedAlbum.album.artists?.map {
                                            AlbumListItem.Artist(id = it.id, name = it.name)
                                        } ?: emptyList(),
                                        genres = savedAlbum.album.genres ?: emptyList(),
                                        popularity = savedAlbum.album.popularity
                                    )
                                } ?: emptyList()
                            )
                        },
                        initialOffset = Offset.Index.initial()
                    ),
                    onAlbumClick = showAlbumDetails,
                    onArtistClick = showArtistDetails,
                    onPlayAlbumClick = playAlbum
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        AlbumList(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}