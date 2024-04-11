package dev.younesgouyd.apps.spotifyclient.desktop.gui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.Profile
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.Settings
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.album.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.aritst.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components.playlist.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class NavigationHost(
    repoStore: RepoStore,
    startDestination: Destination,
    private val onLogout: () -> Unit
) : Component() {
    override val title: String
    private val destinationFactory: DestinationFactory
    private val navigationController: NavigationController
    private val backStack: BackStack

    init {
        title = ""
        destinationFactory = DestinationFactory(repoStore)
        navigationController = NavigationController()
        backStack = BackStack(
            when (startDestination) {
                Destination.Profile -> destinationFactory.getProfile()
                Destination.Playlists -> destinationFactory.getPlaylistList()
                Destination.Albums -> destinationFactory.getAlbumList()
                Destination.Artists -> destinationFactory.getArtistList()
                Destination.Settings -> destinationFactory.getSettings()
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun show() {
        val currentDestination by backStack.currentDestination.collectAsState()
        val inHome by backStack.inHome.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (!inHome) {
                            IconButton(
                                content = { Icon(Icons.AutoMirrored.Default.ArrowBack, null) },
                                onClick = { navigationController.navigateBack() }
                            )
                        }
                    },
                    title = { Text(text = currentDestination.title, style = MaterialTheme.typography.headlineMedium) }
                )
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    currentDestination.show()
                }
            }
        )
    }

    override fun clear() {
        while (backStack.isNotEmpty()) {
            backStack.top().clear()
            backStack.pop()
        }
        coroutineScope.cancel()
    }

    enum class Destination { Profile, Playlists, Albums, Artists, Settings }

    private inner class BackStack(startDestination: Component) {
        val inHome: MutableStateFlow<Boolean>
        val currentDestination: MutableStateFlow<Component>
        private val stack: Stack<Component>

        init {
            stack = Stack<Component>().apply { push(startDestination) }
            currentDestination = MutableStateFlow(startDestination)
            inHome = MutableStateFlow(true)
        }

        fun push(component: Component) {
            stack.push(component)
            currentDestination.update { stack.peek() }
            inHome.update { false }
        }

        fun pop() {
            stack.pop()
            currentDestination.update { stack.peek() }
            inHome.update { stack.size == 1 }
        }

        fun top(): Component {
            return stack.peek()
        }

        fun isNotEmpty(): Boolean {
            return stack.isNotEmpty()
        }
    }

    private inner class DestinationFactory(private val repoStore: RepoStore) {
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
                showPlaylistDetails = { navigationController.navigateTo(getPlaylistDetails(it)) }
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
                showAlbumDetails = { navigationController.navigateTo(getAlbumDetails(it)) }
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
                showArtistDetails = { navigationController.navigateTo(getArtistDetails(it)) }
            )
        }

        fun getArtistDetails(id: ArtistId): ArtistDetails {
            return ArtistDetails(
                id = id,
                repoStore = repoStore,
                showAlbumDetails = { navigationController.navigateTo(getAlbumDetails(it)) }
            )
        }
    }

    private inner class NavigationController {
        fun navigateTo(destination: Component) {
            backStack.push(destination)
        }

        fun navigateBack() {
            backStack.top().clear()
            backStack.pop()
        }
    }
}
