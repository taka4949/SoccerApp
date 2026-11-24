
package com.example.soccerapp.data.repository

import com.example.soccerapp.data.model.Prediction
import com.example.soccerapp.data.remote.api.PredictionApiService
import com.example.soccerapp.data.remote.dto.CreatePredictionRequestDto
import com.example.soccerapp.data.remote.dto.PredictionDto
import retrofit2.HttpException
import javax.inject.Inject







class NetworkPredictionRepository @Inject constructor(
    private val predictionApiService: PredictionApiService
) : PredictionRepository {



    override suspend fun createPrediction(
        matchId: Int,
        predictedHomeScore: Int,
        predictedAwayScore: Int
    ): Prediction {

        val predictionDto = predictionApiService.createPrediction(
            matchId = matchId,
            request = CreatePredictionRequestDto(
                predictedHomeScore = predictedHomeScore,
                predictedAwayScore = predictedAwayScore
            )
        )

        return predictionDto.toPrediction()
    }






    override suspend fun getMyPrediction(
        matchId: Int
    ): Prediction? {

        val response = predictionApiService.getMyPrediction(matchId)

        return when (response.code()) {
            200 -> {
                val predictionDto = response.body()
                    ?: throw IllegalStateException(
                        "Prediction response body is missing"
                    )

                predictionDto.toPrediction()
            }




            404 -> null

            else -> throw HttpException(response)
        }
    }
}

private fun PredictionDto.toPrediction(): Prediction {//変換を共通関数にまとめ、コード重複せず済む。
    return Prediction(
        id = id,
        matchId = matchId,
        predictedHomeScore = predictedHomeScore,
        predictedAwayScore = predictedAwayScore,
        earnedPoints = earnedPoints
    )
}
//拡張関数のため、Dto.idにせずにすむ。（インスタンスがthisとして扱われる）
