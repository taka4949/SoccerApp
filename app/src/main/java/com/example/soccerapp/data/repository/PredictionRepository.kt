
package com.example.soccerapp.data.repository

import com.example.soccerapp.data.model.Prediction

interface PredictionRepository {

    suspend fun createPrediction(
        matchId: Int,
        predictedHomeScore: Int,
        predictedAwayScore: Int
    ): Prediction



    suspend fun getMyPrediction(//ユーザーが予想したものを取得。
        matchId: Int
    ): Prediction?
}
