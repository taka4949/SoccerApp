package com.example.soccerapp.di

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
import com.example.soccerapp.data.remote.api.SoccerApiService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton//生成は1回。
    @FootballNetwork//返り値が複数ある場合（OkHttp,Retrofitが該当）、Hiltが識別できるようにするため。
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->//interceptは、通信をいったん止めている→proceed必須
                val request = chain.request()//ベースurl取得
                    .newBuilder()
                    .header(
                        "X-Auth-Token",
                        BuildConfig.FOOTBALL_DATA_API_TOKEN
                    )//local.propertiesから受け取るトークン（Config)。
                    .build()//リクエストの完成

                chain.proceed(request)//通信を続ける
            }
            .build()//okhttpの完成。build()はhiltによる道具の作成時（1週目）のみ行われる。
    }


    @Provides
    @Singleton
    @FootballNetwork
    fun provideRetrofit(
        @FootballNetwork okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true//dtoに存在しないものは無視していいというルール
        }

        return Retrofit.Builder()
            .baseUrl("https://api.football-data.org/v4/")//これがもともとあって、完成する。
            .client(okHttpClient)//ここで、clientは（）を使うよう指示している。
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()//ここで道具すべてが完成する。
    }//ここで2週目～に帰ってきたデータをsoccerApiServiceのgetCompetitions()に送られる。


    @Provides
    @Singleton
    fun provideSoccerApiService(
        @FootballNetwork retrofit: Retrofit
    ): SoccerApiService {
        return retrofit.create(
            SoccerApiService::class.java//ここで、RetrofitとSoccerApiServiceのものが合体する、すべてが完成して、返す。
        )//SoccerApiServiceという型を返す。create()。ここでこの返り値の理由はhiltで追うため。
        //ここは1週目のhiltの準備だけしか通らない。大事→base url + competitionsでget通信する、きっかけをつくる。
    }
}
//Retrofitのイメージ→class GeneratedSoccerApiService : SoccerApiService {
//
//    override suspend fun getCompetitions(): CompetitionResponseDto {
//
//        // baseUrl + @GET("competitions")
//        // ↓
//        // OkHttpで通信
//        // ↓
//        // JSONを取得
//        // ↓
//        // ConverterでDTOへ変換
//
//        return convertedDto（ここで、matchrepositoryのもとへいく）
//    }
//}