package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.AlbumListItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun AlbumList(state: AlbumListState) {
    when (state) {
        is AlbumListState.Loading -> Text("Loading...")
        is AlbumListState.State -> AlbumList(state)
    }
}

@Composable
private fun AlbumList(state: AlbumListState.State) {
    AlbumList(
        albums = state.albums,
        loadingAlbums = state.loadingAlbums,
        onLoadAlbums = state.onLoadAlbums,
        onAlbumClick = state.onAlbumClick
    )
}

@Composable
private fun AlbumList(
    albums: StateFlow<List<AlbumListItem>>,
    loadingAlbums: StateFlow<Boolean>,
    onLoadAlbums: () -> Unit,
    onAlbumClick: (AlbumId) -> Unit
) {
    val albums by albums.collectAsState()
    val loadingAlbums by loadingAlbums.collectAsState()
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                VerticalScrollbar(lazyGridState)
                LazyVerticalGrid (
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyGridState,
                    contentPadding = PaddingValues(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    columns = GridCells.FixedSize(200.dp)
                ) {
                    items(
                        items = albums,
                        key = { it.id }
                    ) { item ->
                        AlbumItem(
                            album = item,
                            onAlbumClick = onAlbumClick
                        )
                    }
                    if (loadingAlbums) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(modifier = Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = { ScrollToTopFloatingActionButton(lazyGridState) }
    )

    LaunchedEffect(lazyGridState) {
        snapshotFlow {
            lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null || it >= albums.size - 5 }
            .filter { it }
            .collect { onLoadAlbums() }
    }
}

@Composable
private fun AlbumItem(
    modifier: Modifier = Modifier,
    album: AlbumListItem,
    onAlbumClick: (AlbumId) -> Unit
) {
    Item (
        modifier = modifier,
        onClick = { onAlbumClick(album.id) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.TopCenter // todo
            ) {
                Image(
                    url = album.images.preferablyMedium()
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                text = album.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
