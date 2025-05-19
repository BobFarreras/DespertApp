package com.deixebledenkaito.despertapp



import android.os.Bundle


import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge


import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface

import androidx.compose.ui.Modifier

import androidx.hilt.navigation.compose.hiltViewModel
import com.deixebledenkaito.despertapp.navigation.NavGraph

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
                        )

                }
            }
        }
    }
}