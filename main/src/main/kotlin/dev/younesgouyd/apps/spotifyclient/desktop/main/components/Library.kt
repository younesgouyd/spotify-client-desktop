package dev.younesgouyd.apps.spotifyclient.desktop.main.components

import androidx.compose.runtime.Composable
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.FolderId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.FolderRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.TrackRepo
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktoplaylist.AddTrackToPlaylistDialogState
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.Library
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Folder
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Library(
    private val folderRepo: FolderRepo,
    private val trackRepo: TrackRepo,
    private val playTrack: (TrackId) -> Unit,
    private val addTrackToPlaylistDialogState: AddTrackToPlaylistDialogState
) : Component() {
    override val title: String = "Library"
    private val currentFolder: MutableStateFlow<Folder?> = MutableStateFlow(null)
    private val path: StateFlow<Set<Folder>>
    private val folders: StateFlow<List<Folder>>
    private val tracks: StateFlow<List<Folder.Track>>
    private val loadingItems: StateFlow<Boolean>
    private val loadingFolders: MutableStateFlow<Boolean>
    private val loadingTracks: MutableStateFlow<Boolean>

    init {
        loadingFolders = MutableStateFlow(true)
        loadingTracks = MutableStateFlow(true)
        loadingItems = loadingFolders.combine(loadingTracks) { loadingFolders, loadingTracks ->
            loadingFolders || loadingTracks
        }.stateIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), initialValue = true)

        path = flow<Set<Folder>> {
            var value: Set<Folder> = emptySet()
            currentFolder.collect { folder ->
                if (folder == null) {
                    value = emptySet()
                    emit(value)
                } else {
                    val list = value.takeWhile { it.id != folder.id }.toMutableList()
                    list.add(folder)
                    value = list.toSet()
                    emit(value)
                }
            }
        }.stateIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), initialValue = emptySet())

        folders = currentFolder.flatMapLatest {
            flow<List<Folder>> {
                emit(emptyList())
                loadingFolders.value = true
                folderRepo.getSubfolders(it?.id).collect {
                    emit(it)
                    loadingFolders.value = false
                }
            }
        }.stateIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())

        tracks = currentFolder.flatMapLatest {
            flow<List<Folder.Track>> {
                emit(emptyList())
                loadingTracks.value = true
                if (it != null) {
                    trackRepo.getFolderTracks(it.id).collect {
                        emit(it)
                        loadingTracks.value = false
                    }
                }
                loadingTracks.value = false
            }
        }.stateIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
    }

    @Composable
    override fun show() {
        Library(
            path = path,
            loadingItems = loadingItems,
            folders = folders,
            tracks = tracks,
            addTrackToPlaylistDialogState = addTrackToPlaylistDialogState,
            onNewFolder = ::onNewFolder,
            onFolderClick = ::onFolderClick,
            onRenameFolder = ::onRenameFolder,
            onDeleteFolder = ::onDeleteFolder,
            onTrackClick = ::onTrackClick,
            onRemoveTrack = ::onRemoveTrack
        )
    }

    override fun clear() {
        coroutineScope.cancel()
    }

    private fun onNewFolder(name: String) {
        coroutineScope.launch {
            folderRepo.add(name, currentFolder.value?.id)
        }
    }

    private fun onFolderClick(folder: Folder?) {
        currentFolder.value = folder
    }

    private fun onRenameFolder(id: FolderId, name: String) {
        coroutineScope.launch {
            folderRepo.updateName(id, name)
        }
    }

    private fun onDeleteFolder(id: FolderId) {
        coroutineScope.launch {
            folderRepo.delete(id)
        }
    }

    private fun onTrackClick(id: TrackId) {
        playTrack(id)
    }

    private fun onRemoveTrack(id: TrackId) {
        coroutineScope.launch {
            folderRepo.removeTrackFromFolder(id, currentFolder.value!!.id)
        }
    }
}