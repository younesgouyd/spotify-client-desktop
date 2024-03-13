package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.Image


@Composable
fun Profile(
    state: ProfileState
) {
    when (state) {
        is ProfileState.Loading -> Text("Loading...")
        is ProfileState.State -> Profile(state)
    }
}

@Composable
private fun Profile(
    state: ProfileState.State
) {
    val user = state.user

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(url = user.profilePictureUrl)
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                "${user.followerCount} followers",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}