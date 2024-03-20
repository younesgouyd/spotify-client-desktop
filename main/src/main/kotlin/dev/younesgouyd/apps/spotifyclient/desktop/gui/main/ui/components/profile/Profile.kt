package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(url = user.profilePictureUrl)
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                "${user.followerCount} followers",
                style = MaterialTheme.typography.labelLarge
            )
            Button(
                content = {
                    Row {
                        Icon(Icons.AutoMirrored.Default.Logout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Logout")
                    }
                },
                onClick = state.onLogoutClick
            )
        }
    }
}