package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark

import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RodetaSeleccioHorizontal(
    items: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleItems = 3
    val middleItemIndex = visibleItems / 2
    val itemCount = items.size

    val state = rememberLazyListState()
    val density = LocalDensity.current
    val textColor = if (currentThemeIsDark) Color.White else Color.Black
    val initialScrollDone = remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(textColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        val totalWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        val itemWidth = totalWidth / visibleItems

        // Scroll inicial
        LaunchedEffect(value) {
            val index = items.indexOf(value)
            if (index != -1) {
                state.scrollToItem(index)
                initialScrollDone.value = true
            }
        }

        LazyRow(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            flingBehavior = rememberSnapFlingBehavior(state)
        ) {
            items(middleItemIndex) {
                Spacer(modifier = Modifier.width(itemWidth))
            }

            items(itemCount) { index ->
                val item = items[index]
                Text(
                    text = item,
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (item == value) textColor else textColor.copy(alpha = 0.5f),
                        fontWeight = if (item == value) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (item == value) 18.sp else 15.sp,
                    )
                )
            }

            items(middleItemIndex) {
                Spacer(modifier = Modifier.width(itemWidth))
            }
        }

        // Detecció de l’ítem centrat
        val lastNotifiedValue = remember { mutableStateOf<String?>(null) }

        LaunchedEffect(state) {
            snapshotFlow {
                val offsetPx = state.firstVisibleItemScrollOffset
                val itemWidthPx = with(density) { (itemWidth + 8.dp).toPx() }
                val indexOffset = ((offsetPx + itemWidthPx / 2) / itemWidthPx).toInt()
                val centerIndex = state.firstVisibleItemIndex + indexOffset
                centerIndex
            }.distinctUntilChanged().collect { centerIndex ->
                if (centerIndex in items.indices) {
                    val newValue = items[centerIndex]
                    if (newValue != lastNotifiedValue.value) {
                        lastNotifiedValue.value = newValue
                        onValueChange(newValue)
                    }
                }
            }
        }
    }
}
