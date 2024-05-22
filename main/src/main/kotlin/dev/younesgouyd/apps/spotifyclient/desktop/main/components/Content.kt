package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.Content
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.PlaylistOption
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// todo: find a better name
class Content(
    private val repoStore: RepoStore,
    private val onLogout: () -> Unit
) : Component() {
    override val title: String = ""
    private val mainComponentController = MainComponentController()
    private val playerController: PlayerController = PlayerController(repoStore.playbackRepo)
    private val darkTheme: StateFlow<DarkThemeOptions> = repoStore.settingsRepo.getDarkThemeFlow().filterNotNull().stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DarkThemeOptions.SystemDefault
    )

    private val addTrackToPlaylistDialogState = AddTrackToPlaylistDialogState(
        playlists = LazilyLoadedItems<PlaylistOption, Offset.Index>(
            coroutineScope = coroutineScope,
            load = repoStore.playlistRepo::getPlaylistOptions,
            initialOffset = Offset.Index.initial()
        ),
        onAddTrackTopPlaylist = { trackId, playlistId ->
            coroutineScope.launch {
                repoStore.playlistRepo.addItems(playlistId, listOf(trackId.toUri()))
            }
        }
    )
    private val addTrackToFolderDialogState = AddTrackToFolderDialogState(
        load = repoStore.folderRepo::getAddTrackToFolderOptions,
        onAddToFolder = { trackId, folderId ->
            coroutineScope.launch { repoStore.folderRepo.addTrackToFolder(trackId, folderId) }
        },
        onRemoveFromFolder = { trackId, folderId ->
            coroutineScope.launch { repoStore.folderRepo.removeTrackFromFolder(trackId, folderId) }
        }
    )

    private val profileNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Profile, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val playlistsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Playlists, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val albumsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Albums, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val artistsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Artists, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val discoverNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Discover, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val searchNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Search, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val library by lazy { Library(repoStore.folderRepo, repoStore.trackRepo, playTrack = { coroutineScope.launch { playerController.play(uris = listOf(it.toUri())) } }, addTrackToPlaylistDialogState) }
    private val settingsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Settings, playerController, addTrackToPlaylistDialogState, addTrackToFolderDialogState, onLogout) }
    private val player = Player(
        playerController = playerController,
        showAlbumDetails = mainComponentController::showAlbums,
        showArtistDetails = mainComponentController::showArtists,
        addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
        addTrackToFolderDialogState = addTrackToFolderDialogState
    )

    private val currentMainComponent: MutableStateFlow<Component> = MutableStateFlow(profileNavHost)
    private val selectedNavigationDrawerItem = MutableStateFlow(NavigationDrawerItems.Profile)

    @Composable
    override fun show() {
        val currentMainComponent by currentMainComponent.collectAsState()
        val selectedNavigationDrawerItem by selectedNavigationDrawerItem.collectAsState()
        val darkTheme by darkTheme.collectAsState()

        Content(
            darkTheme = darkTheme,
            currentMainComponent = currentMainComponent,
            player = player,
            selectedNavigationDrawerItem = selectedNavigationDrawerItem,
            onNavigationDrawerItemClick = {
                when (it) {
                    NavigationDrawerItems.Profile -> mainComponentController.showProfile()
                    NavigationDrawerItems.Playlists -> mainComponentController.showPlaylists()
                    NavigationDrawerItems.Albums -> mainComponentController.showAlbums(null)
                    NavigationDrawerItems.Artists -> mainComponentController.showArtists(null)
                    NavigationDrawerItems.Discover -> mainComponentController.showDiscover()
                    NavigationDrawerItems.Search -> mainComponentController.showSearch()
                    NavigationDrawerItems.Library -> mainComponentController.showLibrary()
                    NavigationDrawerItems.Settings -> mainComponentController.showSettings()
                }
            },
            onRefreshPlayer = { coroutineScope.launch { playerController.refresh() } }
        )
    }

    override fun clear() {
        profileNavHost.clear()
        playlistsNavHost.clear()
        albumsNavHost.clear()
        artistsNavHost.clear()
        discoverNavHost.clear()
        searchNavHost.clear()
        library.clear()
        settingsNavHost.clear()
        player.clear()
        coroutineScope.cancel()
    }

    enum class NavigationDrawerItems { Profile, Playlists, Albums, Artists, Discover, Search, Library, Settings }

    private inner class MainComponentController {
        fun showSettings() {
            currentMainComponent.update { settingsNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Settings }
        }

        fun showProfile() {
            currentMainComponent.update { profileNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Profile }
        }

        fun showPlaylists() {
            currentMainComponent.update { playlistsNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Playlists }
        }

        fun showAlbums(id: AlbumId?) {
            currentMainComponent.update { albumsNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Albums }
            if (id != null) { albumsNavHost.toAlbumDetails(id) }
        }

        fun showArtists(id: ArtistId?) {
            currentMainComponent.update { artistsNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Artists }
            if (id != null) { artistsNavHost.toArtistDetails(id) }
        }

        fun showDiscover() {
            currentMainComponent.update { discoverNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Discover }
        }

        fun showSearch() {
            currentMainComponent.update { searchNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Search }
        }

        fun showLibrary() {
            currentMainComponent.update { library }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Library }
        }
    }
}