package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Artist
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

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
        loadingArtists = state.loadingArtists,
        onLoadArtists = state.onLoadArtists,
        onArtistClick = state.onArtistClick
    )
}

@Composable
private fun ArtistList(
    artists: StateFlow<List<Artist>>,
    loadingArtists: StateFlow<Boolean>,
    onLoadArtists: () -> Unit,
    onArtistClick: (ArtistId) -> Unit
) {
    val artists by artists.collectAsState()
    val loadingArtists by loadingArtists.collectAsState()
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                VerticalScrollbar(lazyGridState)
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyGridState,
                    contentPadding = PaddingValues(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    columns = GridCells.Adaptive(250.dp)
                ) {
                    items(
                        items = artists,
                        key = { it.id }
                    ) { item ->
                        ArtistItem(
                            artist = item,
                            onArtistClick = onArtistClick
                        )
                    }
                    if (loadingArtists) {
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
        }.map { it == null || it >= artists.size - 5 }
            .filter { it }
            .collect { onLoadArtists() }
    }
}

@Composable
private fun ArtistItem(
    modifier: Modifier = Modifier,
    artist: Artist,
    onArtistClick: (ArtistId) -> Unit
) {
    Item(
        modifier = modifier,
        onClick = { onArtistClick(artist.id) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                url = artist.images.preferablyMedium(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                text = artist.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
