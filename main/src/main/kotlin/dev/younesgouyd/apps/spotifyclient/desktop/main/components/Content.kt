package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.NavigationHost
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
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

    private val profileNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Profile, onLogout) }
    private val playlistsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Playlists, onLogout) }
    private val albumsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Albums, onLogout) }
    private val artistsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Artists, onLogout) }
    private val settingsNavHost by lazy { NavigationHost(repoStore, NavigationHost.Destination.Settings, onLogout) }

    private val currentMainComponent = MutableStateFlow(profileNavHost)
    private val selectedNavigationDrawerItem = MutableStateFlow(NavigationDrawerItems.Profile)

    @Composable
    override fun show() {
        val currentMainComponent by currentMainComponent.collectAsState()
        val selectedNavigationDrawerItem by selectedNavigationDrawerItem.collectAsState()

        PermanentNavigationDrawer(
            modifier = Modifier.fillMaxSize(),
            drawerContent = {
                PermanentDrawerSheet {
                    NavigationDrawerItems.entries.forEach {
                        NavigationDrawerItem(
                            label = { Text(it.toString()) },
                            selected = it == selectedNavigationDrawerItem,
                            onClick = {
                                when (it) {
                                    NavigationDrawerItems.Profile -> mainComponentController.showProfile()
                                    NavigationDrawerItems.Playlists -> mainComponentController.showPlaylists()
                                    NavigationDrawerItems.Albums -> mainComponentController.showAlbums()
                                    NavigationDrawerItems.Artists -> mainComponentController.showArtists()
                                    NavigationDrawerItems.Settings -> mainComponentController.showSettings()
                                }
                            }
                        )
                    }
                }
            },
            content = { currentMainComponent.show() }
        )
    }

    override fun clear() {
        profileNavHost.clear()
        playlistsNavHost.clear()
        albumsNavHost.clear()
        artistsNavHost.clear()
        settingsNavHost.clear()
        coroutineScope.cancel()
    }

    private enum class NavigationDrawerItems { Profile, Playlists, Albums, Artists, Settings }

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

        fun showAlbums() {
            currentMainComponent.update { albumsNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Albums }
        }

        fun showArtists() {
            currentMainComponent.update { artistsNavHost }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Artists }
        }
    }
}