
package com.example.soccerapp.di
import com.example.soccerapp.data.remote.api.PredictionApiService
import com.example.soccerapp.data.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PredictionNetwork

@Module
@InstallIn(SingletonComponent::class)
object PredictionNetworkModule {

    @Provides
    @Singleton
    @PredictionNetwork
    fun providePredictionOkHttpClient(
        @CommentNetwork baseClient: OkHttpClient,
        tokenRepository: TokenRepository
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor { chain ->



                val token = runBlocking {//通常の関数からsuspend関数を実行し、その処理の完了を待つため。
                    tokenRepository.getToken()//suspend関数
                }

                val requestBuilder = chain.request()//"matches/{matchId}/predictionsなどの取得。
                    .newBuilder()

                if (token != null) {
                    requestBuilder.header(
                        "Authorization",
                        "Bearer $token"
                    )
                }

                chain.proceed(requestBuilder.build())//通信開始
            }
            .build()
    }

    @Provides
    @Singleton
    @PredictionNetwork//戻り値も識別として指定
    fun providePredictionRetrofit(
        @CommentNetwork baseRetrofit: Retrofit,
        @PredictionNetwork okHttpClient: OkHttpClient
    ): Retrofit {
        return baseRetrofit.newBuilder()
            .client(okHttpClient)
            .build()
    }

    @Provides//この関数が PredictionApiServiceを生成・提供するとHiltに教える
    @Singleton
    fun providePredictionApiService(
        @PredictionNetwork retrofit: Retrofit
    ): PredictionApiService {
        return retrofit.create(
            PredictionApiService::class.java//PredictionApiService(ただのinterface)の実装を生成している
        )
    }
}

//理解用のデータフロー↓

//Hiltによる準備（インスタンス生成時）

//PredictionNetworkModule
//    ↓
//providePredictionOkHttpClient()
//    │ 認証Interceptorを設定
//    ↓
//providePredictionRetrofit()
//    │ OkHttpClientを組み込む
//    ↓
//providePredictionApiService()
//    │ retrofit.create(...)
//    │ APIの実装を生成→自分でHTTP通信処理を一から実装しなくて済む！！！！！！
//    ↓
//PredictionApiServiceの実装オブジェクト
//    ↓
//NetworkPredictionRepositoryへ注入



//実際の予想登録時

//NetworkPredictionRepository
//    │
//    │ createPrediction(500, 2, 1)
//    ↓
//PredictionApiServiceの実装オブジェクト
//    │
//    │ @POST・@Path・@Bodyをもとに
//    │ HTTPリクエストを作成
//    ↓
//OkHttp Interceptor
//    │
//    │ chain.request()でリクエスト取得
//    │ header()で認証トークン追加
//    ↓
//chain.proceed()
//    │
//    │ 完成したリクエストで通信
//    ↓
//Ktor
//    ↓
//PostgreSQL