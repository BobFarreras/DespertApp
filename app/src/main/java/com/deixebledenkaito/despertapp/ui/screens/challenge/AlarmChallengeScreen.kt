package com.deixebledenkaito.despertapp.ui.screens.challenge


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.ChallengeQuestion
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp

import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.MathQuestion


@Composable
fun AlarmChallengeScreen(
    question: ChallengeQuestion,
    onCorrect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = BackgroundApp(),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question.question,
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 36.dp , start = 10.dp , end = 10.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        question.options.forEach { option ->
            Button(
                onClick = {
                    if (option == question.correctAnswer) {
                        onCorrect()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(Color.White.copy(alpha = 0.1f))
            ) {
                Text(
                    text = option,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}
