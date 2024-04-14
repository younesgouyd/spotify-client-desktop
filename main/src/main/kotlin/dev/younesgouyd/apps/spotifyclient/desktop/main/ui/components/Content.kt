package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.younesgouyd.apps.spotifyclient.desktop.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.main.components.Content.NavigationDrawerItems

@Composable
fun Content(
    currentMainComponent: Component,
    player: Component,
    selectedNavigationDrawerItem: NavigationDrawerItems,
    onNavigationDrawerItemClick: (NavigationDrawerItems) -> Unit
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
                    NavigationDrawerItems.entries.forEach {
                        NavigationDrawerItem(
                            label = { Text(it.toString()) },
                            selected = it == selectedNavigationDrawerItem,
                            onClick = { onNavigationDrawerItemClick(it) }
                        )
                    }
                }
            },
            content = { currentMainComponent.show() }
        )
        player.show()
    }
}