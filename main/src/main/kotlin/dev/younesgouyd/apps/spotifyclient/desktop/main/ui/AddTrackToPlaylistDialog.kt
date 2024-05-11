package dev.younesgouyd.apps.spotifyclient.desktop.main.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.AddTrackToPlaylistDialogState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

data class Track(
    val id: TrackId,
    val name: String?,
    val imageUrl: ImageUrl?
)

@Composable
fun AddTrackToPlaylistDialog(
    state: AddTrackToPlaylistDialogState,
    track: Track,
    onDismissRequest: () -> Unit
) {
    val playlists by state.playlists.items.collectAsState()
    val loading by state.playlists.loading.collectAsState()
    val lazyColumnState = rememberLazyListState()

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.size(width = 400.dp, height = 600.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    text = "Add to playlist",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(64.dp),
                        url = track.imageUrl
                    )
                    Text(
                        text = track.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    VerticalScrollbar(lazyColumnState)
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        state = lazyColumnState,
                        contentPadding = PaddingValues(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = playlists,
                            key = { it.id }
                        ) { playlistOption ->
                            Item(
                                modifier = Modifier.padding(8.dp),
                                onClick = {
                                    state.onAddTrackTopPlaylist(track.id, playlistOption.id)
                                    onDismissRequest()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        modifier = Modifier.size(64.dp),
                                        url = playlistOption.image
                                    )
                                    Text(
                                        text = playlistOption.name ?: "",
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (loading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 2.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(lazyColumnState) {
        snapshotFlow {
            lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null || it >= (playlists.size + 1) - 5 }
            .filter { it }
            .collect { state.playlists.loadMore() }
    }
}