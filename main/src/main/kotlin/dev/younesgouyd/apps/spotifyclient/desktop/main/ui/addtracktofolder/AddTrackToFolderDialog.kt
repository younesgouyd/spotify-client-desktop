package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.AddTrackToFolderOption
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Folder
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun AddTrackToFolderDialog(
    track: Track,
    state: AddTrackToFolderDialogState,
    onDismissRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var path: Set<Folder?> by remember { mutableStateOf(setOf(null)) }
    val pathLazyListState = rememberLazyListState()
    val folders: SnapshotStateList<AddTrackToFolderOption> = remember { mutableStateListOf() }
    val lazyColumnState = rememberLazyListState()
    var currentCollection: Job? by remember { mutableStateOf<Job?>(null) }

    fun onFolderClick(folder: Folder?) {
        currentCollection?.cancel()
        currentCollection = coroutineScope.launch {
            state.load(folder?.id, track.id).collect {
                folders.clear()
                folders.addAll(it)
            }
        }
        coroutineScope.launch {
            val list = path.drop(1)
                .takeWhile { folder != null && it!!.id != folder.id}
                .toMutableList()
            list.add(0, null)
            list.add(folder)
            path = list.toSet()
            pathLazyListState.animateScrollToItem(path.indices.last)
        }
    }

    LaunchedEffect(Unit) {
        currentCollection?.cancel()
        currentCollection = launch {
            state.load(null, track.id).collect {
                folders.clear()
                folders.addAll(it)
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.size(width = 500.dp, height = 600.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    text = "Add to folder",
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
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        state = pathLazyListState,
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    content = { Text(".") },
                                    onClick = { onFolderClick(null) }
                                )
                                Text("/")
                            }
                        }
                        items(items = path.drop(1), key = { it!!.id }) { folder ->
                            require(folder != null)
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    content = { Text(folder.name) },
                                    onClick = { onFolderClick(folder) }
                                )
                                Text("/")
                            }
                        }
                    }
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
                            items = folders,
                            key = { it.folder.id }
                        ) { folderOption ->
                            Item(
                                modifier = Modifier.padding(8.dp),
                                onClick = { onFolderClick(folderOption.folder) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        modifier = Modifier.size(64.dp).aspectRatio(1f),
                                        imageVector = Icons.Default.Folder,
                                        contentScale = ContentScale.FillWidth,
                                        alignment = Alignment.TopCenter,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = folderOption.folder.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Checkbox(
                                        checked = folderOption.added,
                                        onCheckedChange = {
                                            if (!folderOption.added) {
                                                state.onAddToFolder(track.id, folderOption.folder.id)
                                            } else {
                                                state.onRemoveFromFolder(track.id, folderOption.folder.id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}