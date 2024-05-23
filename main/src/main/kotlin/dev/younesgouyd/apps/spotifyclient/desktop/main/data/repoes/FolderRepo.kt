package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import dev.younesgouyd.apps.spotifyclient.desktop.main.FolderId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.sqldelight.FolderQueries
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.sqldelight.FolderTrackCrossRefQueries
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder.AddTrackToFolderOption
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class FolderRepo(
    private val folderQueries: FolderQueries,
    private val folderTrackQueries: FolderTrackCrossRefQueries
) {
    fun getFolder(id: FolderId): Flow<Folder> {
        return folderQueries.select(id.value) { id, name, _, _, _ ->
            Folder(FolderId(id), name)
        }.asFlow().mapToOne(Dispatchers.IO)
    }

    fun getSubfolders(id: FolderId?): Flow<List<Folder>> {
        return folderQueries.selectSubfolders(id?.value) { id, name, _, _, _ ->
            Folder(FolderId(id), name)
        }.asFlow().mapToList(Dispatchers.IO)
    }

    fun getAddTrackToFolderOptions(folderId: FolderId?, trackId: TrackId): Flow<List<AddTrackToFolderOption>> {
        return folderQueries.selectAddTrackToFolderOptions(trackId.value, folderId?.value) { id, name, trackId ->
            AddTrackToFolderOption(
                folder = Folder(FolderId(id), name),
                added = trackId != null
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun add(name: String, parentFolderId: FolderId?) {
        withContext(Dispatchers.IO) {
            val currentDatetime = Instant.now().toEpochMilli()
            folderQueries.insert(name, parentFolderId?.value, currentDatetime, currentDatetime)
        }
    }

    suspend fun updateName(id: FolderId, name: String) {
        withContext(Dispatchers.IO) {
            folderQueries.updateName(name, Instant.now().toEpochMilli(), id.value)
        }
    }

    suspend fun delete(id: FolderId) {
        withContext(Dispatchers.IO) {
            folderQueries.delete(id.value)
        }
    }

    suspend fun addTrackToFolder(trackId: TrackId, folderId: FolderId) {
        withContext(Dispatchers.IO) {
            val currentDatetime = Instant.now().toEpochMilli()
            folderTrackQueries.insert(folderId.value, trackId.value, currentDatetime, currentDatetime)
        }
    }

    suspend fun removeTrackFromFolder(trackId: TrackId, folderId: FolderId) {
        withContext(Dispatchers.IO) {
            folderTrackQueries.delete(folderId.value, trackId.value)
        }
    }
}