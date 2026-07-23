package com.example.soccerapp.di

import com.example.soccerapp.data.repository.MatchRepository
import com.example.soccerapp.data.repository.SoccerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module//hiltへの作成や接続ルール(このファイル全体含め）

@InstallIn(SingletonComponent::class)//接続ルールをどの範囲で使用できるようにするか指定

abstract class RepositoryModule {//Hiltがこの宣言を読み、必要なコードを生成

    @Binds//引数の実装クラスを、戻り値のinterfaceとして結び付ける
    @Singleton//Repositoryをアプリのプロセス内で1個だけ作り、同じものを再利用可能にしている

    abstract fun bindSoccerRepository(
        implementation: MatchRepository//これが中身。インターフェースは空のルール。
    ): SoccerRepository
}