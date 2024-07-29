package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun UserDetails(state: UserDetailsState) {
    when (state) {
        is UserDetailsState.Loading -> Text("Loading...")
        is UserDetailsState.State -> UserDetails(state)
    }
}

@Composable
private fun UserDetails(state: UserDetailsState.State) {
    UserDetails(
        user = state.user,
        playlists = state.playlists,
        onPlaylistClick = state.onPlaylistClick,
        onOwnerClick = state.onOwnerClick,
        onPlayPlaylistClick = state.onPlayPlaylistClick
    )
}

@Composable
private fun UserDetails(
    user: User,
    playlists: LazilyLoadedItems<User.Playlist, Offset.Index>,
    onPlaylistClick: (PlaylistId) -> Unit,
    onOwnerClick: (UserId) -> Unit,
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
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        UserInfo(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            user = user
                        )
                    }
                    playlists(
                        playlists = items,
                        onPlaylistClick = onPlaylistClick,
                        onOwnerClick = onOwnerClick,
                        onPlayPlaylistClick = onPlayPlaylistClick
                    )
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
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.fillMaxHeight(),
            url = user.profilePictureUrl,
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = user.displayName ?: "",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            if (user.followerCount != null) {
                Text(
                    text = "${user.followerCount} followers",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

private fun LazyGridScope.playlists(
    playlists: List<User.Playlist>,
    onPlaylistClick: (PlaylistId) -> Unit,
    onOwnerClick: (UserId) -> Unit,
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
                TextButton(
                    onClick = { playlist.owner?.let { onOwnerClick(it.id) } }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, null)
                        Text(playlist.owner?.displayName ?: "")
                    }
                }
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