package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.Component
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.RepoStore
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.settings.Settings
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.settings.SettingsState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Settings(
    private val repoStore: RepoStore
) : Component() {
    override val title: String = "Settings"
    private val state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState.Loading)

    init {
        coroutineScope.launch {
            state.update {
                SettingsState.State(
                    darkTheme = repoStore.settingsRepo.getDarkThemeFlow().stateIn(
                        scope = coroutineScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = null
                    ),
                    onDarkThemeChange = {
                        coroutineScope.launch { repoStore.settingsRepo.updateDarkTheme(it) }
                    }
                )
            }
        }
    }

    @Composable
    override fun show() {
        val state by state.collectAsState()

        Settings(state)
    }

    override fun clear() {
        coroutineScope.cancel()
    }
}