package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.artist.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Item
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.ScrollToTopFloatingActionButton
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.VerticalScrollbar
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialog
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Track
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun ArtistDetails(state: ArtistDetailsState) {
    when (state) {
        is ArtistDetailsState.Loading -> Text("Loading...")
        is ArtistDetailsState.State -> ArtistDetails(state)
    }
}

@Composable
private fun ArtistDetails(state: ArtistDetailsState.State) {
    ArtistDetails(
        artist = state.artist,
        followButtonEnabledState = state.followButtonEnabledState,
        topTracks = state.topTracks,
        albums = state.albums,
        addTrackToPlaylistDialogState = state.addTrackToPlaylistDialogState,
        addTrackToFolderDialogState = state.addTrackToFolderDialogState,
        relatedArtists = state.relatedArtists,
        onPlayClick = state.onPlayClick,
        onPlayTrackClick = state.onPlayTrackClick,
        onAlbumClick = state.onAlbumClick,
        onArtistClick = state.onArtistClick,
        onPlayAlbumClick = state.onPlayAlbumClick,
        onArtistFollowStateChange = state.onArtistFollowStateChange
    )
}

private const val CARD_WIDTH_DP = 250

@Composable
private fun ArtistDetails(
    artist: StateFlow<Artist>,
    followButtonEnabledState: StateFlow<Boolean>,
    topTracks: List<Artist.Track>,
    albums: LazilyLoadedItems<Artist.Album, Offset.Index>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    relatedArtists: List<Artist.RelatedArtist>,
    onPlayClick: () -> Unit,
    onArtistFollowStateChange: (state: Boolean) -> Unit,
    onPlayTrackClick: (TrackId) -> Unit,
    onAlbumClick: (AlbumId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onPlayAlbumClick: (AlbumId) -> Unit
) {
    val artist by artist.collectAsState()
    val followButtonEnabledState by followButtonEnabledState.collectAsState()
    val albumItems by albums.items.collectAsState()
    val loadingAlbums by albums.loading.collectAsState()
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                VerticalScrollbar(lazyGridState)
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                    state = lazyGridState,
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    columns = GridCells.Adaptive(CARD_WIDTH_DP.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ArtistInfo(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            artist = artist,
                            followButtonEnabledState = followButtonEnabledState,
                            onPlayClick = onPlayClick,
                            onArtistFollowStateChange = onArtistFollowStateChange
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Top tracks",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TopTracks(
                            modifier = Modifier.fillMaxWidth(),
                            tracks = topTracks,
                            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                            addTrackToFolderDialogState = addTrackToFolderDialogState,
                            onPlayTrackClick = onPlayTrackClick
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Related artists",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        RelatedArtists(
                            modifier = Modifier.fillMaxWidth(),
                            artists = relatedArtists,
                            onArtistClick = onArtistClick
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Discography",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    items(
                        items = albumItems,
                        key = { it.id }
                    ) { item ->
                        AlbumItem(
                            album = item,
                            onClick = onAlbumClick,
                            onArtistClick = onArtistClick,
                            onPlayClick = onPlayAlbumClick
                        )
                    }
                    if (loadingAlbums) {
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
        }.map { it == null || it >= (albumItems.size + 4) - 5 }
            .filter { it }
            .collect { albums.loadMore() }
    }
}

@Composable
private fun ArtistInfo(
    modifier: Modifier = Modifier,
    artist: Artist,
    followButtonEnabledState: Boolean,
    onPlayClick: () -> Unit,
    onArtistFollowStateChange: (state: Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.fillMaxHeight(),
            url = artist.images.preferablyMedium(),
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = artist.name ?: "",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            if (artist.followerCount != null) {
                Text(
                    text = "${artist.followerCount} followers",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            if (artist.genres.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Genres: ${artist.genres.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            if (artist.popularity != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Popularity: ${artist.popularity}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayCircle, null)
                            Text(text = "Play", style = MaterialTheme.typography.labelMedium)
                        }
                    },
                    onClick = onPlayClick
                )
                Button(
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!artist.followed) {
                                Icon(Icons.Default.PersonAdd, null)
                                Text(text = "Follow", style = MaterialTheme.typography.labelMedium)
                            } else {
                                Icon(Icons.Default.PersonRemove, null)
                                Text(text = "Unfollow", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    },
                    onClick = { onArtistFollowStateChange(!artist.followed) },
                    enabled = followButtonEnabledState
                )
            }
        }
    }
}

@Composable
private fun TopTracks(
    modifier: Modifier = Modifier,
    tracks: List<Artist.Track>,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    onPlayTrackClick: (TrackId) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        for (track in tracks) {
            var addToPlaylistDialogVisible by remember { mutableStateOf(false) }
            var addToFolderDialogVisible by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { onPlayTrackClick(track.id) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(64.dp),
                        url = track.images.small
                    )
                    Text(
                        text = track.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row {
                    IconButton(
                        content = { Icon(Icons.Default.Save, null) },
                        onClick = { addToPlaylistDialogVisible = true }
                    )
                    IconButton(
                        content = { Icon(Icons.Default.Folder, null) },
                        onClick = { addToFolderDialogVisible = true }
                    )
                }
            }
            if (track != tracks.last()) {
                HorizontalDivider()
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
}

@Composable
private fun RelatedArtists(
    modifier: Modifier = Modifier,
    artists: List<Artist.RelatedArtist>,
    onArtistClick: (ArtistId) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = artists, key = { it.id }) { artist ->
            Item(
                modifier = Modifier.width(CARD_WIDTH_DP.dp),
                onClick = { onArtistClick(artist.id) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.size(CARD_WIDTH_DP.dp).aspectRatio(1f),
                        url = artist.images.preferablyMedium(),
                        contentScale = ContentScale.FillWidth,
                        alignment = Alignment.TopCenter
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        text = artist.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = artist.followerCount?.let { "$it followers" } ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = artist.popularity?.let { "Popularity: $it" } ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        text = if (artist.genres.isNotEmpty()) "Genres: ${artist.genres.joinToString(separator = ", ")}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumItem(
    modifier: Modifier = Modifier,
    album: Artist.Album,
    onClick: (AlbumId) -> Unit,
    onArtistClick: (ArtistId) -> Unit,
    onPlayClick: (AlbumId) -> Unit
) {
    Item (
        modifier = modifier,
        onClick = { onClick(album.id) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                url = album.images.preferablyMedium(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                text = album.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = album.releaseDate?.let { "Released: $it" } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = album.totalTracks?.let { "$it tracks" } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(items = album.artists, key = { it.id }) { artist ->
                    TextButton(
                        onClick = { onArtistClick(artist.id) },
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, null)
                                Text(artist.name ?: "")
                            }
                        }
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
                    onClick = { onPlayClick(album.id) }
                )
            }
        }
    }
}