package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.search.Search
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.search.SearchResult
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Search(
    private val repoStore: RepoStore,
    private val showArtist: (ArtistId) -> Unit,
    private val showAlbum: (AlbumId) -> Unit,
    private val showPlaylist: (PlaylistId) -> Unit,
    private val showUser: (UserId) -> Unit,
    private val playTrack: (TrackId) -> Unit,
    private val playArtist: (ArtistId) -> Unit,
    private val playAlbum: (AlbumId) -> Unit,
    private val playPlaylist: (PlaylistId) -> Unit,
    private val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    private val addTrackToFolderDialogState: AddTrackToFolderDialogState
) : Component() {
    override val title: String = "Search"
    private val searchResult: MutableStateFlow<SearchResult?> = MutableStateFlow(null)
    private val loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @Composable
    override fun show() {
        Search(
            searchResult = searchResult.asStateFlow(),
            loading = loading.asStateFlow(),
            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
            addTrackToFolderDialogState = addTrackToFolderDialogState,
            onSearch = { query ->
                coroutineScope.launch {
                    loading.update { true }
                    searchResult.update {
                        val data = repoStore.searchRepo.search(query)
                        SearchResult(
                            tracks = data.tracks?.items?.filterNotNull()?.let { tracks ->
                                tracks.map { trackObject ->
                                    SearchResult.Track(
                                        id = trackObject.id,
                                        name = trackObject.name,
                                        images = trackObject.album?.images?.toImages() ?: Images.empty(),
                                        artists = trackObject.artists?.filterNotNull()?.map {
                                            SearchResult.Track.Artist(id = it.id, name = it.name)
                                        } ?: emptyList()
                                    )
                                }
                            } ?: emptyList(),
                            artists = data.artists?.items?.filterNotNull()?.let {  artists ->
                                artists.map { artistObject ->
                                    SearchResult.Artist(
                                        id = artistObject.id,
                                        name = artistObject.name,
                                        images = artistObject.images.toImages2()
                                    )
                                }
                            } ?: emptyList(),
                            albums = data.albums?.items?.filterNotNull()?.let { albums ->
                                albums.map { albumObject ->
                                    SearchResult.Album(
                                        id = albumObject.id,
                                        name = albumObject.name,
                                        images = albumObject.images.toImages(),
                                        artists = albumObject.artists?.filterNotNull()?.map {
                                            SearchResult.Album.Artist(id = it.id, name = it.name)
                                        } ?: emptyList()
                                    )
                                }
                            } ?: emptyList(),
                            playlists = data.playlists?.items?.filterNotNull()?.let { playlists ->
                                playlists.map { playlistObject ->
                                    SearchResult.Playlist(
                                        id = playlistObject.id,
                                        name = playlistObject.name,
                                        images = playlistObject.images.toImages(),
                                        owner = playlistObject.owner?.let { SearchResult.Playlist.Owner(id = it.id, name = it.displayName) }
                                    )
                                }
                            } ?: emptyList()
                        )
                    }
                    loading.update { false }
                }
            },
            onTrackClick = playTrack,
            onPlayTrackClick = playTrack,
            onArtistClick = showArtist,
            onPlayArtistClick = playArtist,
            onAlbumClick = showAlbum,
            onPlayAlbumClick = playAlbum,
            onPlaylistClick = showPlaylist,
            onPlaylistOwnerClick = showUser,
            onPlayPlaylistClick = playPlaylist
        )
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}