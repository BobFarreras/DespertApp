package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object CustomSoundManager {
    private const val PREFS_NAME = "custom_sounds"
    private const val KEY_SOUNDS = "sounds_list"

    private val gson = Gson()
    private val type = object : TypeToken<List<CustomAlarmSound>>() {}.type

    fun saveCustomSound(context: Context, sound: CustomAlarmSound) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getCustomSounds(context).toMutableList()

        // Elimina si ja existia amb el mateix URI
        current.removeAll { it.uriString == sound.uriString }
        current.add(sound)

        val json = gson.toJson(current)
        prefs.edit { putString(KEY_SOUNDS, json) }
    }

    fun getCustomSounds(context: Context): List<CustomAlarmSound> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SOUNDS, null) ?: return emptyList()
        return try {
            gson.fromJson<List<CustomAlarmSound>>(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun removeCustomSound(context: Context, uriString: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getCustomSounds(context).filter { it.uriString != uriString }
        prefs.edit { putString(KEY_SOUNDS, gson.toJson(current)) }
    }

    fun clearAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY_SOUNDS) }
    }
}
