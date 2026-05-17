package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import javax.inject.Inject

class GetUserReviewsUseCase @Inject constructor(
    private val ratingsRepository: RatingsRepository
) {
    suspend operator fun invoke(userId: String): List<Review> =
        ratingsRepository.getUserReviews(userId)
}
