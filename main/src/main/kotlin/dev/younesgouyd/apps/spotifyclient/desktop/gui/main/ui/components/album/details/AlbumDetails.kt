package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AlbumInfo(
                modifier = Modifier.fillMaxWidth(),
                album = album,
                onPlayClick = onPlayClick
            )
        }
        items(
            items = tracks
        ) { item ->
            TrackItem(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                track = item,
                onTrackClick = onTrackClick
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun AlbumInfo(
    modifier: Modifier,
    album: Album,
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
