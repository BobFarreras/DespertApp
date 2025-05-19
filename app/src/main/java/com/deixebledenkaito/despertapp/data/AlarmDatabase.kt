package com.deixebledenkaito.despertapp.data

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// AlarmDatabase.kt
@Database(entities = [AlarmEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao // Proporciona accés al DAO

    // AlarmDatabase.kt
    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_db"
                )
                    .addMigrations(MIGRATION_2_3) // Afegeix aquesta línia
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE alarms ADD COLUMN challengeType TEXT NOT NULL DEFAULT 'Matemàtiques'")
            }
        }
    }
}