package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark


@Composable
fun SegmentedControl(
    items: List<String>,
    selectedItem: String,
    onItemSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = items.indexOf(selectedItem)
    val rowWidth = remember { mutableIntStateOf(0) }
    val textColor = if (currentThemeIsDark) Color.White else Color.Black
    val indicatorColor = textColor.copy(alpha = 0.2f)

    val indicatorOffset by animateDpAsState(
        targetValue = with(LocalDensity.current) {
            (rowWidth.intValue / items.size * selectedIndex).toDp()
        },
        label = "IndicatorOffset"
    )

    Box(
        modifier = modifier
            .background(textColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .onSizeChanged { rowWidth.intValue = it.width }
    ) {
        // Fons animat de l'indicador
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .fillMaxWidth(1f / items.size)
                .fillMaxHeight()
                .background(indicatorColor, RoundedCornerShape(12.dp))
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                val isSelected = item == selectedItem

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemSelect(item) }
                        .background(
                            color = if (isSelected) indicatorColor else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (isSelected) textColor else textColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}
