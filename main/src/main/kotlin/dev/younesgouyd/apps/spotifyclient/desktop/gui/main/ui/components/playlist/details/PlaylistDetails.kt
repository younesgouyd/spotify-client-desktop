package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details

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
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist
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
        tracks = state.tracks,
        loadingTracks = state.loadingTracks,
        onLoadTracks = state.onLoadTracks,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@Composable
private fun PlaylistDetails(
    playlist: Playlist,
    tracks: StateFlow<List<Playlist.Track>>,
    loadingTracks: StateFlow<Boolean>,
    onLoadTracks: () -> Unit,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit
) {
    val tracks by tracks.collectAsState()
    val loadingTracks by loadingTracks.collectAsState()
    val lazyColumnState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { it ->
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
                            onPlayClick = onPlayClick
                        )
                    }
                    items(
                        items = tracks,
                        key = { it.id }
                    ) { item ->
                        TrackItem(
                            modifier = Modifier.fillMaxWidth(),
                            track = item,
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
        }.map { it == null || it >= (tracks.size + 1) - 5 }
            .filter { it }
            .collect { onLoadTracks() }
    }
}

@Composable
private fun PlaylistInfo(
    modifier: Modifier,
    playlist: Playlist,
    onPlayClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            url = playlist.images.preferablyMedium()
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = playlist.name ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = playlist.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
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
    track: Playlist.Track,
    onTrackClick: (TrackId) -> Unit
) {
    Item(
        modifier = modifier,
        onClick = { onTrackClick(track.id) },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(64.dp),
                url = track.images.preferablySmall()
            )
            Text(
                text = track.name ?: "",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
