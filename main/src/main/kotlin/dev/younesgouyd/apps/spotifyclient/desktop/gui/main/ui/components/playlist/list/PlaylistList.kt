package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.PlaylistListItem

@Composable
fun PlaylistList(state: PlaylistListState) {
    when (state) {
        is PlaylistListState.Loading -> Text("Loading...")
        is PlaylistListState.State -> PlaylistList(state)
    }
}

@Composable
private fun PlaylistList(state: PlaylistListState.State) {
    PlaylistList(
        playlists = state.playlists,
        onPlaylistClick = state.onPlaylistClick
    )
}

@Composable
private fun PlaylistList(
    playlists: List<PlaylistListItem>,
    onPlaylistClick: (PlaylistId) -> Unit
) {
    LazyVerticalStaggeredGrid (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalItemSpacing = 18.dp,
        columns = StaggeredGridCells.FixedSize(200.dp)
    ) {
        items(
            items = playlists
        ) { item ->
            PlaylistItem(
                playlist = item,
                onPlaylistClick = onPlaylistClick
            )
        }
    }
}

@Composable
private fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: PlaylistListItem,
    onPlaylistClick: (PlaylistId) -> Unit
) {
    Item (
        modifier = modifier,
        onClick = { onPlaylistClick(playlist.id) }
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
                    url = playlist.images.preferablyMedium()
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                text = playlist.name ?: "",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
