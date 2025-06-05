package com.deixebledenkaito.despertapp.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object TemesPreferencesManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "temes_prefs")

    private val DARK_ENABLED = booleanPreferencesKey("dark_enabled")
    private val LIGHT_ENABLED = booleanPreferencesKey("light_enabled")

    suspend fun savePreferences(context: Context, prefs: TemesPreferences) {
        context.dataStore.edit {
            it[DARK_ENABLED] = prefs.darkEnabled
            it[LIGHT_ENABLED] = prefs.lightEnabled
        }
    }

    suspend fun loadPreferences(context: Context): TemesPreferences {
        val prefs = context.dataStore.data.first()
        return TemesPreferences(
            darkEnabled = prefs[DARK_ENABLED] != false,
            lightEnabled = prefs[LIGHT_ENABLED] == true
        )
    }
}