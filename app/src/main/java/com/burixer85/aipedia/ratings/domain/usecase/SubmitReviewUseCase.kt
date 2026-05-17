package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import javax.inject.Inject

class SubmitReviewUseCase @Inject constructor(private val ratingsRepository: RatingsRepository) {
    suspend operator fun invoke(
        aiId: String,
        userId: String,
        displayName: String,
        score: Int,
        body: String?
    ) = ratingsRepository.submitReview(aiId, userId, displayName, score, body)
}
