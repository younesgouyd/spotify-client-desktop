package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalItemSpacing = 18.dp,
        columns = StaggeredGridCells.FixedSize(200.dp)
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
                    modifier = Modifier.fillMaxWidth(),
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
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        items(
            items = albums
        ) { item ->
            AlbumItem(
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
                text = artist.name,
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = "Top Tracks",
            style = MaterialTheme.typography.headlineMedium
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (track != tracks.last()) {
                    HorizontalDivider()
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
                text = album.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}