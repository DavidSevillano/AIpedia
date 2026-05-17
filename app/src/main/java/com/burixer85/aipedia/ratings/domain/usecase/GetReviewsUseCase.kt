package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(private val ratingsRepository: RatingsRepository) {
    suspend operator fun invoke(aiId: String): List<Review> =
        ratingsRepository.getReviews(aiId)
}
