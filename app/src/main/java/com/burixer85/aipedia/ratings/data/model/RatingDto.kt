package com.burixer85.aipedia.ratings.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatingDto(
    @SerialName("id") val id: String,
    @SerialName("ai_id") val aiId: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("score") val score: Int
)

@Serializable
data class RatingInsertDto(
    @SerialName("ai_id") val aiId: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("score") val score: Int
)
