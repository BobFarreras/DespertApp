package com.deixebledenkaito.despertapp.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

object AlarmPreferencesManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "alarm_prefs")

    private val VOLUME = intPreferencesKey("volume")
    private val VIBRATION = booleanPreferencesKey("vibration_enabled")
    private val INCREASING_VOLUME = booleanPreferencesKey("increasing_volume")

    suspend fun savePreferences(context: Context, prefs: AlarmPreferences) {
        context.dataStore.edit {
            it[VOLUME] = prefs.volume
            it[VIBRATION] = prefs.vibrationEnabled
            it[INCREASING_VOLUME] = prefs.increasingVolume
        }
    }

    suspend fun loadPreferences(context: Context): AlarmPreferences {
        val prefs = context.dataStore.data.first()
        return AlarmPreferences(
            volume = prefs[VOLUME] ?: 80,
            vibrationEnabled = prefs[VIBRATION] != false,
            increasingVolume = prefs[INCREASING_VOLUME] == true
        )
    }
}