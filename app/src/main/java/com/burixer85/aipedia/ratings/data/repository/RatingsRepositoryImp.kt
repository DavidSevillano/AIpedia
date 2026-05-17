package com.burixer85.aipedia.ratings.data.repository

import com.burixer85.aipedia.ratings.data.model.RatingDto
import com.burixer85.aipedia.ratings.data.model.RatingInsertDto
import com.burixer85.aipedia.ratings.data.model.ReviewDto
import com.burixer85.aipedia.ratings.data.model.ReviewInsertDto
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class RatingsRepositoryImp @Inject constructor(
    private val supabase: SupabaseClient
) : RatingsRepository {

    override suspend fun getRatingSummary(aiId: String): RatingSummary {
        val reviews = supabase.from("ai_reviews")
            .select { filter { eq("ai_id", aiId) } }
            .decodeList<ReviewDto>()

        if (reviews.isEmpty()) return RatingSummary(0f, 0, (1..5).associateWith { 0 })

        val distribution = (1..5).associateWith { star -> reviews.count { it.score == star } }
        val average = reviews.map { it.score }.average().toFloat()
        return RatingSummary(average = average, totalCount = reviews.size, distribution = distribution)
    }

    override suspend fun getReviews(aiId: String): List<Review> =
        supabase.from("ai_reviews")
            .select { filter { eq("ai_id", aiId) } }
            .decodeList<ReviewDto>()
            .map { it.toDomain() }

    override suspend fun getUserRating(aiId: String, deviceId: String): Int? =
        supabase.from("ai_ratings")
            .select {
                filter {
                    eq("ai_id", aiId)
                    eq("device_id", deviceId)
                }
            }
            .decodeList<RatingDto>()
            .firstOrNull()?.score

    override suspend fun getUserReview(aiId: String, userId: String): Review? =
        supabase.from("ai_reviews")
            .select {
                filter {
                    eq("ai_id", aiId)
                    eq("user_id", userId)
                }
            }
            .decodeList<ReviewDto>()
            .firstOrNull()?.toDomain()

    override suspend fun submitRating(aiId: String, deviceId: String, score: Int) {
        supabase.from("ai_ratings").upsert(
            RatingInsertDto(aiId = aiId, deviceId = deviceId, score = score)
        ) { onConflict = "ai_id,device_id" }
    }

    override suspend fun submitReview(
        aiId: String, userId: String, displayName: String, score: Int, body: String?
    ) {
        supabase.from("ai_reviews").upsert(
            ReviewInsertDto(aiId = aiId, userId = userId, displayName = displayName, score = score, body = body)
        ) { onConflict = "ai_id,user_id" }
    }
}
