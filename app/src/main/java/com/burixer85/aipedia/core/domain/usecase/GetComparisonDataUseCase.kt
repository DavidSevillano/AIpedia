package com.burixer85.aipedia.core.domain.usecase

import com.burixer85.aipedia.core.domain.model.ComparisonData
import com.burixer85.aipedia.core.domain.repository.ComparisonRepository
import javax.inject.Inject

class GetComparisonDataUseCase @Inject constructor(
    private val comparisonRepository: ComparisonRepository
) {
    suspend operator fun invoke(aiId: String): ComparisonData =
        comparisonRepository.getComparisonData(aiId)
}
