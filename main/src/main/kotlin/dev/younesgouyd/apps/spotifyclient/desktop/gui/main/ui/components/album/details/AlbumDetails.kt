package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album

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
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@Composable
private fun AlbumDetails(
    album: Album,
    tracks: List<Album.Track>,
    onPlayClick: () -> Unit,
    onTrackClick: (TrackId) -> Unit
) {
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AlbumInfo(album, onPlayClick)
        }
        items(
            items = tracks
        ) { item ->
            TrackItem(
                track = item,
                onTrackClick = onTrackClick
            )
        }
    }
}

@Composable
private fun AlbumInfo(
    album: Album,
    onPlayClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(300.dp),
            url = album.images.large
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleLarge
                )
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
    track: Album.Track,
    onTrackClick: (TrackId) -> Unit
) {
    Item(
        modifier = Modifier.padding(8.dp),
        onClick = { onTrackClick(track.id) },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = track.name,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
