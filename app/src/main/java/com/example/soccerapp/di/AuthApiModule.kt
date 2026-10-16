
package com.example.soccerapp.di

import com.example.soccerapp.data.remote.api.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthApiModule {

    @Provides
    @Singleton
    fun provideAuthApiService(
        @CommentNetwork retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(
            AuthApiService::class.java
        )
    }
}

//AuthApiServiceが必要
//       ↓
//HiltがAuthApiModuleの
//provideAuthApiService()を見つける
//       ↓
//関数の引数を見る
//@CommentNetwork Retrofit
//       ↓
//CommentNetworkModuleにある
//@CommentNetwork Retrofitの
//生成定義を見つける
//       ↓
//Retrofitを受け取る
//       ↓
//retrofit.create(AuthApiService::class.java)
//       ↓
//AuthApiService完成