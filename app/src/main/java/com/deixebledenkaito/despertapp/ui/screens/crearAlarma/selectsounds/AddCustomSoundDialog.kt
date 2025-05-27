package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer


@Composable
fun AddCustomSoundDialog(
    uri: Uri,
    context: Context,
    onDismiss: () -> Unit,
    onConfirm: (name: String, uri: String, startPositionMs: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var durationMs by remember { mutableLongStateOf(0L) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            addListener(object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    durationMs = duration
                }
            })
        }
    }
// Controlar millor l'estat del reproductor
    LaunchedEffect(player) {
        durationMs = player.duration.takeIf { it > 0 } ?: 0L
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Afegir so personalitzat") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom del so") },
                    singleLine = true
                )

                if (durationMs > 0) {
                    Text("Escull el punt d'inici: ${(sliderPosition * durationMs).toLong() / 1000}s")

                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 0f..1f
                    )

                    Button(
                        onClick = {
                            val startMs = (sliderPosition * durationMs).toLong()
                            player.seekTo(startMs)
                            player.playWhenReady = true
                            isPlaying = true
                        }
                    ) {
                        Text(if (isPlaying) "Reproduint..." else "Reproduir des d'aquí")
                    }
                } else {
                    Text("Carregant durada del fitxer...")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val startMs = (sliderPosition * durationMs).toLong()
                onConfirm(name, uri.toString(), startMs)
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                player.stop()
                onDismiss()
            }) {
                Text("Cancel·lar")
            }
        }
    )
}
