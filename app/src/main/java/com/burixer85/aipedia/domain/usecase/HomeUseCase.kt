package com.burixer85.aipedia.domain.usecase

import com.burixer85.aipedia.domain.model.Ai
import com.burixer85.aipedia.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    operator fun invoke(): Flow<List<Ai>> {
        return homeRepository.getAllAisWithCategories()
            .onStart {
                homeRepository.loadAndCacheInitialData()
            }
    }
}