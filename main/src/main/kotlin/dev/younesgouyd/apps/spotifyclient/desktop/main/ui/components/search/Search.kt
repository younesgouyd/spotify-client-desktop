package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Search(
    searchResult: StateFlow<SearchResult?>,
    loading: StateFlow<Boolean>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onSearch: (query: String) -> Unit,
    onTrackClick: (TrackId) -> Unit,
    onPlayTrackClick: (TrackId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onPlayArtistClick: (ArtistId) -> Unit,
    onAlbumClick: (AlbumId) -> Unit,
    onPlayAlbumClick: (AlbumId) -> Unit,
    onPlaylistClick: (PlaylistId) -> Unit,
    onPlaylistOwnerClick: (UserId) -> Unit,
    onPlayPlaylistClick: (PlaylistId) -> Unit
) {
    val types = remember {
        mutableStateListOf<SearchType>().apply { addAll(SearchType.entries) }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchForm(modifier = Modifier.fillMaxWidth().padding(12.dp), types = types, onSearch = onSearch)
        SearchResult(
            modifier = Modifier.fillMaxWidth(),
            searchResult = searchResult,
            types = types.toList(),
            loading = loading,
            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
            addTrackToFolderDialogState = addTrackToFolderDialogState,
            onTrackClick = onTrackClick,
            onPlayTrackClick = onPlayTrackClick,
            onArtistClick = onArtistClick,
            onPlayArtistClick = onPlayArtistClick,
            onAlbumClick = onAlbumClick,
            onPlayAlbumClick = onPlayAlbumClick,
            onPlaylistClick = onPlaylistClick,
            onPlaylistOwnerClick = onPlaylistOwnerClick,
            onPlayPlaylistClick = onPlayPlaylistClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchForm(
    modifier: Modifier = Modifier,
    types: SnapshotStateList<SearchType>,
    onSearch: (query: String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            value = query,
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
            onValueChange = { query = it },
            trailingIcon = {
                IconButton(
                    content = { Icon(Icons.Default.Clear, null) },
                    onClick = { query = "" }
                )
            }
        )
        MultiChoiceSegmentedButtonRow {
            for (type in SearchType.entries) {
                SegmentedButton(
                    label = { Text(text = type.name, style = MaterialTheme.typography.labelMedium) },
                    checked = types.contains(type),
                    onCheckedChange = { if (types.contains(type)) types.remove(type) else types.add(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = SearchType.entries.indexOf(type),
                        count = SearchType.entries.size
                    )
                )
            }
        }
    }
}

@Composable
private fun SearchResult(
    modifier: Modifier = Modifier,
    searchResult: StateFlow<SearchResult?>,
    types: List<SearchType>,
    loading: StateFlow<Boolean>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onTrackClick: (TrackId) -> Unit,
    onPlayTrackClick: (TrackId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onPlayArtistClick: (ArtistId) -> Unit,
    onAlbumClick: (AlbumId) -> Unit,
    onPlayAlbumClick: (AlbumId) -> Unit,
    onPlaylistClick: (PlaylistId) -> Unit,
    onPlaylistOwnerClick: (UserId) -> Unit,
    onPlayPlaylistClick: (PlaylistId) -> Unit
) {
    val searchResult by searchResult.collectAsState()
    val loading by loading.collectAsState()

    if (loading) {
        Text(modifier = modifier, text = "Loading...")
    } else {
        searchResult?.let { result ->
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (types.contains(SearchType.Track)) {
                    item {
                        ResultItems<SearchResult.Track>(
                            modifier = Modifier.fillMaxWidth(),
                            headline = "Tracks",
                            items = result.tracks,
                            key = { it.id },
                            onItemClick = { onTrackClick(it.id) },
                            itemContent = { track ->
                                TrackItemContent(
                                    track = track,
                                    addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                                    addTrackToFolderDialogState = addTrackToFolderDialogState,
                                    onArtistClick = onArtistClick,
                                    onPlayClick = { onPlayTrackClick(track.id) }
                                )
                            }
                        )
                    }
                }
                if (types.contains(SearchType.Artist)) {
                    item {
                        ResultItems<SearchResult.Artist>(
                            modifier = Modifier.fillMaxWidth(),
                            headline = "Artists",
                            items = result.artists,
                            key = { it.id },
                            onItemClick = { onArtistClick(it.id) },
                            itemContent = { ArtistItemContent(artist = it, onPlayClick = { onPlayArtistClick(it.id) }) }
                        )
                    }
                }
                if (types.contains(SearchType.Album)) {
                    item {
                        ResultItems<SearchResult.Album>(
                            modifier = Modifier.fillMaxWidth(),
                            headline = "Albums",
                            items = result.albums,
                            key = { it.id },
                            onItemClick = { onAlbumClick(it.id) },
                            itemContent = {
                                AlbumItemContent(
                                    album = it,
                                    onArtistClick = onArtistClick,
                                    onPlayClick = { onPlayAlbumClick(it.id) }
                                )
                            }
                        )
                    }
                }
                if (types.contains(SearchType.Playlist)) {
                    item {
                        ResultItems<SearchResult.Playlist>(
                            modifier = Modifier.fillMaxWidth(),
                            headline = "Playlists",
                            items = result.playlists,
                            key = { it.id },
                            onItemClick = { onPlaylistClick(it.id) },
                            itemContent = {
                                PlaylistItemContent(
                                    playlist = it,
                                    onOwnerClick = onPlaylistOwnerClick,
                                    onPlayClick = { onPlayPlaylistClick(it.id) }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> ResultItems(
    modifier: Modifier = Modifier,
    headline: String,
    items: List<T>,
    key: (item: T) -> Any,
    onItemClick: (item: T) -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = headline,
            style = MaterialTheme.typography.headlineMedium
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(12.dp)
        ) {
            items(items = items, key = key) { item ->
                Item(
                    modifier = Modifier.width(250.dp),
                    onClick = { onItemClick(item) },
                    content = { itemContent(item) }
                )
            }
        }
    }
}

@Composable
private fun TrackItemContent(
    modifier: Modifier = Modifier,
    track: SearchResult.Track,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: () -> Unit
) {
    var addToPlaylistDialogVisible by remember { mutableStateOf(false) }
    var addToFolderDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier.aspectRatio(1f),
            url = track.images.preferablyMedium(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = track.name ?: "",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(items = track.artists, key = { it.id }) { artist ->
                TextButton(
                    content = { Text(text = artist.name ?: "", style = MaterialTheme.typography.labelMedium) },
                    onClick = { onArtistClick(artist.id) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = { Icon(Icons.Default.Save, null) },
                onClick = { addToPlaylistDialogVisible = true }
            )
            IconButton(
                content = { Icon(Icons.Default.Folder, null) },
                onClick = { addToFolderDialogVisible = true }
            )
            IconButton(
                content = { Icon(Icons.Default.PlayCircle, null) },
                onClick = onPlayClick
            )
        }

        if (addToPlaylistDialogVisible) {
            AddTrackToPlaylistDialog(
                state = addTrackToPlaylistDialogState,
                track = Track(track.id, track.name, track.images.preferablySmall()),
                onDismissRequest = { addToPlaylistDialogVisible = false }
            )
        }

        if (addToFolderDialogVisible) {
            AddTrackToFolderDialog(
                track = Track(track.id, track.name, track.images.preferablySmall()),
                state = addTrackToFolderDialogState,
                onDismissRequest = { addToFolderDialogVisible = false }
            )
        }
    }
}

@Composable
private fun ArtistItemContent(
    modifier: Modifier = Modifier,
    artist: SearchResult.Artist,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = { Icon(Icons.Default.PlayCircle, null) },
                onClick = onPlayClick
            )
        }
    }
}

@Composable
private fun AlbumItemContent(
    modifier: Modifier = Modifier,
    album: SearchResult.Album,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier.aspectRatio(1f),
            url = album.images.preferablyMedium(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = album.name ?: "",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(items = album.artists, key = { it.id }) { artist ->
                TextButton(
                    content = { Text(text = artist.name ?: "", style = MaterialTheme.typography.labelMedium) },
                    onClick = { onArtistClick(artist.id) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = { Icon(Icons.Default.PlayCircle, null) },
                onClick = onPlayClick
            )
        }
    }
}

@Composable
private fun PlaylistItemContent(
    modifier: Modifier = Modifier,
    playlist: SearchResult.Playlist,
    onOwnerClick: (UserId) -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier.aspectRatio(1f),
            url = playlist.images.preferablyMedium(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = playlist.name ?: "",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        TextButton(
            modifier = Modifier.padding(horizontal = 4.dp),
            content = { Text(text = playlist.owner?.name ?: "", style = MaterialTheme.typography.labelMedium) },
            onClick = { playlist.owner?.let { onOwnerClick(it.id) } }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = { Icon(Icons.Default.PlayCircle, null) },
                onClick = onPlayClick
            )
        }
    }
}
