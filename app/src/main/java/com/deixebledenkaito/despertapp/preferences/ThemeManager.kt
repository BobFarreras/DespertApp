package com.deixebledenkaito.despertapp.preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// En un fitxer central com ThemeManager.kt
object ThemeManager {
    var currentThemeIsDark by mutableStateOf(true)
}
