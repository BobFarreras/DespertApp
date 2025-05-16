package com.deixebledenkaito.despertapp


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.deixebledenkaito.despertapp.navigation.NavGraph
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import com.deixebledenkaito.despertapp.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DespertAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: AlarmViewModel = hiltViewModel()


                    NavGraph(
                        viewModel = viewModel,
                        onLogout = { /* Lógica para cerrar sesión */ },

                        )
                }
            }
        }
    }
}