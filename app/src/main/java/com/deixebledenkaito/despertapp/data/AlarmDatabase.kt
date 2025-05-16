package com.deixebledenkaito.despertapp.data

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// AlarmDatabase.kt
@Database(entities = [AlarmEntity::class], version = 1)
@TypeConverters(com.deixebledenkaito.despertapp.data.Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao // Proporciona accés al DAO

    companion object {
        @Volatile private var INSTANCE: AlarmDatabase? = null

        // Crea o retorna la instància singleton de la base de dades
        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}