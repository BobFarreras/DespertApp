package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun RodetaSeleccioHorizontal(
    items: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemWidth = 120.dp
    val visibleItems = 3
    val middleItemIndex = visibleItems / 2
    val itemCount = items.size

    val state = rememberLazyListState()
    val density = LocalDensity.current
    val initialScrollDone = remember { mutableStateOf(false) }

    // Scroll inicial per posar el valor seleccionat al mig
    LaunchedEffect(Unit) {
        if (!initialScrollDone.value) {
            val initialIndex = items.indexOf(value).coerceIn(0, itemCount - 1)
            val centeredIndex = (initialIndex - middleItemIndex).coerceAtLeast(0)
            state.scrollToItem(centeredIndex)
            initialScrollDone.value = true
        }
    }

    // Dibuixa la interfície
    Box(
        modifier = modifier
            .width(itemWidth * visibleItems)
            .fillMaxHeight()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
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
                        color =  if (item == value) Color.White else Color.White.copy(alpha = 0.5f) ,
                        fontWeight = if (item == value) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (item == value) 19.sp else 16.sp,
                    )
                )
            }

            items(middleItemIndex) {
                Spacer(modifier = Modifier.width(itemWidth))
            }
        }

        // Detecció de l’ítem centrat real
        val lastNotifiedValue = remember { mutableStateOf<String?>(null) }

        LaunchedEffect(state) {
            snapshotFlow {
                // Càlcul amb offset real en píxels
                val offsetPx = state.firstVisibleItemScrollOffset
                val itemWidthPx = with(density) { (itemWidth + 8.dp).toPx() } // 8.dp = spacing

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

//@Composable
//fun RodetaSeleccioHorizontal(
//    items: List<String>,
//    value: String,
//    onValueChange: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val itemWidth = 120.dp
//    val visibleItems = 3
//    val middleItemIndex = visibleItems / 2
//    val itemCount = items.size
//
//    val state = rememberLazyListState()
//    val initialScrollDone = remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        if (!initialScrollDone.value) {
//            val initialIndex = (items.indexOf(value)).coerceIn(0, itemCount - 1)
//            state.scrollToItem((initialIndex - middleItemIndex).coerceAtLeast(0))
//            initialScrollDone.value = true
//        }
//    }
//
//    Box(
//        modifier = modifier
//            .width(itemWidth * visibleItems)
//            .fillMaxHeight()
//            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
//        contentAlignment = Alignment.Center
//    ) {
//        LazyRow(
//            state = state,
//            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            flingBehavior = rememberSnapFlingBehavior(state)
//        ) {
//            // Espai al principi
//            items(middleItemIndex) {
//                Spacer(modifier = Modifier.width(itemWidth))
//            }
//
//            // Ítems reals
//            items(itemCount) { index ->
//                val item = items[index]
//                val isSelected = state.firstVisibleItemIndex + middleItemIndex == index
//
//                Text(
//                    text = item,
//                    modifier = Modifier
//                        .width(itemWidth)
//                        .fillMaxHeight(),
//                    textAlign = TextAlign.Center,
//                    style = if (isSelected) {
//                        MaterialTheme.typography.titleLarge.copy(
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold
//                        )
//                    } else {
//                        MaterialTheme.typography.bodyMedium.copy(
//                            color = Color.White.copy(alpha = 0.6f)
//                        )
//                    }
//                )
//            }
//
//            // Espai al final
//            items(middleItemIndex) {
//                Spacer(modifier = Modifier.width(itemWidth))
//            }
//        }
//
//
//
//        val lastNotifiedValue = remember { mutableStateOf<String?>(null) }
//
//        LaunchedEffect(state) {
//            snapshotFlow {
//                val centerItem = state.firstVisibleItemIndex + middleItemIndex
//                centerItem
//            }.distinctUntilChanged().collect { centerIndex ->
//                if (centerIndex in items.indices) {
//                    val newValue = items[centerIndex]
//                    if (newValue != lastNotifiedValue.value) {
//                        lastNotifiedValue.value = newValue
//                        onValueChange(newValue)
//                    }
//                }
//            }
//        }
//    }
//}