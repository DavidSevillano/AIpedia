package com.burixer85.aipedia.ratings.domain.usecase

import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.repository.RatingsRepository
import javax.inject.Inject

class GetRatingSummaryUseCase @Inject constructor(private val ratingsRepository: RatingsRepository) {
    suspend operator fun invoke(aiId: String): RatingSummary =
        ratingsRepository.getRatingSummary(aiId)
}
