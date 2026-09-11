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
    ): SoccerRepository//これはインターフェース！。
}

//abstractとは、処理内容が書かれていない関数が残っている、印。


//Moduleが必要な理由
//
//Hiltがそのままでは作れない場合です。
//
//interface
//→ どの実装を使うか分からない
//→ @Bindsを置くModuleが必要
//
//Retrofitなどの外部クラス
//→ @Inject constructorを書けない
//→ @Providesを置くModuleが必要
//
//今回の整理です。
//
//MatchThreadViewModel
//→ @HiltViewModel + @Inject
//→ Module不要
//
//NetworkCommentRepository本体
//→ @Inject constructor
//→ 生成だけならModule不要
//
//CommentRepository
//→ interface
//→ NetworkCommentRepositoryとの接続にModule必要
//
//Retrofit
//→ 外部クラス
//→ 作り方を示すModule必要