package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.deixebledenkaito.despertapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// SelectSoundViewModel.kt
@HiltViewModel
class SelectSoundViewModel @Inject constructor(
    private val context: Application
) : AndroidViewModel(context) {

    private val _allSounds = MutableStateFlow<List<AlarmSound>>(emptyList())
    val allSounds: StateFlow<List<AlarmSound>> = _allSounds.asStateFlow()

    init {
        loadSounds()
    }

    fun loadSounds() {
        val predefinedSounds = listOf(
            AlarmSound("default", "So per defecte", R.raw.alarm),
            AlarmSound("Piano", "Piano 1", R.raw.piano),
            AlarmSound("bolaDeDrac", "Bola de Drac", R.raw.boladedrac),
            AlarmSound("insultsCatala", "'Pol Gise' Insults Catalans", R.raw.insults),
            AlarmSound("Vegeta", "Vegeta", R.raw.vegeta),
            AlarmSound("RumbaMediterrani", "Rumba vora el mar", R.raw.rumbamediterrani),
            AlarmSound("PianoDos", "Piano 2", R.raw.pianodos),
            AlarmSound("PimPomParty", "Pim Pom Party", R.raw.pimpom)
        )

        val customSounds = CustomSoundManager.getCustomSounds(context)
            .map { AlarmSound(it.uriString, it.name, null, it.startTimeMs.toLong()) }

        _allSounds.value = predefinedSounds + customSounds
    }


    fun refreshCustomSounds() = loadSounds() // per coherència
}
