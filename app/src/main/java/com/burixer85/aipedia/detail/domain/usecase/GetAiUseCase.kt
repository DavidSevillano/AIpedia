package com.burixer85.aipedia.detail.domain.usecase

import com.burixer85.aipedia.detail.domain.repository.DetailRepository
import com.burixer85.aipedia.core.domain.model.Ai
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetAiUseCase @Inject constructor(private val detailRepository: DetailRepository) {
    suspend operator fun invoke(aiId: UUID): Ai? {
        return detailRepository.getAiById(aiId)
    }
}