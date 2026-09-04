package com.example.soccerapp.di

import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.soccerapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.example.soccerapp.data.remote.api.CommentApiService


@Module
@InstallIn(SingletonComponent::class)
object CommentNetworkModule {

    @Provides
    @Singleton
    @CommentNetwork
    fun commentProvideOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()//これは.Builder()という型になるから、.build()で型を正す。
            .build()
    }

    @Provides
    @Singleton
    @CommentNetwork
    fun commentProvideRetrofit(
        @CommentNetwork okHttpClient: OkHttpClient
    ):Retrofit{
        val json = Json{
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideCommentApiService(
        @CommentNetwork retrofit:Retrofit
    ): CommentApiService{
        return retrofit.create(
            CommentApiService::class.java
        )
    }
}