package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user

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
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.User
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun User(state: UserState) {
    when (state) {
        is UserState.Loading -> Text("Loading...")
        is UserState.State -> User(state)
    }
}

@Composable
private fun User(state: UserState.State) {
    User(
        user = state.user,
        playlists = state.playlists,
        onPlaylistClick = state.onPlaylistClick,
        onPlayPlaylistClick = state.onPlayPlaylistClick
    )
}

@Composable
private fun User(
    user: User,
    playlists: LazilyLoadedItems<User.Playlist, Offset.Index>,
    onPlaylistClick: (PlaylistId) -> Unit,
    onPlayPlaylistClick: (PlaylistId) -> Unit
) {
    val loadingPlaylists by playlists.loading.collectAsState()
    val items by playlists.items.collectAsState()
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                VerticalScrollbar(lazyGridState)
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyGridState,
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    columns = GridCells.Adaptive(250.dp)
                ) {
                    item(content = { UserInfo(user = user) }, span = { GridItemSpan(maxLineSpan) })
                    playlists(items, onPlaylistClick, onPlayPlaylistClick)
                    if (loadingPlaylists) {
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
        }.map { it == null || it >= (items.size + 2) - 5 }
            .filter { it }
            .collect { playlists.loadMore() }
    }
}

@Composable
private fun UserInfo(
    modifier: Modifier = Modifier,
    user: User
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Start
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            url = user.profilePictureUrl
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.displayName ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "${user.followerCount} followers",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

private fun LazyGridScope.playlists(
    playlists: List<User.Playlist>,
    onPlaylistClick: (PlaylistId) -> Unit,
    onPlayPlaylistClick: (PlaylistId) -> Unit
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Playlists",
            style = MaterialTheme.typography.headlineMedium
        )
    }
    items(
        items = playlists,
        key = { it.id }
    ) { playlist ->
        Item(onClick = { onPlaylistClick(playlist.id) }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
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
                        onClick = { onPlayPlaylistClick(playlist.id) }
                    )
                }
            }
        }
    }
}