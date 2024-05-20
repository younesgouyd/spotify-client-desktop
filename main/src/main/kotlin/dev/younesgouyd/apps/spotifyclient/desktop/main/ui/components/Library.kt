package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.FolderId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Folder
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Library(
    path: StateFlow<Set<Folder>>,
    loadingItems: StateFlow<Boolean>,
    folders: StateFlow<List<Folder>>,
    tracks: StateFlow<List<Folder.Track>>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    onNewFolder: (name: String) -> Unit,
    onFolderClick: (Folder?) -> Unit,
    onRenameFolder: (FolderId, name: String) -> Unit,
    onDeleteFolder: (FolderId) -> Unit,
    onTrackClick: (TrackId) -> Unit,
    onRemoveTrack: (TrackId) -> Unit
) {
    val path by path.collectAsState()
    val loadingItems by loadingItems.collectAsState()
    val folders by folders.collectAsState()
    val tracks by tracks.collectAsState()
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                VerticalScrollbar(lazyGridState)
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyGridState,
                    contentPadding = PaddingValues(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    columns = GridCells.Adaptive(250.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ToolBar(
                            modifier = Modifier.fillMaxWidth(),
                            path = path,
                            onFolderClick = onFolderClick,
                            onNewFolder = onNewFolder
                        )
                    }
                    items(items = folders, key = { it.id }) { folder ->
                        FolderItem(
                            folder = folder,
                            onClick = { onFolderClick(folder) },
                            onRenameFolder = { onRenameFolder(folder.id, it) },
                            onRemoveClick = { onDeleteFolder(folder.id) }
                        )
                    }
                    items(items = tracks, key = { it.id }) {
                        TrackItem(
                            track = it,
                            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                            onClick = { onTrackClick(it.id) },
                            onRemoveClick = { onRemoveTrack(it.id) }
                        )
                    }
                    if (loadingItems) {
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
}

@Composable
private fun ToolBar(
    modifier: Modifier = Modifier,
    path: Set<Folder>,
    onFolderClick: (Folder?) -> Unit,
    onNewFolder: (name: String) -> Unit
) {
    val pathLazyListState = rememberLazyListState()
    var newFolderFormVisible by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            LazyRow(
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
                items(items = path.toList(), key = { it.id }) { folder ->
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
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* todo - sort by alpha */ },
                    content = { Icon(Icons.Default.SortByAlpha, null) }
                )
                IconButton(
                    onClick = { /* todo - sort by date */ },
                    content = { Icon(Icons.AutoMirrored.Default.Sort, null) }
                )
                Text("|") // todo - use VerticalDivider
                IconButton(
                    onClick = { newFolderFormVisible = true },
                    content = { Icon(Icons.Default.Add, null) }
                )
            }
        }
    }

    if (newFolderFormVisible) {
        FolderForm(
            onDismissRequest = { newFolderFormVisible = false },
            onDone = onNewFolder
        )
    }

    LaunchedEffect(path) {
        pathLazyListState.animateScrollToItem(path.size)
    }
}

@Composable
private fun FolderItem(
    modifier: Modifier = Modifier,
    folder: Folder,
    onClick: () -> Unit,
    onRenameFolder: (name: String) -> Unit,
    onRemoveClick: () -> Unit
) {
    var deleteConfirmationDialogVisible by remember { mutableStateOf(false) }
    var editFormDialogVisible by remember { mutableStateOf(false) }

    Item(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                imageVector = Icons.Default.Folder,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
                contentDescription = null
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                text = folder.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { editFormDialogVisible = true },
                    content = { Icon(Icons.Default.Edit, null) }
                )
                IconButton(
                    onClick = { deleteConfirmationDialogVisible = true },
                    content = { Icon(Icons.Default.Remove, null) }
                )
            }
        }
    }

    if (editFormDialogVisible) {
        FolderForm(
            name = folder.name,
            onDismissRequest = { editFormDialogVisible = false },
            onDone = onRenameFolder
        )
    }

    if (deleteConfirmationDialogVisible) {
        DeleteConfirmationDialog(
            message = "Remove \"${folder.name}\" from this folder?",
            onDismissRequest = { deleteConfirmationDialogVisible = false },
            onYesClick = onRemoveClick
        )
    }
}

@Composable
private fun TrackItem(
    modifier: Modifier = Modifier,
    track: Folder.Track,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    var addToPlaylistDialogVisible by remember { mutableStateOf(false) }
    var deleteConfirmationDialogVisible by remember { mutableStateOf(false) }

    Item(modifier = modifier, onClick = onClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image(
                modifier = Modifier.aspectRatio(1f),
                url = track.images.preferablyMedium(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                text = track.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    content = { Icon(Icons.Default.Save, null) },
                    onClick = { addToPlaylistDialogVisible = true }
                )
                IconButton(
                    onClick = { deleteConfirmationDialogVisible = true },
                    content = { Icon(Icons.Default.Remove, null) }
                )
            }
        }
    }

    if (addToPlaylistDialogVisible) {
        AddTrackToPlaylistDialog(
            state = addTrackToPlaylistDialogState,
            track = Track(track.id, track.name, track.images.preferablySmall()),
            onDismissRequest = { addToPlaylistDialogVisible = false }
        )
    }

    if (deleteConfirmationDialogVisible) {
        DeleteConfirmationDialog(
            message = "Remove \"${track.name}\" from this folder?",
            onDismissRequest = { deleteConfirmationDialogVisible = false },
            onYesClick = onRemoveClick
        )
    }
}

@Composable
private fun FolderForm(
    name: String = "",
    onDismissRequest: () -> Unit,
    onDone: (name: String) -> Unit
) {
    var name by remember { mutableStateOf(name) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.width(500.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    text = "New folder",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onDone(name); onDismissRequest() }),
                )
                Button(
                    content = { Text("Done") },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDone(name)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    message: String,
    onDismissRequest: () -> Unit,
    onYesClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.width(500.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        content = { Text("Yes") },
                        onClick = onYesClick
                    )
                    Button(
                        content = { Text("No") },
                        onClick = onDismissRequest
                    )
                }
            }
        }
    }
}