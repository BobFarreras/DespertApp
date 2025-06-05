package com.deixebledenkaito.despertapp.ui.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark



@Composable
fun ButtonsBottomBar(
    onSettingsClick: () -> Unit,
    onAlarmClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedButtom: String,
) {

    val textColor = if (currentThemeIsDark) Color.White else Color.Black

    Box(modifier = modifier){
        Row(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 30.dp).padding(bottom = 16.dp),

        ) {
            IconButton(onClick = onAlarmClick, modifier = Modifier.size(98.dp)) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Alarm",
                    modifier = Modifier.size(48.dp).padding(6.dp), // Mida lleugerament reduïda
                    tint = if(selectedButtom == "Alarmes") textColor else textColor.copy(alpha = 0.4f)

                )
            }

            IconButton(onClick = onSettingsClick, modifier = Modifier.size(98.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(48.dp).padding(6.dp), // Mida lleugerament reduïda
                    tint = if(selectedButtom == "Settings") textColor else textColor.copy(alpha = 0.4f)
                )
            }
        }
    }
}