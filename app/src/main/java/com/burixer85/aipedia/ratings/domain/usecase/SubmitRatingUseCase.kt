package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import javax.inject.Inject

class SubmitRatingUseCase @Inject constructor(private val ratingsRepository: RatingsRepository) {
    suspend operator fun invoke(aiId: String, deviceId: String, score: Int) =
        ratingsRepository.submitRating(aiId, deviceId, score)
}
