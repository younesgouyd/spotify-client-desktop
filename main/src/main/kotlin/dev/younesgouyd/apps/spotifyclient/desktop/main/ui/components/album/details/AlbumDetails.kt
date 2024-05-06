package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.LazilyLoadedItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.Offset
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Album
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
        onSaveClick = state.onSaveClick,
        onRemoveClick = state.onRemoveClick,
        onArtistClick = state.onArtistClick,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@Composable
private fun AlbumDetails(
    album: Album,
    saved: StateFlow<Boolean>,
    saveRemoveButtonEnabled: StateFlow<Boolean>,
    tracks: LazilyLoadedItems<Album.Track, Offset.Index>,
    onSaveClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit
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
                            modifier = Modifier.fillMaxWidth(),
                            album = album,
                            saved = saved,
                            saveRemoveButtonEnabled = saveRemoveButtonEnabled,
                            onArtistClick = onArtistClick,
                            onPlayClick = onPlayClick,
                            onSaveClick = onSaveClick,
                            onRemoveClick = onRemoveClick
                        )
                    }
                    items(
                        items = items,
                        key = { it.id }
                    ) { item ->
                        TrackItem(
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            track = item,
                            onTrackClick = onTrackClick
                        )
                        HorizontalDivider()
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
        modifier = modifier.height(300.dp),
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
            verticalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterVertically)
        ) {
            Text(
                text = album.name ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (artist in album.artists) {
                    TextButton(
                        content = { Text(text = artist.name ?: "", style = MaterialTheme.typography.labelMedium) },
                        onClick = { onArtistClick(artist.id) }
                    )
                }
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

@Composable
private fun TrackItem(
    modifier: Modifier = Modifier,
    track: Album.Track,
    onTrackClick: (TrackId) -> Unit
) {
    Box(
        modifier = modifier.clickable { onTrackClick(track.id) },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = track.name ?: "",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
