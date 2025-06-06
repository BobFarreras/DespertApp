package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark


@Composable
fun AnimacioDiaChip(
    day: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    val textColor = if (currentThemeIsDark) Color.White else Color.Black
    val textColorContrri = if (!currentThemeIsDark) Color.White else Color.Black

    val backgroundColor by animateColorAsState(
        if (selected) textColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200)
    )
    val contentColor by animateColorAsState(
        if (selected)  textColorContrri else textColor,
        animationSpec = tween(durationMillis = 200)
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (enabled) textColor else textColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = day,
                color = if (enabled) contentColor else contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}