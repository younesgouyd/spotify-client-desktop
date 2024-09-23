package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.playlist.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.formatted
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
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
        addTrackToFolderDialogState = state.addTrackToFolderDialogState,
        onOwnerClick = state.onOwnerClick,
        onPlaylistFollowStateChange = state.onPlaylistFollowStateChange,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick,
        onArtistClick = state.onArtistClick,
        onAlbumClick = state.onAlbumClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlaylistDetails(
    playlist: StateFlow<Playlist>,
    followButtonEnabledState: StateFlow<Boolean>,
    tracks: LazilyLoadedItems<Playlist.Track, Offset.Index>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onOwnerClick: (UserId) -> Unit,
    onPlaylistFollowStateChange: (state: Boolean) -> Unit,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onAlbumClick: (AlbumId) -> Unit
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
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        PlaylistInfo(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            playlist = playlist,
                            followButtonEnabledState = followButtonEnabledState,
                            onOwnerClick = onOwnerClick,
                            onPlaylistFollowStateChange = onPlaylistFollowStateChange,
                            onPlayClick = onPlayClick
                        )
                    }
                    item {
                        Spacer(Modifier.size(8.dp))
                    }
                    stickyHeader {
                        TracksHeader(modifier = Modifier.fillMaxWidth().height(64.dp))
                        HorizontalDivider()
                    }
                    items(items = items) { track ->
                        TrackItem(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            track = track,
                            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                            addTrackToFolderDialogState = addTrackToFolderDialogState,
                            onTrackClick = onTrackClick,
                            onArtistClick = onArtistClick,
                            onAlbumClick = onAlbumClick
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
        modifier = modifier,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = playlist.name ?: "",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            if (playlist.owner != null) {
                TextButton(
                    onClick = { onOwnerClick(playlist.owner.id) },
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, null)
                            Text(
                                text = playlist.owner.name ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                )
            }
            if (playlist.followerCount != null) {
                Text(
                    text = "${playlist.followerCount} followers",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            if (playlist.description != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = playlist.description,
                    style = MaterialTheme.typography.bodyMedium
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

private const val TITLE_WEIGHT = .45f
private const val ALBUM_WEIGHT = .18f
private const val ADDED_AT_WEIGHT = .1f
private const val POPULARITY_WEIGHT = .07f
private const val DURATION_WEIGHT = .1f
private const val ACTIONS_WEIGHT = .1f

@Composable
private fun TracksHeader(modifier: Modifier = Modifier) {
    Surface {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.fillMaxSize().weight(TITLE_WEIGHT), contentAlignment = Alignment.Center) {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.fillMaxSize().weight(ALBUM_WEIGHT), contentAlignment = Alignment.Center) {
                Text(
                    text = "Album",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.fillMaxSize().weight(ADDED_AT_WEIGHT), contentAlignment = Alignment.Center) {
                Text(
                    text = "Date added",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.fillMaxSize().weight(POPULARITY_WEIGHT), contentAlignment = Alignment.Center) {
                Text(
                    text = "Popularity",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.fillMaxSize().weight(DURATION_WEIGHT), contentAlignment = Alignment.Center) {
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.fillMaxSize().weight(ACTIONS_WEIGHT), contentAlignment = Alignment.Center) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onTrackClick: (TrackId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onAlbumClick: (AlbumId) -> Unit
) {
    var addToPlaylistDialogVisible by remember { mutableStateOf(false) }
    var addToFolderDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.clickable { onTrackClick(track.id) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // image + title + artists
        Box(modifier = Modifier.fillMaxSize().weight(TITLE_WEIGHT), contentAlignment = Alignment.CenterStart) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.fillMaxHeight(),
                    url = track.album?.images?.preferablySmall(),
                    contentScale = ContentScale.FillHeight
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = track.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(items = track.artists, key = { it.id }) { artist ->
                            TextButton(
                                onClick = { onArtistClick(artist.id) },
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Person, null)
                                        Text(
                                            text = artist.name ?: "",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // album
        Box(modifier = Modifier.fillMaxSize().weight(ALBUM_WEIGHT), contentAlignment = Alignment.CenterStart) {
            if (track.album != null) {
                TextButton(
                    onClick = { onAlbumClick(track.album.id) },
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Album, null)
                            Text(
                                text = track.album.name ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // added at
        Box(modifier = Modifier.fillMaxSize().weight(ADDED_AT_WEIGHT), contentAlignment = Alignment.Center) {
            Text(
                text = track.addedAt ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // popularity
        Box(modifier = Modifier.fillMaxSize().weight(POPULARITY_WEIGHT), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = track.popularity?.toString() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // duration
        Box(modifier = Modifier.fillMaxSize().weight(DURATION_WEIGHT), contentAlignment = Alignment.Center) {
            Text(
                text = track.duration.formatted(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // actions
        Box(modifier = Modifier.fillMaxSize().weight(ACTIONS_WEIGHT), contentAlignment = Alignment.Center) {
            Row {
                IconButton(
                    content = { Icon(Icons.Default.Save, null) },
                    onClick = { addToPlaylistDialogVisible = true }
                )
                IconButton(
                    content = { Icon(Icons.Default.Folder, null) },
                    onClick = { addToFolderDialogVisible = true }
                )
            }
        }
    }
    HorizontalDivider()

    if (addToPlaylistDialogVisible) {
        AddTrackToPlaylistDialog(
            state = addTrackToPlaylistDialogState,
            track = Track(track.id, track.name, track.album?.images?.preferablySmall()),
            onDismissRequest = { addToPlaylistDialogVisible = false }
        )
    }

    if (addToFolderDialogVisible) {
        AddTrackToFolderDialog(
            track = Track(track.id, track.name, track.album?.images?.preferablySmall()),
            state = addTrackToFolderDialogState,
            onDismissRequest = { addToFolderDialogVisible = false }
        )
    }
}

