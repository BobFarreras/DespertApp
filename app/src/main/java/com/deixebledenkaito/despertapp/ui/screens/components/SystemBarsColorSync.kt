package com.deixebledenkaito.despertapp.ui.screens.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp

@Composable
fun SystemBarsColorSync(darkTheme: Boolean = false) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val color = BackgroundApp().first().toArgb() // 👈 Primer color del gradient

            window.statusBarColor = color
            window.navigationBarColor = color

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = darkTheme
            insetsController.isAppearanceLightNavigationBars = darkTheme
        }
    }
}
