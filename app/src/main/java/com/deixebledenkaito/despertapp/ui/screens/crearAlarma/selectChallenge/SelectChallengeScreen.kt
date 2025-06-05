package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectChallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp


// Nova pantalla per seleccionar el tipus de repte
@Composable
fun SelectChallengeScreen(
    onChallengeSelected: (challengeId: String, challengeName: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (currentThemeIsDark) Color.White else Color.Black
    // Llista de reptes disponibles
    val challenges = listOf(
        Challenge("Matemàtiques", "Matemàtiques", R.drawable.matematiques),
        Challenge("Cultura Catalana", "Cultura Catalana", R.drawable.catalunya),
        Challenge("Anime", "Anime", R.drawable.anime),
        Challenge("Angles", "Angles", R.drawable.angles),
        Challenge("Aleatori", "Aleatori", R.drawable.icon_aleatori)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = BackgroundApp(currentThemeIsDark),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(top = 26.dp)
            .padding(16.dp)

    ) {
        // Capçalera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Selecciona un repte",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            )

            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tancar",
                    tint = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Llista de reptes
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challenges) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onSelect = {
                        onChallengeSelected(challenge.id, challenge.name)
                    }
                )
            }

        }
    }
}

// Data class per representar un repte
data class Challenge(
    val id: String,
    val name: String,
    val iconRes: Int
)

// Component de la targeta de repte
@Composable
fun ChallengeCard(
    challenge: Challenge,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (currentThemeIsDark) Color.White else Color.Black

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = textColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = onSelect),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = challenge.iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = challenge.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = textColor)
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = "Seleccionar",
                tint = textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}