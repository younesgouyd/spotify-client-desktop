package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.DarkThemeOptions
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.Content.NavigationDrawerItems
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.Theme

@Composable
fun Content(
    darkTheme: DarkThemeOptions,
    currentMainComponent: Component,
    player: Component,
    selectedNavigationDrawerItem: NavigationDrawerItems,
    onNavigationDrawerItemClick: (NavigationDrawerItems) -> Unit,
    onRefreshPlayer: () -> Unit
) {
    Theme(
        darkTheme = darkTheme,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    PermanentNavigationDrawer(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        drawerContent = {
                            PermanentDrawerSheet {
                                Column(
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    NavigationDrawerItems.entries.forEach {
                                        NavigationDrawerItem(
                                            label = { Text(it.toString()) },
                                            selected = it == selectedNavigationDrawerItem,
                                            onClick = { onNavigationDrawerItemClick(it) }
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        content = { Icon(Icons.Default.Refresh, null) },
                                        onClick = onRefreshPlayer
                                    )
                                }
                            }
                        },
                        content = { currentMainComponent.show() }
                    )
                    player.show()
                }
            }
        }
    )
}