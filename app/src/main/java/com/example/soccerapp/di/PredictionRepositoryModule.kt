
package com.example.soccerapp.di

import com.example.soccerapp.data.repository.PredictionRepository
import com.example.soccerapp.data.repository.NetworkPredictionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
abstract class PredictionRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPredictionRepository(
        implementation: NetworkPredictionRepository
    ): PredictionRepository
}



//PredictionViewModel
//    │
//    │ PredictionRepository型を要求
//    ↓
//Hilt
//    │
//    │ @Bindsの設定を確認
//    ↓
//NetworkPredictionRepositoryを用意
//    │
//    │ 必要なPredictionApiServiceも用意
//    ↓
//PredictionViewModelへ注入
