package com.deixebledenkaito.despertapp.ui.screens.settings.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deixebledenkaito.despertapp.preferences.AlarmPreferences
import com.deixebledenkaito.despertapp.preferences.AlarmPreferencesManager
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
@Composable
fun AlarmSettingsScreen() {
    val context = LocalContext.current
    var volume by remember { mutableFloatStateOf(80f) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var increasingVolume by remember { mutableStateOf(false) }
    val textColor = if (currentThemeIsDark) Color.White else Color.Black

    // Carrega preferències quan s'obre la pantalla
    LaunchedEffect(true) {
        val prefs = AlarmPreferencesManager.loadPreferences(context)
        volume = prefs.volume.toFloat()
        vibrationEnabled = prefs.vibrationEnabled
        increasingVolume = prefs.increasingVolume
    }

    // Guarda automàticament quan hi ha canvis
    LaunchedEffect(volume, vibrationEnabled, increasingVolume) {
        AlarmPreferencesManager.savePreferences(
            context,
            AlarmPreferences(
                volume = volume.toInt(),
                vibrationEnabled = vibrationEnabled,
                increasingVolume = increasingVolume
            )
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Fons de pantalla (gradient)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = BackgroundApp(currentThemeIsDark),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 44.dp)
        ) {
            // Títol
            Text(
                text = "Configuració d'Alarmes",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Configuracions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = textColor.copy(alpha = 0.1f),
                    contentColor = textColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Volum
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Volum de l'alarma",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${volume.toInt()}%",
                            style = MaterialTheme.typography.bodyLarge, fontSize = 20.sp,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = textColor,
                            activeTrackColor = textColor.copy(alpha = 0.8f),
                            inactiveTrackColor = textColor.copy(alpha = 0.2f)
                        )
                    )

                    // Vibrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Vibrar",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = textColor,
                                checkedTrackColor = textColor.copy(alpha = 0.3f),
                                uncheckedThumbColor = textColor.copy(alpha = 0.4f),
                                uncheckedTrackColor = textColor.copy(alpha = 0.1f)
                            )
                        )
                    }

                    // Volum incremental
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Volum incremental",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Switch(
                            checked = increasingVolume,
                            onCheckedChange = { increasingVolume = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = textColor,
                                checkedTrackColor = textColor.copy(alpha = 0.3f),
                                uncheckedThumbColor = textColor.copy(alpha = 0.4f),
                                uncheckedTrackColor = textColor.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }


    }
}