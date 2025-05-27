package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RodaTempsPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    val itemHeight = 48.dp
    val visibleItems = 3
    val middleItemIndex = visibleItems / 2
    val itemCount = range.count()


    val state = rememberLazyListState()

    // Scroll inicial quan es mostra el component
    val initialScrollDone = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!initialScrollDone.value) {
            val initialIndex = (value - range.first).coerceIn(0, itemCount - 1)
            state.scrollToItem(initialIndex)
            // 👇 NOTA: aquí **NO** cridem onValueChange directament
            initialScrollDone.value = true
        }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItems)
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = rememberSnapFlingBehavior(state)
        ) {
            // Afegim espais al principi per poder accedri al =00
            items(middleItemIndex) {
                Spacer(modifier = Modifier.height(itemHeight))
            }

            // Ítems reals
            items(itemCount) { index ->
                val itemValue = range.first + index
                val isSelected = itemValue == value

                Text(
                    text = itemValue.toString().padStart(2, '0'),
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = if (isSelected) {
                        MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        MaterialTheme.typography.titleLarge.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                )
            }

            // Espais al final per poder accedir al 24 o 60
            items(middleItemIndex) {
                Spacer(modifier = Modifier.height(itemHeight))
            }
        }
        // Gradient decoratiu (opcional)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.White.copy(alpha = 0.05f),
                        0.7f to Color.White.copy(alpha = 0.1f),
                        1f to Color.Transparent
                    )
                )
        )

        // Guarda l'últim valor notificat a onValueChange per evitar duplicats
        val lastNotifiedValue = remember { mutableStateOf<Int?>(null) }

        LaunchedEffect(state) {
            // Observem contínuament canvis en la posició visible de la LazyColumn
            snapshotFlow {
                val centerItem = state.firstVisibleItemIndex + middleItemIndex
                val offset = state.firstVisibleItemScrollOffset
                Pair(centerItem, offset)
            }.collect { (index) ->
                val realIndex = index - middleItemIndex // Calculem l'índex real dins de la llista de valors
                if (realIndex in 0 until itemCount) {
                    val newValue = range.first + realIndex

                    // Log per veure quins valors es detecten
                    Log.d("RodaTempsPicker", "Scroll detectat: index=$realIndex → valor=$newValue")

                    // Només notifiquem si realment ha canviat el valor mostrat
                    if (newValue != lastNotifiedValue.value) {
                        Log.d("RodaTempsPicker", "Nou valor notificat: $newValue")
                        lastNotifiedValue.value = newValue
                        onValueChange(newValue)
                    } else {
                        Log.d("RodaTempsPicker", "Valor ja notificat, no fem res: $newValue")
                    }
                } else {
                    Log.w("RodaTempsPicker", "Índex fora de rang: $realIndex")
                }
            }
        }
    }
}
