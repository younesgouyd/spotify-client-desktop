package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.playlist.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.SimplifiedPlaylist

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
    playlists: List<SimplifiedPlaylist>,
    onPlaylistClick: (PlaylistId) -> Unit
) {
    LazyVerticalStaggeredGrid (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        columns = StaggeredGridCells.Adaptive(200.dp)
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
    playlist: SimplifiedPlaylist,
    onPlaylistClick: (PlaylistId) -> Unit
) {
    Item (
        modifier = Modifier.fillMaxSize().padding(8.dp),
        onClick = { onPlaylistClick(playlist.id) }
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
        ) {
            Image(
                modifier = Modifier.size(150.dp),
                url = playlist.images.medium
            )
            Text(playlist.name)
        }
    }
}
