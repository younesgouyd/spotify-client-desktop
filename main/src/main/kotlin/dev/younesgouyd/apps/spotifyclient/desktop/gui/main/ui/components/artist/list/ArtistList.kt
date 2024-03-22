package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.artist.list

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
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.Artist

@Composable
fun ArtistList(state: ArtistListState) {
    when (state) {
        is ArtistListState.Loading -> Text("Loading...")
        is ArtistListState.State -> ArtistList(state)
    }
}

@Composable
private fun ArtistList(state: ArtistListState.State) {
    ArtistList(
        artists = state.artists,
        onArtistClick = state.onArtistClick
    )
}

@Composable
private fun ArtistList(
    artists: List<Artist>,
    onArtistClick: (ArtistId) -> Unit
) {
    LazyVerticalStaggeredGrid (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalItemSpacing = 18.dp,
        columns = StaggeredGridCells.Adaptive(200.dp)
    ) {
        items(
            items = artists
        ) { item ->
            ArtistItem(
                artist = item,
                onArtistClick = onArtistClick
            )
        }
    }
}

@Composable
private fun ArtistItem(
    artist: Artist,
    onArtistClick: (ArtistId) -> Unit
) {
    Item(
        onClick = { onArtistClick(artist.id) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.width(300.dp),
                url = artist.images.preferablyMedium()
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = artist.name,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
