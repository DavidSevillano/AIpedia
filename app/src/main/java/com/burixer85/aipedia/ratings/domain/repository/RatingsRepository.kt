package com.burixer85.aipedia.ratings.domain.repository

import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.model.Review

interface RatingsRepository {
    suspend fun getRatingSummary(aiId: String): RatingSummary
    suspend fun getReviews(aiId: String): List<Review>
    suspend fun getUserRating(aiId: String, deviceId: String): Int?
    suspend fun getUserReview(aiId: String, userId: String): Review?
    suspend fun submitRating(aiId: String, deviceId: String, score: Int)
    suspend fun submitReview(aiId: String, userId: String, displayName: String, score: Int, body: String?)
}
