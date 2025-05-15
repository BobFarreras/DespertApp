package com.deixebledenkaito.despertapp.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.deixebledenkaito.despertapp.utils.MathChallengeGenerator

// AlarmChallengeActivity.kt
class AlarmChallengeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val question = MathChallengeGenerator.generate() // Genera una pregunta nova
            AlarmChallengeScreen(question = question) {
                Log.d("AlarmChallenge", "Resposta correcta! Tancant activitat.")
                finish() // Tanca la pantalla si l'usuari encerta
            }
        }
    }
}
