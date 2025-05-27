package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.net.toUri
import com.deixebledenkaito.despertapp.R


// SelectSoundScreen.kt
@Composable
fun SelectSoundScreen(
    onSoundSelected: (soundId: String, soundName: String) -> Unit,
    onCancel: () -> Unit,
    viewModel: SelectSoundViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var currentlyPlaying by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val allSounds by viewModel.allSounds.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var soundToDelete by remember { mutableStateOf<AlarmSound?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedUri = it
            showDialog = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Selecciona un so", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 32.dp)) {
            item {
                SoundCard(
                    sound = AlarmSound("add_custom", "Afegir so personalitzat", null),
                    isPlaying = false,
                    onPlayToggle = {},
                    onSelect = { launcher.launch(arrayOf("audio/*")) },
                    isAddSound = true,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }
            items(allSounds) { sound ->
                SoundCard(
                    sound = sound,
                    isPlaying = currentlyPlaying == sound.id,
                    onPlayToggle = {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null

                        if (currentlyPlaying == sound.id) {
                            currentlyPlaying = null
                        } else {
                            mediaPlayer = MediaPlayer()

                            try {
                                if (sound.id.startsWith("content://")) {
                                    mediaPlayer?.setDataSource(context, sound.id.toUri())
                                } else {
                                    val resId = when (sound.id) {
                                        "default" -> R.raw.alarm
                                        "Piano" -> R.raw.piano
                                        "bolaDeDrac" -> R.raw.boladedrac
                                        "insultsCatala" -> R.raw.insults
                                        "SoldatAlarma" -> R.raw.soldatalarma
                                        "Vegeta" -> R.raw.vegeta
                                        "LinkinPark" -> R.raw.linkpark
                                        "ImagineDragons" -> R.raw.imagindragons
                                        "RumbaMediterrani" -> R.raw.rumbamediterrani
                                        "PianoDos" -> R.raw.pianodos
                                        else -> null
                                    }

                                    resId?.let {
                                        val afd = context.resources.openRawResourceFd(it)
                                        mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                        afd.close()
                                    }
                                }

                                mediaPlayer?.apply {
                                    setAudioStreamType(AudioManager.STREAM_ALARM)
                                    prepare()
                                    seekTo(sound.startTimeMs?.toInt() ?: 0)
                                    isLooping = true
                                    start()
                                }

                                currentlyPlaying = sound.id
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onSelect = { onSoundSelected(sound.id, sound.name) },
                    onLongClick = {
                        if (sound.id.startsWith("content://")) {
                            soundToDelete = sound
                        }
                    }
                )
            }

        }
    }

    if (showDialog && selectedUri != null) {
        AddCustomSoundDialog(
            uri = selectedUri!!,
            context = context,
            onDismiss = {
                showDialog = false
                selectedUri = null
            },
            onConfirm = { name, uriString, startMs ->
                CustomSoundManager.saveCustomSound(context, CustomAlarmSound(name, uriString, startMs))
                viewModel.refreshCustomSounds() // si tens un ViewModel per actualitzar
            }
        )
    }
    if (soundToDelete != null) {
        AlertDialog(
            onDismissRequest = { soundToDelete = null },
            title = { Text("Eliminar so") },
            text = { Text("Segur que vols eliminar \"${soundToDelete!!.name}\"?") },
            confirmButton = {
                Button(onClick = {
                    CustomSoundManager.removeCustomSound(context, soundToDelete!!.id)
                    viewModel.refreshCustomSounds()
                    soundToDelete = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { soundToDelete = null }) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}


