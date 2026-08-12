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
object DatabaseModule {//objectとは、1個だけ使うという意味。これはここしか存在する必要がない。api

    @Provides//下に書く関数がかえすものをhiltに登録する
    @Singleton
    fun provideSoccerDatabase(
        @ApplicationContext context: Context
    ): SoccerDatabase {
        return Room.databaseBuilder(
            context,
            SoccerDatabase::class.java,//クラスの設計を渡している。
            "soccer_database"//端末内に作成するSQLiteデータベースファイルの名前
        ).build()//↑どの保存先をどのデータベース設計で使うのかを設定している。
    }

    @Provides
    @Singleton
    fun provideMatchDao(
        database: SoccerDatabase
    ): MatchDao {
        return database.matchDao()
    }
}

//このファイルの役割
//1. SoccerDatabaseは、この方法で用意する
//2. MatchDaoは、SoccerDatabaseから取り出す

//流れ。①MatchRepository
//「MatchDaoが必要」
//↓
//Hilt
//「DatabaseModuleに用意方法がある」
//↓
//provideMatchDao()を使う
//↓
//そのためにSoccerDatabaseが必要
//↓
//provideSoccerDatabase()を使う
//↓
//RoomでSoccerDatabaseを用意
//↓
//database.matchDao()でMatchDaoを取得
//↓
//MatchRepositoryへ渡す