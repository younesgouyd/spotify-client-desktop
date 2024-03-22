package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Login(
    errorMessage: String,
    enabled: Boolean,
    onLoginClick: (clientId: String) -> Unit
) {
    var clientId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                TextField(
                    isError = errorMessage.isNotEmpty(),
                    label = { Text("Your Spotify Client ID") },
                    value = clientId,
                    onValueChange = { clientId = it },
                    enabled = enabled,
                    supportingText = { if (errorMessage.isNotEmpty()) Text(errorMessage) }
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    modifier = Modifier.align(Alignment.End),
                    content = { Text("Log in") },
                    onClick = { onLoginClick(clientId) },
                    enabled = enabled
                )
            }
        }
    }
}
