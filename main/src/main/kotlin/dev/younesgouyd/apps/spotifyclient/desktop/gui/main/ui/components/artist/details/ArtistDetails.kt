package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Artist

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
        onPlayClick = state.onPlayClick,
        onAlbumClick = state.onAlbumClick,
        onPlayTrackClick = state.onPlayTrackClick
    )
}

@Composable
private fun ArtistDetails(
    artist: Artist,
    topTracks: List<Artist.Track>,
    albums: List<Album>,
    onPlayClick: () -> Unit,
    onAlbumClick: (AlbumId) -> Unit,
    onPlayTrackClick: (TrackId) -> Unit
) {
    LazyVerticalStaggeredGrid (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        columns = StaggeredGridCells.Adaptive(200.dp)
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                ArtistInfo(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant),
                    artist = artist,
                    onPlayClick = onPlayClick
                )
                TopTracks(
                    modifier = Modifier.fillMaxWidth(),
                    tracks = topTracks,
                    onPlayTrackClick = onPlayTrackClick
                )
                Text(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    text = "Discography",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        items(
            items = albums
        ) { item ->
            AlbumItem(
                modifier = Modifier.padding(8.dp),
                album = item,
                onAlbumClick = onAlbumClick
            )
        }
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
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Start
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(url = artist.images.medium)
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleLarge
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
    var topTracksExpanded by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Top Tracks",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    content = {
                        when (topTracksExpanded) {
                            true -> Icon(Icons.Default.ExpandLess, null)
                            false -> Icon(Icons.Default.ExpandMore, null)
                        }
                    },
                    onClick = { topTracksExpanded = !topTracksExpanded }
                )
            }
            if (topTracksExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    for (track in tracks) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(url = track.images.small)
                            Text(
                                modifier = Modifier.weight(1f),
                                text = track.name,
                                style = MaterialTheme.typography.titleSmall
                            )
                            IconButton(
                                content = { Icon(Icons.Default.PlayCircle, null) },
                                onClick = { onPlayTrackClick(track.id) }
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumItem(
    modifier: Modifier = Modifier,
    album: Album,
    onAlbumClick: (AlbumId) -> Unit
) {
    Item (
        modifier = modifier,
        onClick = { onAlbumClick(album.id) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
        ) {
            Image(
                modifier = Modifier.size(150.dp),
                url = album.images.medium
            )
            Text(album.name)
        }
    }
}