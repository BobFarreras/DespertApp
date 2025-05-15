package com.deixebledenkaito.despertapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.deixebledenkaito.despertapp.presentation.navigation.NavGraph
import com.deixebledenkaito.despertapp.ui.theme.DespertAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // Necessari per a Hilt
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DespertAppTheme {

                // Aplicar el tema i afegir el NavGraph
                Surface(modifier = Modifier.fillMaxSize()) {

                    NavGraph()

                    //:)
                }

            }
        }
    }
}

