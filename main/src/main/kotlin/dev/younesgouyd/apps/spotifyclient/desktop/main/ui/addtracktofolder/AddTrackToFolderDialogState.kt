package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.addtracktofolder

import dev.younesgouyd.apps.spotifyclient.desktop.main.FolderId
import dev.younesgouyd.apps.spotifyclient.desktop.main.TrackId
import kotlinx.coroutines.flow.Flow

class AddTrackToFolderDialogState(
    val load: suspend (FolderId?, TrackId) -> Flow<List<AddTrackToFolderOption>>,
    val onAddToFolder: (TrackId, FolderId) -> Unit,
    val onRemoveFromFolder: (TrackId, FolderId) -> Unit
)