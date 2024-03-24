package dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.DarkThemeOptions

@Composable
fun Settings(state: SettingsState) {
    when (state) {
        is SettingsState.Loading -> Text("Loading...")
        is SettingsState.State -> Settings(state)
    }
}

@Composable
private fun Settings(state: SettingsState.State) {
    val scrollState = rememberScrollState()
    val darkTheme by state.darkTheme.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DarkTheme(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = darkTheme,
            onDarkThemeChange = state.onDarkThemeChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DarkTheme(
    modifier: Modifier = Modifier,
    selectedOption: DarkThemeOptions?,
    onDarkThemeChange: (DarkThemeOptions) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Dark Theme")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = selectedOption?.label ?: "",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                for (darkThemeOption in DarkThemeOptions.entries) {
                    DropdownMenuItem(
                        text =  { Text(darkThemeOption.label) },
                        onClick = {
                            onDarkThemeChange(darkThemeOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}