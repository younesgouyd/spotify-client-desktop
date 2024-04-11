package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
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
        tracks = state.tracks,
        loadingTracks = state.loadingTracks,
        onArtistClick = state.onArtistClick,
        onLoadTracks = state.onLoadTracks,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@Composable
private fun AlbumDetails(
    album: Album,
    tracks: StateFlow<List<Album.Track>>,
    loadingTracks: StateFlow<Boolean>,
    onArtistClick: (ArtistId) -> Unit,
    onLoadTracks: () -> Unit,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit
) {
    val tracks by tracks.collectAsState()
    val loadingTracks by loadingTracks.collectAsState()
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
                            onArtistClick = onArtistClick,
                            onPlayClick = onPlayClick
                        )
                    }
                    items(
                        items = tracks,
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
        }.map { it == null ||  it >= (tracks.size + 1) - 5  }
            .filter { it }
            .collect { onLoadTracks() }
    }
}

@Composable
private fun AlbumInfo(
    modifier: Modifier,
    album: Album,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            url = album.images.preferablyMedium()
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = album.name ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            Row {
                for (artist in album.artists) {
                    TextButton(
                        content = { Text(text = artist.name ?: "", style = MaterialTheme.typography.labelMedium) },
                        onClick = { onArtistClick(artist.id) }
                    )
                }
            }
            IconButton(
                content = { Icon(Icons.Default.PlayCircle, null) },
                onClick = onPlayClick
            )
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
