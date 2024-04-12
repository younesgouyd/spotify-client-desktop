package dev.younesgouyd.apps.spotifyclient.desktop.main.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// todo - currently we can't add margin because the modifier is passed to the inner Box.
//        this decision was made to solve 2 issues:
//        1. to fix the incorrect clipping of .combinedClickable modifier
//        2. to control the content Alignment when Card's size is greater than its content.
//        there has to be a way to stretch size of the inner box to match size of the containing card; similar to
//        .matchParentSize
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Item(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    val finalModifier = if (onClick != null) {
        Modifier
            .combinedClickable(
                onClick = onClick,
            ).then(modifier)
    } else {
        modifier
    }
    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors(),
        content = {
            Box(
                modifier = finalModifier.fillMaxSize(),
                contentAlignment = contentAlignment,
                content = { content() }
            )
        }
    )
}
