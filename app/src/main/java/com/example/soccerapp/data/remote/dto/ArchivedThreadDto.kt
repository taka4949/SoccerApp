

package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArchivedThreadDto(
    val threadId: Long,
    val number: Int
)

