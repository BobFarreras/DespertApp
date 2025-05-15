package com.deixebledenkaito.despertapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deixebledenkaito.despertapp.utils.MathQuestion

// AlarmChallengeScreen.kt
@Composable
fun AlarmChallengeScreen(question: MathQuestion, onCorrect: () -> Unit) {
    var selected by remember { mutableStateOf<Int?>(null) } // Estat de l'opció seleccionada

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(question.question, fontSize = 24.sp, fontWeight = FontWeight.Bold) // Mostra la pregunta

        Spacer(modifier = Modifier.height(24.dp))

        // Dibuixa cada opció com a botó
        question.options.forEach { option ->
            Button(
                onClick = {
                    selected = option // Guarda l'opció seleccionada
                    if (option == question.correctAnswer) onCorrect() // Si és correcta, crida onCorrect()
                    else Log.d("AlarmChallenge", "Resposta incorrecta: $option")
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(option.toString()) // Mostra el text del botó
            }
        }
    }
}