package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.album.list

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
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Album

@Composable
fun AlbumList(state: AlbumListState) {
    when (state) {
        is AlbumListState.Loading -> Text("Loading...")
        is AlbumListState.State -> AlbumList(state)
    }
}

@Composable
private fun AlbumList(state: AlbumListState.State) {
    AlbumList(
        albums = state.albums,
        onAlbumClick = state.onAlbumClick
    )
}

@Composable
private fun AlbumList(
    albums: List<Album>,
    onAlbumClick: (AlbumId) -> Unit
) {
    LazyVerticalStaggeredGrid (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalItemSpacing = 18.dp,
        columns = StaggeredGridCells.Adaptive(200.dp)
    ) {
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
            Image(
                modifier = Modifier.width(300.dp),
                url = album.images.preferablyMedium()
            )
            Text(
                modifier = Modifier.padding(12.dp),
                text = album.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
