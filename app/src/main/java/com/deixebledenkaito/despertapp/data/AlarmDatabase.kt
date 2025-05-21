package com.deixebledenkaito.despertapp.data

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// AlarmDatabase.kt
@Database(entities = [AlarmEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao // Proporciona accés al DAO

    // AlarmDatabase.kt
    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null
        // Patró Singleton per a la instància de la base de dades
        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_db"
                )
                    .addMigrations(MIGRATION_3_4) // Afegeix aquesta línia
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE alarms ADD COLUMN repeatType TEXT NOT NULL DEFAULT 'Diàriament'")
            }
        }
    }
}