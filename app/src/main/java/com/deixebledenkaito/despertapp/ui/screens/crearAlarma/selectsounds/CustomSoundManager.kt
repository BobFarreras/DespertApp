package com.deixebledenkaito.despertapp.ui.screens.crearAlarma.selectsounds

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import androidx.core.content.edit
import com.google.gson.JsonParser

object CustomSoundManager {
    private const val PREFS_NAME = "custom_sounds"
    private const val KEY_SOUNDS = "sounds_list"

    private val gson = Gson()

    fun getCustomSounds(context: Context): List<CustomAlarmSound> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SOUNDS, null) ?: return emptyList()

        return try {
            val jsonArray = JsonParser.parseString(json).asJsonArray
            jsonArray.map { gson.fromJson(it, CustomAlarmSound::class.java) }
        } catch (e: Exception) {
            Log.e("CustomSoundManager", "Error reading sounds", e)
            emptyList()
        }
    }

    fun saveCustomSound(context: Context, sound: CustomAlarmSound) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getCustomSounds(context).toMutableList()

        // Elimina si ja existia amb el mateix URI
        current.removeAll { it.uriString == sound.uriString }
        current.add(sound)

        val json = gson.toJson(current)
        prefs.edit { putString(KEY_SOUNDS, json) }
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

    fun updateCustomSounds(context: Context, list: List<CustomAlarmSound>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_SOUNDS, gson.toJson(list)) }
    }
}
