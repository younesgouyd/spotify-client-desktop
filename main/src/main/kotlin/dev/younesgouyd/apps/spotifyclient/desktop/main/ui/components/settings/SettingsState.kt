package dev.younesgouyd.apps.spotifyclient.desktop.main.ui.components.settings

import dev.younesgouyd.apps.spotifyclient.desktop.main.DarkThemeOptions
import kotlinx.coroutines.flow.StateFlow

sealed class SettingsState {
    data object Loading : SettingsState()

    data class State(
        val darkTheme: StateFlow<DarkThemeOptions?>,
        val onDarkThemeChange: (DarkThemeOptions) -> Unit
    ) : SettingsState()
}