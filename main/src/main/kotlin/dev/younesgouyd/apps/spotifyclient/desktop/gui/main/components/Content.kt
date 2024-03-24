package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.AlbumId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ArtistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.PlaylistId
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.NavigationDrawerItems
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// todo: find a better name
class Content(
    private val repoStore: RepoStore,
    private val onLogout: () -> Unit
) : Component() {
    private val mainComponentController = MainComponentController()
    private val componentsFactory = ComponentsFactory()
    private val currentMainComponent = MutableStateFlow<Component>(componentsFactory.getProfile())
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
                                    NavigationDrawerItems.Playlists -> mainComponentController.showPlaylistList()
                                    NavigationDrawerItems.Albums -> mainComponentController.showAlbumList()
                                    NavigationDrawerItems.Artists -> mainComponentController.showArtistList()
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

    private inner class MainComponentController {
        fun showSettings() {
            currentMainComponent.update { componentsFactory.getSettings() }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Settings }
        }

        fun showProfile() {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getProfile()
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Profile }
        }

        fun showPlaylistList() {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getPlaylistList()
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Playlists }
        }

        fun showPlaylistDetails(id: PlaylistId) {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getPlaylistDetails(id)
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Playlists }
        }

        fun showAlbumList() {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getAlbumList()
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Albums }
        }

        fun showAlbumDetails(id: AlbumId) {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getAlbumDetails(id)
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Albums }
        }

        fun showArtistList() {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getArtistList()
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Artists }
        }

        fun showArtistDetails(id: ArtistId) {
            currentMainComponent.update {
                it.clear()
                componentsFactory.getArtistDetails(id)
            }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Artists }
        }
    }

    private inner class ComponentsFactory {
        fun getSettings(): Settings {
            return Settings(
                repoStore = repoStore
            )
        }

        fun getProfile(): Profile {
            return Profile(
                repoStore = repoStore,
                onLogout = onLogout
            )
        }

        fun getPlaylistList(): PlaylistList {
            return PlaylistList(
                repoStore = repoStore,
                showPlaylistDetails = { mainComponentController.showPlaylistDetails(it) }
            )
        }

        fun getPlaylistDetails(id: PlaylistId): PlaylistDetails {
            return PlaylistDetails(
                id = id,
                repoStore = repoStore,
            )
        }

        fun getAlbumList(): AlbumList {
            return AlbumList(
                repoStore = repoStore,
                showAlbumDetails = { mainComponentController.showAlbumDetails(it) }
            )
        }

        fun getAlbumDetails(id: AlbumId): AlbumDetails {
            return AlbumDetails(
                id = id,
                repoStore = repoStore,
            )
        }

        fun getArtistList(): ArtistList {
            return ArtistList(
                repoStore = repoStore,
                showArtistDetails = { mainComponentController.showArtistDetails(it) }
            )
        }

        fun getArtistDetails(id: ArtistId): ArtistDetails {
            return ArtistDetails(
                id = id,
                repoStore = repoStore,
                showAlbumDetails = { mainComponentController.showAlbumDetails(it) }
            )
        }
    }

    override fun clear() {
        currentMainComponent.value.clear()
        coroutineScope.cancel()
    }
}