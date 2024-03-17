package dev.younesgouyd.apps.spotifyclient.desktop.gui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.Login
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.Profile
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.DARK_MODE_SETTING
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.NavigationDrawerItems
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object Application {
    private val currentMainComponent = MutableStateFlow<Component?>(null)
    private val selectedNavigationDrawerItem = MutableStateFlow<NavigationDrawerItems?>(null)
    private val repoStore = RepoStore()

    fun start() {
        MainComponentController.showLogin()
        application {
            Window(
                state = rememberWindowState(
                    placement = WindowPlacement.Maximized,
                    position = WindowPosition(Alignment.Center)
                ),
                onCloseRequest = ::exitApplication,
            ) {
                val currentMainComponent by currentMainComponent.collectAsState()
                val selectedNavigationDrawerItem by selectedNavigationDrawerItem.collectAsState()

                Theme(
                    darkTheme = DARK_MODE_SETTING,
                    content = {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PermanentNavigationDrawer(
                                    modifier = Modifier.fillMaxWidth().weight(.75f),
                                    drawerContent = {
                                        PermanentDrawerSheet {
                                            NavigationDrawerItems.entries.forEach {
                                                NavigationDrawerItem(
                                                    label = { Text(it.toString()) },
                                                    selected = it == selectedNavigationDrawerItem,
                                                    onClick = {
                                                        when (it) {
                                                            NavigationDrawerItems.Profile -> MainComponentController.showProfile()
                                                            NavigationDrawerItems.Playlists -> MainComponentController.showPlaylistList()
                                                            NavigationDrawerItems.Albums -> MainComponentController.showAlbumList()
                                                            NavigationDrawerItems.Artists -> MainComponentController.showArtistList()
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    },
                                    content = { currentMainComponent!!.show() }
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    object MainComponentController {
        fun showLogin() {
            currentMainComponent.update { ComponentsFactory.getLogin() }
        }

        fun showProfile() {
            currentMainComponent.update { ComponentsFactory.getProfile() }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Profile }
        }

        fun showPlaylistList() {
            currentMainComponent.update { ComponentsFactory.getPlaylistList() }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Playlists }
        }

        fun showPlaylistDetails(id: PlaylistId) {
            currentMainComponent.update { ComponentsFactory.getPlaylistDetails(id) }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Playlists }
        }

        fun showAlbumList() {
            currentMainComponent.update { ComponentsFactory.getAlbumList() }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Albums }
        }

        fun showAlbumDetails(id: AlbumId) {
            currentMainComponent.update { ComponentsFactory.getAlbumDetails(id) }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Albums }
        }

        fun showArtistList() {
            currentMainComponent.update { ComponentsFactory.getArtistList() }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Artists }
        }

        fun showArtistDetails(id: ArtistId) {
            currentMainComponent.update { ComponentsFactory.getArtistDetails(id) }
            selectedNavigationDrawerItem.update { NavigationDrawerItems.Artists }
        }
    }

    private object ComponentsFactory {
        fun getLogin(): Login {
            return Login(
                repoStore = repoStore,
                toCurrentUserProfile = { MainComponentController.showProfile() }
            )
        }

        fun getProfile(): Profile {
            return Profile(
                repoStore = repoStore
            )
        }

        fun getPlaylistList(): PlaylistList {
            return PlaylistList(
                repoStore = repoStore,
                showPlaylistDetails = { MainComponentController.showPlaylistDetails(it) }
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
                showAlbumDetails = { MainComponentController.showAlbumDetails(it) }
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
                showArtistDetails = { MainComponentController.showArtistDetails(it) }
            )
        }

        fun getArtistDetails(id: ArtistId): ArtistDetails {
            return ArtistDetails(
                id = id,
                repoStore = repoStore,
                showAlbumDetails = { MainComponentController.showAlbumDetails(it) }
            )
        }
    }
}
