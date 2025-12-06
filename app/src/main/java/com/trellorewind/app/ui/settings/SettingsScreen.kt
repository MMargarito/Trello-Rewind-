package com.trellorewind.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trellorewind.app.interaction.HapticsManager
import com.trellorewind.app.interaction.SoundManager
import com.trellorewind.app.interaction.ConfettiManager
import com.trellorewind.app.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    hapticsManager: HapticsManager,
    soundManager: SoundManager,
    confettiManager: ConfettiManager,
    themeManager: ThemeManager,
    onBackClick: () -> Unit
) {
    var hapticsEnabled by remember { mutableStateOf(hapticsManager.isEnabled) }
    var soundsEnabled by remember { mutableStateOf(soundManager.isEnabled) }
    var animationsEnabled by remember { mutableStateOf(confettiManager.isEnabled) }
    var darkModeEnabled by remember { mutableStateOf(themeManager.isDarkMode) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Appearance Section
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Dark Mode Toggle
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Switch to dark theme",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = {
                            darkModeEnabled = it
                            themeManager.isDarkMode = it
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Interaction Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Haptics Toggle
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptics",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Vibration feedback for interactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = {
                            hapticsEnabled = it
                            hapticsManager.isEnabled = it
                        }
                    )
                }
            }
            
            // Sounds Toggle
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sounds",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Sound effects for actions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = soundsEnabled,
                        onCheckedChange = {
                            soundsEnabled = it
                            soundManager.isEnabled = it
                        }
                    )
                }
            }
            
            // Animations Toggle
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Animations",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Confetti and visual effects",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = animationsEnabled,
                        onCheckedChange = {
                            animationsEnabled = it
                            confettiManager.isEnabled = it
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // About Section
            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Kanhero",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A delightful Kanban board app with smooth animations and tactile feedback. Be the hero of your tasks!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

