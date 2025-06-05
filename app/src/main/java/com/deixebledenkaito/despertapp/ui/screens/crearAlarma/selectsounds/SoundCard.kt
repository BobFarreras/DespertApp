package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.preferences.ThemeManager.currentThemeIsDark

/**
* Targeta UI reutilitzable per mostrar i interactuar amb sons
*/
@Composable
fun SoundCard(
    sound: AlarmSound,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isAddSound: Boolean = false,
    onLongClick: (() -> Unit)? = null
) {
    var textButtom = "Seleccionar"
    val textColor = if (currentThemeIsDark) Color.White else Color.Black.copy(alpha = 0.9f)
    val textColorContrari = if (!currentThemeIsDark) Color.White else Color.Black.copy(alpha = 0.9f)
    Card(
        modifier = modifier.fillMaxWidth().combinedClickable(
            onClick = onSelect,
            onLongClick = { onLongClick?.invoke() }
        ),
        colors = CardDefaults.cardColors(containerColor = textColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sound.name, color = textColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                if(!isAddSound) Text(text = if (isPlaying) "Reproduint..." else "Prem per escoltar", color = if(currentThemeIsDark) {Color.LightGray} else {Color.DarkGray})

            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                if(isAddSound){
                    textButtom = "Buscar"
                }else{
                    IconButton(onClick = onPlayToggle) {
                        Icon(
                            imageVector = (if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow),
                            contentDescription = null,
                            tint = textColor
                        )
                    }
                }


                Button(onClick = onSelect, colors = ButtonDefaults.buttonColors(containerColor = textColor)) {
                    Text(textButtom, color = textColorContrari)
                    if(isAddSound) Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = textColorContrari, modifier = Modifier.padding(start = 6.dp))

                }
            }
        }
    }
}
