package com.deixebledenkaito.despertapp.ui.screens.challenge

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.deixebledenkaito.despertapp.ui.screens.challenge.tipusChallenge.ChallengeQuestion
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun AlarmChallengeScreen(
    question: ChallengeQuestion,
    onCorrect: () -> Unit,
    onSnooze: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val answerStates = remember { mutableStateMapOf<String, AnswerState>() }
    Log.d("AlarmChallengeScreen", "Shuffled options: ${question.options}")
    // Es barreja només un cop
    val shuffledOptions = remember(question) {
        question.options.shuffled()
    }
    Log.d("AlarmChallengeScreen", "Shuffled options shuffled: $shuffledOptions")


    // Inicialitza estats
    shuffledOptions.forEach{ option ->
        answerStates.putIfAbsent(option, AnswerState())
    }

    val width = 96.dp
    val widthInPx = with(LocalDensity.current) { width.toPx() }
    val anchors = mapOf(0f to false, widthInPx to true)
    val swipeableState = rememberSwipeableState(initialValue = false)
    // Efecte per detectar quan s'ha deslliscat completament
    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue) {
            onSnooze()
            swipeableState.animateTo(false) // Retorna a la posició inicial
        }
    }
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
        // Contenidor per al switch desllisable
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Fons del switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Posposar 10 minuts",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 96.dp)
                )
            }

            // Element desllisable
            Box(
                modifier = Modifier
                    .offset(x = swipeableState.offset.value.dp)
                    .size(width, 48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = androidx.compose.foundation.gestures.Orientation.Horizontal

                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Posposar",
                    tint = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.3f))
        Text(
            text = question.question,
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 36.dp , start = 10.dp , end = 10.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        shuffledOptions.forEach { option ->
            val state = answerStates[option] ?: AnswerState()

            val backgroundColor by animateColorAsState(
                targetValue = when {
                    state.isCorrect -> Color(0xFF4CAF50)
                    state.isWrong -> Color.Red
                    else -> Color.White.copy(alpha = 0.1f)
                },
                animationSpec = tween(durationMillis = 300),
                label = "buttonColor"
            )

            Button(
                onClick = {
                    if (state.isCorrect || answerStates.any { it.value.isCorrect }) return@Button

                    if (option == question.correctAnswer) {
                        answerStates[option]?.isCorrect = true
                        coroutineScope.launch {
                            delay(1000)
                            onCorrect()
                        }
                    } else {
                        answerStates[option]?.isWrong = true
                        coroutineScope.launch {
                            delay(700)
                            answerStates[option]?.isWrong = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor)
            ) {
                val buttonText = if (state.isCorrect) "Correcte!" else option
                Text(buttonText, fontSize = 24.sp, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.weight(0.3f))

    }
}


class AnswerState {
    var isCorrect by mutableStateOf(false)
    var isWrong by mutableStateOf(false)
}