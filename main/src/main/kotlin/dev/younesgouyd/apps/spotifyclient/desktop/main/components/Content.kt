package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.Content
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// todo: find a better name
class Content(
    private val repoStore: RepoStore,
    private val onLogout: () -> Unit
) : Component() {
    override val title: String = ""
    private val mainComponentController = MainComponentController()
    private val playerController: PlayerController = PlayerController(repoStore)

    private val profileNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Profile, onLogout, playerController) }
    private val playlistsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Playlists, onLogout, playerController) }
    private val albumsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Albums, onLogout, playerController) }
    private val artistsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Artists, onLogout, playerController) }
    private val discoverNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Discover, onLogout, playerController) }
    private val settingsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Settings, onLogout, playerController) }
    private val player = Player(
        playerController = playerController,
        showAlbumDetails = mainComponentController::showAlbums,
        showArtistDetails = mainComponentController::showArtists
    )

    private val currentMainComponent = MutableStateFlow(profileNavHost)
    private val selectedNavigationDrawerItem = MutableStateFlow(NavigationDrawerItems.Profile)

    @Composable
    override fun show() {
        val currentMainComponent by currentMainComponent.collectAsState()
        val selectedNavigationDrawerItem by selectedNavigationDrawerItem.collectAsState()

        Content(
            currentMainComponent = currentMainComponent,
            selectedNavigationDrawerItem = selectedNavigationDrawerItem,
            player = player,
            onNavigationDrawerItemClick = {
                when (it) {
                    NavigationDrawerItems.Profile -> mainComponentController.showProfile()
                    NavigationDrawerItems.Playlists -> mainComponentController.showPlaylists()
                    NavigationDrawerItems.Albums -> mainComponentController.showAlbums(null)
                    NavigationDrawerItems.Artists -> mainComponentController.showArtists(null)
                    NavigationDrawerItems.Discover -> mainComponentController.showDiscover()
                    NavigationDrawerItems.Settings -> mainComponentController.showSettings()
                }
            }
        )
    }

    override fun clear() {
        profileNavHost.clear()
        playlistsNavHost.clear()
        albumsNavHost.clear()
        artistsNavHost.clear()
        discoverNavHost.clear()
        settingsNavHost.clear()
        player.clear()
        coroutineScope.cancel()
    }

    enum class NavigationDrawerItems { Profile, Playlists, Albums, Artists, Discover, Settings }

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
    }
}