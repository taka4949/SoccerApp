package com.example.soccerapp.di

import android.content.Context
import androidx.room.Room
import com.example.soccerapp.data.local.dao.MatchDao
import com.example.soccerapp.data.local.database.SoccerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSoccerDatabase(
        @ApplicationContext context: Context
    ): SoccerDatabase {
        return Room.databaseBuilder(
            context,
            SoccerDatabase::class.java,
            "soccer_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMatchDao(
        database: SoccerDatabase
    ): MatchDao {
        return database.matchDao()
    }
}