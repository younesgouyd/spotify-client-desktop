package dev.younesgouyd.apps.spotifyclient.desktop.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.album.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.album.AlbumList
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst.ArtistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.aritst.ArtistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.playlist.PlaylistDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.playlist.PlaylistList
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class NavigationHost(
    repoStore: RepoStore,
    startDestination: Destination,
    private val onLogout: () -> Unit,
    private val playerController: PlayerController
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
                Destination.Discover -> destinationFactory.getDiscover()
                Destination.Search -> destinationFactory.getSearch()
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

    fun toAlbumDetails(id: AlbumId) { navigationController.navigateTo(destinationFactory.getAlbumDetails(id)) }

    fun toArtistDetails(id: ArtistId) { navigationController.navigateTo(destinationFactory.getArtistDetails(id)) }

    enum class Destination { Profile, Playlists, Albums, Artists, Discover, Search, Settings }

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
                showPlaylistDetails = { navigationController.navigateTo(getPlaylistDetails(it)) },
                playPlaylist = { coroutineScope.launch { playerController.play(it.toUri()) } }
            )
        }

        fun getPlaylistDetails(id: PlaylistId): PlaylistDetails {
            return PlaylistDetails(
                id = id,
                repoStore = repoStore,
                showUserDetails = { navigationController.navigateTo(getUser(it)) },
                play = { coroutineScope.launch { playerController.play(id.toUri()) } },
                playTrack = { coroutineScope.launch { playerController.play(uris = listOf(it.toUri())) } }
            )
        }

        fun getUser(id: UserId): User {
            return User(
                id = id,
                repoStore = repoStore,
                showPlaylistDetails = { navigationController.navigateTo(getPlaylistDetails(it)) },
                playPlaylist = { coroutineScope.launch { playerController.play(it.toUri()) } }
            )
        }

        fun getAlbumList(): AlbumList {
            return AlbumList(
                repoStore = repoStore,
                showAlbumDetails = { navigationController.navigateTo(getAlbumDetails(it)) },
                playAlbum = { coroutineScope.launch { playerController.play(it.toUri()) } }
            )
        }

        fun getAlbumDetails(id: AlbumId): AlbumDetails {
            return AlbumDetails(
                id = id,
                repoStore = repoStore,
                showArtistDetails = { navigationController.navigateTo(getArtistDetails(it)) },
                play = { coroutineScope.launch { playerController.play(id.toUri()) } },
                playTrack = { coroutineScope.launch { playerController.play(uris = listOf(it.toUri())) } }
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
                showAlbumDetails = { navigationController.navigateTo(getAlbumDetails(it)) },
                play = { coroutineScope.launch { playerController.play(id.toUri()) } },
                playTrack = { coroutineScope.launch { playerController.play(uris = listOf(it.toUri())) } },
                playAlbum = { coroutineScope.launch { playerController.play(it.toUri()) } }
            )
        }

        fun getDiscover(): Discover {
            return Discover(
                repoStore = repoStore,
                showPlaylistDetails = { navigationController.navigateTo(getPlaylistDetails(it)) },
                playPlaylist = { coroutineScope.launch { playerController.play(it.toUri()) } }
            )
        }

        fun getSearch(): Search {
            return Search(
                repoStore = repoStore,
                showArtist = { navigationController.navigateTo(getArtistDetails(it)) },
                showAlbum = { navigationController.navigateTo(getAlbumDetails(it)) },
                showPlaylist = { navigationController.navigateTo(getPlaylistDetails(it)) },
                playTrack = { coroutineScope.launch { playerController.play(uris = listOf(it.toUri())) } },
                playArtist = { coroutineScope.launch { playerController.play(it.toUri()) } },
                playAlbum = { coroutineScope.launch { playerController.play(it.toUri()) } },
                playPlaylist = { coroutineScope.launch { playerController.play(it.toUri()) } }
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
