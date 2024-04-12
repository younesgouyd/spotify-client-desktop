package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun ArtistDetails(state: ArtistDetailsState) {
    when (state) {
        is ArtistDetailsState.Loading -> Text("Loading...")
        is ArtistDetailsState.State -> ArtistDetails(state)
    }
}

@Composable
private fun ArtistDetails(state: ArtistDetailsState.State) {
    ArtistDetails(
        artist = state.artist,
        topTracks = state.topTracks,
        albums = state.albums,
        loadingAlbums = state.loadingAlbums,
        onLoadAlbums = state.onLoadAlbums,
        onPlayClick = state.onPlayClick,
        onAlbumClick = state.onAlbumClick,
        onPlayTrackClick = state.onPlayTrackClick
    )
}

@Composable
private fun ArtistDetails(
    artist: Artist,
    topTracks: List<Artist.Track>,
    albums: StateFlow<List<Artist.Album>>,
    loadingAlbums: StateFlow<Boolean>,
    onLoadAlbums: () -> Unit,
    onPlayClick: () -> Unit,
    onAlbumClick: (AlbumId) -> Unit,
    onPlayTrackClick: (TrackId) -> Unit
) {
    val albums by albums.collectAsState()
    val loadingAlbums by loadingAlbums.collectAsState()
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
                    columns = GridCells.FixedSize(200.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ArtistInfo(
                            modifier = Modifier.fillMaxWidth(),
                            artist = artist,
                            onPlayClick = onPlayClick
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Top Tracks",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TopTracks(
                            modifier = Modifier.fillMaxWidth(),
                            tracks = topTracks,
                            onPlayTrackClick = onPlayTrackClick
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Discography",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
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
        }.map { it == null || it >= (albums.size + 4) - 5 }
            .filter { it }
            .collect { onLoadAlbums() }
    }
}

@Composable
private fun ArtistInfo(
    modifier: Modifier = Modifier,
    artist: Artist,
    onPlayClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            url = artist.images.preferablyMedium()
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = artist.name ?: "",
                style = MaterialTheme.typography.displayMedium
            )
            IconButton(
                content = { Icon(Icons.Default.PlayCircle, null) },
                onClick = onPlayClick
            )
        }
    }
}

@Composable
private fun TopTracks(
    modifier: Modifier = Modifier,
    tracks: List<Artist.Track>,
    onPlayTrackClick: (TrackId) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        for (track in tracks) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { onPlayTrackClick(track.id) },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(64.dp),
                    url = track.images.small
                )
                Text(
                    text = track.name ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (track != tracks.last()) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun AlbumItem(
    modifier: Modifier = Modifier,
    album: Artist.Album,
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