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
        return OkHttpClient.Builder()//OkHttpClientを生成する
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
            .baseUrl("http://10.0.2.2:8080/")//ローカルホスト（Ktorがここで動いている）
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                "application/json".toMediaType()//ここでList<CommentDto>へ変換。
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


//イメージ→(CommentApiService::class.java)
//class GeneratedCommentApiService : CommentApiService {
//
//    override suspend fun getComments(
//        matchId: Int
//    ): List<CommentDto> {
//
//        // @GETを見る
//        // @Pathを見る
//        // HTTPリクエストを作る
//        // OkHttpで送る
//        // JSONを受け取る
//        // List<CommentDto>へ変換する
//
//        return ...
//}
//}