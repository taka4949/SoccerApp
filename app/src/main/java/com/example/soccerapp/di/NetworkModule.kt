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
    @Singleton//返す値を1つにする
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->//interceptは、通信をいったん止めている→proceed必須
                val request = chain.request()//
                    .newBuilder()
                    .header(
                        "X-Auth-Token",
                        BuildConfig.FOOTBALL_DATA_API_TOKEN
                    )
                    .build()//リクエストの完成

                chain.proceed(request)//通信を続ける
            }
            .build()//okhttpの完成。build()はhiltによる道具の作成時（1週目）のみ行われる。
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true//dtoに存在しないものは無視していいというルール
        }

        return Retrofit.Builder()
            .baseUrl("https://api.football-data.org/v4/")//これがもともとあって、完成する。
            .client(okHttpClient)//ここで、送信（ではないが、ざっくり理解）clientは（）を使うよう指示している。
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()//ここで道具すべてが完成する。2週目、ここでjsonが帰ってきてkotlinに変換。
    }//ここで2週目～に帰ってきたデータをsoccerApiServiceのgetcompetitions()に送られる。


    @Provides
    @Singleton
    fun provideSoccerApiService(
        retrofit: Retrofit
    ): SoccerApiService {
        return retrofit.create(
            SoccerApiService::class.java//ここで、retorofitとapiseriviceのものが合体する、すべてが完成して、返す。
        )//soccerApiServiseという型を返す。create()。ここでこの返り値の理由はhiltで追うため。
        //ここは1週目のhiltの準備だけしか通らない。ここで、base url + competitionsでget通信する、きっかけをつくる。
    }
}