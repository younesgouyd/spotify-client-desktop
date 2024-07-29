package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.formatted
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun AlbumDetails(state: AlbumDetailsState) {
    when (state) {
        is AlbumDetailsState.Loading -> Text("Loading...")
        is AlbumDetailsState.State -> AlbumDetails(state)
    }
}

@Composable
private fun AlbumDetails(state: AlbumDetailsState.State) {
    AlbumDetails(
        album = state.album,
        saved = state.saved,
        saveRemoveButtonEnabled = state.saveRemoveButtonEnabled,
        tracks = state.tracks,
        addTrackToPlaylistDialogState = state.addTrackToPlaylistDialogState,
        addTrackToFolderDialogState = state.addTrackToFolderDialogState,
        onSaveClick = state.onSaveClick,
        onRemoveClick = state.onRemoveClick,
        onArtistClick = state.onArtistClick,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AlbumDetails(
    album: Album,
    saved: StateFlow<Boolean>,
    saveRemoveButtonEnabled: StateFlow<Boolean>,
    tracks: LazilyLoadedItems<Album.Track, Offset.Index>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onSaveClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit,
) {
    val saved by saved.collectAsState()
    val saveRemoveButtonEnabled by saveRemoveButtonEnabled.collectAsState()
    val items by tracks.items.collectAsState()
    val loadingTracks by tracks.loading.collectAsState()
    val lazyColumnState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                VerticalScrollbar(lazyColumnState)
                LazyColumn (
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyColumnState,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        AlbumInfo(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            album = album,
                            saved = saved,
                            saveRemoveButtonEnabled = saveRemoveButtonEnabled,
                            onArtistClick = onArtistClick,
                            onPlayClick = onPlayClick,
                            onSaveClick = onSaveClick,
                            onRemoveClick = onRemoveClick
                        )
                    }
                    item {
                        Spacer(Modifier.size(8.dp))
                    }
                    stickyHeader {
                        TracksHeader(modifier = Modifier.fillMaxWidth().height(64.dp))
                        HorizontalDivider()
                    }
                    items(items = items, key = { it.id }) { track ->
                        TrackItem(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            track = track,
                            images = album.images,
                            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                            addTrackToFolderDialogState = addTrackToFolderDialogState,
                            onTrackClick = onTrackClick,
                            onArtistClick = onArtistClick
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
        }.map { it == null ||  it >= (items.size + 1) - 5  }
            .filter { it }
            .collect { tracks.loadMore() }
    }
}

@Composable
private fun AlbumInfo(
    modifier: Modifier,
    album: Album,
    saved: Boolean,
    saveRemoveButtonEnabled: Boolean,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: () -> Unit,
    onSaveClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.fillMaxHeight(),
            url = album.images.preferablyMedium(),
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = album.name ?: "",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (artist in album.artists) {
                    TextButton(
                        onClick = { onArtistClick(artist.id) },
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    Icon(Icons.Default.Person, null)
                                    Text(
                                        text = artist.name ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )
                        }
                    )
                }
            }
            if (album.releaseDate != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Release date: ${album.releaseDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            if (album.genres.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Genres: ${album.genres.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            if (album.popularity != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Popularity: ${album.popularity}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
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
                            if (!saved) {
                                Icon(Icons.Default.Add, null)
                                Text(text = "Save", style = MaterialTheme.typography.labelMedium)
                            } else {
                                Icon(Icons.Default.Remove, null)
                                Text(text = "Remove", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    },
                    onClick = { if (!saved) onSaveClick() else onRemoveClick() },
                    enabled = saveRemoveButtonEnabled
                )
            }
        }
    }
}

private const val TITLE_WEIGHT = .8f
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
    track: Album.Track,
    images: Images,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onTrackClick: (TrackId) -> Unit,
    onArtistClick: (ArtistId) -> Unit
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
            Column(
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
            track = Track(track.id, track.name, images.preferablySmall()),
            onDismissRequest = { addToPlaylistDialogVisible = false }
        )
    }

    if (addToFolderDialogVisible) {
        AddTrackToFolderDialog(
            track = Track(track.id, track.name, images.preferablySmall()),
            state = addTrackToFolderDialogState,
            onDismissRequest = { addToFolderDialogVisible = false }
        )
    }
}
