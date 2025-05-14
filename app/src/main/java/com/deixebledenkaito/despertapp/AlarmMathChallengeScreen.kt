package com.deixebledenkaito.despertapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AlarmMathChallengeScreen(onCorrect: () -> Unit) {
    var num1 by remember { mutableStateOf(Random.nextInt(5, 15)) }
    var num2 by remember { mutableStateOf(Random.nextInt(5, 15)) }
    val correctAnswer = num1 + num2

    var answer by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFCDD2))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Resol aquest problema per apagar l’alarma:", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        Text("$num1 + $num2 = ?", style = MaterialTheme.typography.displayMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Resposta") },
            isError = error,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (answer.toIntOrNull() == correctAnswer) {
                onCorrect()
            } else {
                error = true
            }
        }) {
            Text("Comprovar")
        }

        if (error) {
            Text("Incorrecte! Torna-ho a provar.", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
