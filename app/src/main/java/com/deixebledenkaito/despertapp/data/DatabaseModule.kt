package com.deixebledenkaito.despertapp.data


import android.content.Context
import androidx.room.Room
import com.deixebledenkaito.despertapp.data.AlarmDao
import com.deixebledenkaito.despertapp.data.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlarmDatabase {
        return Room.databaseBuilder(
            context,
            AlarmDatabase::class.java,
            "alarm_db"
        ).build()
    }

    @Provides
    fun provideAlarmDao(db: AlarmDatabase): AlarmDao {
        return db.alarmDao()
    }
}
