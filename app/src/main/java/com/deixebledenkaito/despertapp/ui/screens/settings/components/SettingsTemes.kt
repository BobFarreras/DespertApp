package com.deixebledenkaito.despertapp.ui.screens.settings.components

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.deixebledenkaito.despertapp.preferences.TemesPreferences
import com.deixebledenkaito.despertapp.preferences.TemesPreferencesManager
import com.deixebledenkaito.despertapp.preferences.ThemeManager
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp


@Composable
fun SettingsTemes(context: Context = LocalContext.current) {
    var darkEnabled by remember { mutableStateOf(ThemeManager.currentThemeIsDark) }
    var lightEnabled by remember { mutableStateOf(!ThemeManager.currentThemeIsDark) }
    val textColor = if (ThemeManager.currentThemeIsDark) Color.White else Color.Black

    // Load preferences
    LaunchedEffect(Unit) {
        val prefs = TemesPreferencesManager.loadPreferences(context)
        darkEnabled = prefs.darkEnabled
        lightEnabled = prefs.lightEnabled
        ThemeManager.currentThemeIsDark = darkEnabled
        Log.d("ThemeDebug", "Inicialitzat dark: $darkEnabled, light: $lightEnabled")
    }


    // Guarda i aplica quan canvia un switch
    LaunchedEffect(darkEnabled, lightEnabled) {
        Log.d("ThemeDebug", "S'han canviat els switches: dark=$darkEnabled, light=$lightEnabled")
        ThemeManager.currentThemeIsDark = darkEnabled

        TemesPreferencesManager.savePreferences(
            context,
            TemesPreferences(darkEnabled = darkEnabled, lightEnabled = lightEnabled)
        )

        Log.d("ThemeDebug", "Preferències guardades")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = BackgroundApp(ThemeManager.currentThemeIsDark),
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
            Text(
                text = "Configuració dels Temes",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = textColor.copy(alpha = 0.1f),
                    contentColor = textColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 34.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Dark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Fosc", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = darkEnabled,
                            onCheckedChange = {
                                darkEnabled = it
                                lightEnabled = !it
                                Log.d("ThemeDebug", "Switch Dark: $it")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = textColor,
                                checkedTrackColor = textColor.copy(alpha = 0.3f),
                                uncheckedThumbColor = textColor.copy(alpha = 0.4f),
                                uncheckedTrackColor = textColor.copy(alpha = 0.1f)
                            )
                        )
                    }

                    // Light
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Clar", style = MaterialTheme.typography.bodyLarge,fontWeight = FontWeight.Bold)
                        Switch(
                            checked = lightEnabled,
                            onCheckedChange = {
                                lightEnabled = it
                                darkEnabled = !it
                                Log.d("ThemeDebug", "Switch Light: $it")
                            },
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
