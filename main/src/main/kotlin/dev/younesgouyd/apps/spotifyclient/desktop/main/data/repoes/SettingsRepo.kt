package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes

import dev.younesgouyd.apps.spotifyclient.desktop.main.DarkThemeOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

class SettingsRepo(
    private val appDataRepo: AppDataRepo
) {
    companion object {
        private const val DARK_THEME_TAG = "dark_theme"
    }

    private val darkThemeCallbacks: MutableList<Callback> = mutableListOf()

    suspend fun init() {
        val value = getDarkTheme()
        if (value == null) {
            updateDarkTheme(DarkThemeOptions.SystemDefault)
        }
    }

    fun getDarkThemeFlow(): Flow<DarkThemeOptions?> {
        return callbackFlow {
            val callback = object : Callback() {
                override suspend fun onNewValue(value: DarkThemeOptions?) {
                    send(value)
                }
            }
            darkThemeCallbacks += callback
            callback.onNewValue(getDarkTheme())
            awaitClose { darkThemeCallbacks.removeIf { it.id == callback.id } }
        }
    }

    suspend fun updateDarkTheme(theme: DarkThemeOptions) {
        val data = appDataRepo.getData()
        data.put(DARK_THEME_TAG, theme.name)
        appDataRepo.save(data)
        for (callback in darkThemeCallbacks) {
            callback.onNewValue(getDarkTheme())
        }
    }

    private suspend fun getDarkTheme(): DarkThemeOptions? {
        val data = appDataRepo.getData()
        val value = if (data.has(DARK_THEME_TAG)) data.getString(DARK_THEME_TAG) else return null
        return when (value) {
            DarkThemeOptions.SystemDefault.name -> DarkThemeOptions.SystemDefault
            DarkThemeOptions.Enabled.name -> DarkThemeOptions.Enabled
            DarkThemeOptions.Disabled.name -> DarkThemeOptions.Disabled
            else -> throw Exception()
        }
    }

    private abstract class Callback {
        val id: UUID = UUID.randomUUID()

        abstract suspend fun onNewValue(value: DarkThemeOptions?)
    }
}