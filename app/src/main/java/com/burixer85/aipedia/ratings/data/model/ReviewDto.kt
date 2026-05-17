package com.burixer85.aipedia.ratings.data.model

import com.burixer85.aipedia.ratings.domain.model.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    @SerialName("id") val id: String,
    @SerialName("ai_id") val aiId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("score") val score: Int,
    @SerialName("body") val body: String? = null,
    @SerialName("created_at") val createdAt: String
) {
    fun toDomain() = Review(
        id = id, aiId = aiId, userId = userId,
        displayName = displayName, score = score,
        body = body, createdAt = createdAt
    )
}

@Serializable
data class ReviewInsertDto(
    @SerialName("ai_id") val aiId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("score") val score: Int,
    @SerialName("body") val body: String? = null
)
