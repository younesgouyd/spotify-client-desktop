package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.Search
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.SearchResult
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
    private val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState
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
            onSearchClick = { query ->
                coroutineScope.launch {
                    loading.update { true }
                    searchResult.update { repoStore.searchRepo.search(query) }
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