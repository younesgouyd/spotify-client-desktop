package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.details

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
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Playlist

@Composable
fun PlaylistDetails(state: PlaylistDetailsState) {
    when (state) {
        is PlaylistDetailsState.Loading -> Text("Loading playlist...")
        is PlaylistDetailsState.State -> PlaylistDetails(state)
    }
}

@Composable
private fun PlaylistDetails(state: PlaylistDetailsState.State) {
    PlaylistDetails(
        playlist = state.playlist,
        tracks = state.tracks,
        onPlayClick = state.onPlayClick,
        onTrackClick = state.onTrackClick
    )
}

@Composable
private fun PlaylistDetails(
    playlist: Playlist,
    tracks: List<Playlist.Track>,
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
            PlaylistInfo(playlist, onPlayClick)
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
private fun PlaylistInfo(
    playlist: Playlist,
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
            url = playlist.imageUrl
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
                    text = playlist.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodySmall
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
    track: Playlist.Track,
    onTrackClick: (TrackId) -> Unit
) {
    Item(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        onClick = { onTrackClick(track.id) },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(150.dp),
                url = track.images.medium
            )
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
