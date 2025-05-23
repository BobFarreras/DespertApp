package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deixebledenkaito.despertapp.R
import com.deixebledenkaito.despertapp.ui.screens.colors.BackgroundApp

// SelectSoundScreen.kt
@Composable
fun SelectSoundScreen(
    onSoundSelected: (soundId: String, soundName: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Llista de sons disponibles
    val alarmSounds = listOf(
        AlarmSound("default", "So per defecte", R.raw.alarm),
        AlarmSound("bebe", "Plors de Bebe", R.raw.bebe),
        AlarmSound("bolaDeDrac", "Bola de Drac", R.raw.boladedrac),
        AlarmSound("insultsCatala", "Insults Catalans", R.raw.insults),
        AlarmSound("SoldatAlarma", "Motivacio Soldat", R.raw.soldatalarma),
        AlarmSound("Vegeta", "Vegeta", R.raw.vegeta),
        AlarmSound("LinkinPark", "Linkin Park", R.raw.linkpark),
        AlarmSound("ImagineDragons", "Imagine Dragons", R.raw.imagindragons)
    )

    val current = LocalContext.current
    var currentlyPlaying by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = BackgroundApp(),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(16.dp)
    ) {
        // Capçalera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Selecciona un so",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tancar",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Llista de sons
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(alarmSounds) { sound ->
                SoundCard(
                    sound = sound,
                    isPlaying = currentlyPlaying == sound.id,
                    onPlayToggle = {
                        if (currentlyPlaying == sound.id) {
                            mediaPlayer?.stop()
                            currentlyPlaying = null
                        } else {
                            mediaPlayer?.stop()
                            mediaPlayer = MediaPlayer.create(current, sound.resourceId).apply {
                                isLooping = true
                                start()
                            }
                            currentlyPlaying = sound.id
                        }
                    },
                    onSelect = {
                        onSoundSelected(sound.id, sound.name)
                    }
                )
            }
        }
    }
}





// Data class per representar un so d'alarma
data class AlarmSound(
    val id: String,
    val name: String,
    @RawRes val resourceId: Int
)

// Component de la targeta de so
@Composable
fun SoundCard(
    sound: AlarmSound,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sound.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isPlaying) "Reproduint..." else "Prem per escoltar",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPlayToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Aturar" else "Reproduir",
                        tint = Color.White
                    )
                }

                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Seleccionar",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}