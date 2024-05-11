package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.UserId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Playlist
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun PlaylistDetails(state: PlaylistDetailsState) {
    when (state) {
        is PlaylistDetailsState.Loading -> Text("Loading...")
        is PlaylistDetailsState.State -> PlaylistDetails(state)
    }
}

@Composable
private fun PlaylistDetails(state: PlaylistDetailsState.State) {
    PlaylistDetails(
        playlist = state.playlist,
        followButtonEnabledState = state.followButtonEnabledState,
        tracks = state.tracks,
        addTrackToPlaylistDialogState = state.addTrackToPlaylistDialogState,
        onOwnerClick = state.onOwnerClick,
        onPlaylistFollowStateChange = state.onPlaylistFollowStateChange,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@Composable
private fun PlaylistDetails(
    playlist: StateFlow<Playlist>,
    followButtonEnabledState: StateFlow<Boolean>,
    tracks: LazilyLoadedItems<Playlist.Track, Offset.Index>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    onOwnerClick: (UserId) -> Unit,
    onPlaylistFollowStateChange: (state: Boolean) -> Unit,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit
) {
    val playlist by playlist.collectAsState()
    val followButtonEnabledState by followButtonEnabledState.collectAsState()
    val items by tracks.items.collectAsState()
    val loadingTracks by tracks.loading.collectAsState()
    val lazyColumnState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {
            Box(modifier = Modifier.fillMaxSize().padding(it)) {
                VerticalScrollbar(lazyColumnState)
                LazyColumn (
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyColumnState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        PlaylistInfo(
                            modifier = Modifier.fillMaxWidth(),
                            playlist = playlist,
                            followButtonEnabledState = followButtonEnabledState,
                            onOwnerClick = onOwnerClick,
                            onPlaylistFollowStateChange = onPlaylistFollowStateChange,
                            onPlayClick = onPlayClick
                        )
                    }
                    items(items = items) { item ->
                        TrackItem(
                            modifier = Modifier.fillMaxWidth(),
                            track = item,
                            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                            onTrackClick = onTrackClick
                        )
                    }
                    if (loadingTracks) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = { ScrollToTopFloatingActionButton(lazyColumnState) }
    )

    LaunchedEffect(lazyColumnState) {
        snapshotFlow {
            lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null || it >= (items.size + 1) - 5 }
            .filter { it }
            .collect { tracks.loadMore() }
    }
}

@Composable
private fun PlaylistInfo(
    modifier: Modifier,
    playlist: Playlist,
    followButtonEnabledState: Boolean,
    onOwnerClick: (UserId) -> Unit,
    onPlaylistFollowStateChange: (state: Boolean) -> Unit,
    onPlayClick: () -> Unit
) {
    Row(
        modifier = modifier.height(300.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.fillMaxHeight(),
            url = playlist.images.preferablyMedium(),
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterVertically)
        ) {
            Text(
                text = playlist.name ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = playlist.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            if (playlist.owner != null) {
                TextButton(
                    content = { Text(text = playlist.owner.name ?: "", style = MaterialTheme.typography.labelMedium) },
                    onClick = { onOwnerClick(playlist.owner.id) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayCircle, null)
                            Text(text = "Play", style = MaterialTheme.typography.labelMedium)
                        }
                    },
                    onClick = onPlayClick
                )
                Button(
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!playlist.followed) {
                                Icon(Icons.AutoMirrored.Default.PlaylistAdd, null)
                                Text(text = "Follow", style = MaterialTheme.typography.labelMedium)
                            } else {
                                Icon(Icons.Default.PlaylistRemove, null)
                                Text(text = "Unfollow", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    },
                    onClick = { if (playlist.canUnfollow) onPlaylistFollowStateChange(!playlist.followed) },
                    enabled = followButtonEnabledState && playlist.canUnfollow
                )
            }
        }
    }
}

@Composable
private fun TrackItem(
    modifier: Modifier = Modifier,
    track: Playlist.Track,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    onTrackClick: (TrackId) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }

    Item(
        modifier = modifier,
        onClick = { onTrackClick(track.id) },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(64.dp),
                    url = track.images.preferablySmall()
                )
                Text(
                    text = track.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                content = { Icon(Icons.Default.Save, null) },
                onClick = { dialogVisible = true }
            )
        }
    }

    if (dialogVisible) {
        AddTrackToPlaylistDialog(
            state = addTrackToPlaylistDialogState,
            track = Track(track.id, track.name, track.images.preferablySmall()),
            onDismissRequest = { dialogVisible = false }
        )
    }
}
