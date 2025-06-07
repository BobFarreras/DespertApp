package com.deixebledenkaito.despertapp.ui.screens.colors


import androidx.compose.ui.graphics.Color



fun BackgroundApp(isDark: Boolean): List<Color> {
    return if (isDark) {
        listOf(Color(0xFF1C0C05), Color(0xF70A0909))
    } else {
        listOf(Color(0xFFE3F2FD), Color(0xFF6CABD3))
    }
}