package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaylistListItem
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun PlaylistList(state: PlaylistListState) {
    when (state) {
        is PlaylistListState.Loading -> Text("Loading...")
        is PlaylistListState.State -> PlaylistList(state)
    }
}

@Composable
private fun PlaylistList(state: PlaylistListState.State) {
    PlaylistList(
        playlists = state.playlists,
        onPlaylistClick = state.onPlaylistClick,
        onPlayPlaylistClick = state.onPlayPlaylistClick
    )
}

@Composable
private fun PlaylistList(
    playlists: LazilyLoadedItems<PlaylistListItem, Offset.Index>,
    onPlaylistClick: (PlaylistId) -> Unit,
    onPlayPlaylistClick: (PlaylistId) -> Unit
) {
    val items by playlists.items.collectAsState()
    val loading by playlists.loading.collectAsState()
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
                    columns = GridCells.Adaptive(250.dp)
                ) {
                    items(
                        items = items,
                        key = { it.id }
                    ) { item ->
                        PlaylistItem(
                            playlist = item,
                            onClick = onPlaylistClick,
                            onPlayClick = onPlayPlaylistClick
                        )
                    }
                    if (loading) {
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
        }.map { it == null ||  it >= (items.size + 1) - 5 }
            .filter { it }
            .collect { playlists.loadMore() }
    }
}

@Composable
private fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: PlaylistListItem,
    onClick: (PlaylistId) -> Unit,
    onPlayClick: (PlaylistId) -> Unit
) {
    Item (
        modifier = modifier,
        onClick = { onClick(playlist.id) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                url = playlist.images.preferablyMedium(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                text = playlist.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    content = { Icon(Icons.Default.PlayCircle, null) },
                    onClick = { onPlayClick(playlist.id) }
                )
            }
        }
    }
}
