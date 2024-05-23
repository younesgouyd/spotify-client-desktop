package dev.younesgouyd.apps.spotifyclient.desktop.main.components.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.main.*
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details.Album
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details.AlbumDetails
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.album.details.AlbumDetailsState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Images
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlbumDetails(
    private val id: AlbumId,
    private val repoStore: RepoStore,
    addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState,
    addTrackToFolderDialogState: AddTrackToFolderDialogState,
    private val showArtistDetails: (ArtistId) -> Unit,
    play: () -> Unit,
    playTrack: (TrackId) -> Unit
) : Component() {
    override val title: String = "Album"
    private val state: MutableStateFlow<AlbumDetailsState> = MutableStateFlow(AlbumDetailsState.Loading)
    private val saved: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val saveRemoveButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true)

    init {
        coroutineScope.launch {
            reloadSaved()
            state.update {
                AlbumDetailsState.State(
                    album = run {
                        val data = repoStore.albumRepo.getAlbum(id)
                        Album(
                            id = data.id,
                            name = data.name,
                            artists = data.artists?.map {
                                Album.Artist(
                                    id = it.id,
                                    name = it.name
                                )
                            } ?: emptyList(),
                            images = data.images?.toImages() ?: Images.empty()
                        )
                    },
                    saved = saved.asStateFlow().filterNotNull().stateIn(coroutineScope),
                    saveRemoveButtonEnabled = saveRemoveButtonEnabled.asStateFlow(),
                    tracks = LazilyLoadedItems<Album.Track, Offset.Index>(
                        coroutineScope = coroutineScope,
                        load = { offset ->
                            val data = repoStore.trackRepo.getAlbumTracks(id, offset)
                            LazilyLoadedItems.Page(
                                nextOffset = Offset.Index.fromUrl(data.next),
                                items = data.items?.filterNotNull()?.map {
                                    Album.Track(
                                        id = it.id,
                                        name = it.name
                                    )
                                } ?: emptyList()
                            )
                        },
                        initialOffset = Offset.Index.initial()
                    ),
                    addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
                    addTrackToFolderDialogState = addTrackToFolderDialogState,
                    onSaveClick = {
                        coroutineScope.launch {
                            saveRemoveButtonEnabled.update { false }
                            repoStore.albumRepo.saveAlbum(id)
                            reloadSaved()
                            saveRemoveButtonEnabled.update { true }
                        }
                    },
                    onRemoveClick = {
                        coroutineScope.launch {
                            saveRemoveButtonEnabled.update { false }
                            repoStore.albumRepo.removeAlbum(id)
                            reloadSaved()
                            saveRemoveButtonEnabled.update { true }
                        }
                    },
                    onArtistClick = showArtistDetails,
                    onPlayClick = play,
                    onTrackClick = playTrack
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        AlbumDetails(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private suspend fun reloadSaved() {
        saved.update { repoStore.albumRepo.isAlbumSaved(id) }
    }
}