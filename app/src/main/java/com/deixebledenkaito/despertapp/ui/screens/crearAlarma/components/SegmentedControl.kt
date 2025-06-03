package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedItem: String,
    onItemSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = items.indexOf(selectedItem)
    val rowWidth = remember { mutableIntStateOf(0) }
    val rowHeight = remember { mutableIntStateOf(0) }
    val colorTextModels = Color(0xF7676161)

    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))

    ) {
        // Indicador de selecció
        Box(
            modifier = Modifier
                .offset(x = with(LocalDensity.current) { (rowWidth.intValue / items.size * selectedIndex).toDp() })
                .fillMaxWidth(1f / items.size)
                .background(Color.White, RoundedCornerShape(8.dp))
                .onSizeChanged {
                    rowWidth.intValue = it.width * items.size
                    rowHeight.intValue = it.height
                }
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (item == selectedItem) Color.White else colorTextModels,

                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemSelect(item) }
                        .padding(vertical = 8.dp)
                        .wrapContentWidth()
                        .background(color = if (item == selectedItem) Color.White.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 18.dp)

                )
            }
        }
    }
}