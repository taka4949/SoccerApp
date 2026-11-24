
package com.example.soccerapp.server

import kotlinx.serialization.Serializable

@Serializable
data class CreatePredictionRequest(//予想スコア
    val predictedHomeScore: Int,
    val predictedAwayScore: Int
)

@Serializable
data class PredictionResponse(//id,matchIdは後々利用。履歴から変更など。
    val id: Long,
    val matchId: Int,
    val predictedHomeScore: Int,
    val predictedAwayScore: Int,
    val earnedPoints: Int?
)


//Android
//  │
//  │ CreatePredictionRequestDto
//  │
//  ↓ JSON送信
//Ktor
//  │
//  │ CreatePredictionRequest
//  │
//  ↓
//Repository → PostgreSQL
//  │
//  ↓
//Ktor
//  │
//  │ PredictionResponse
//  │
//  ↓ JSON返却
//Android
//  │
//  │ PredictionDto
//  ↓
//Prediction（アプリ内部のモデル）