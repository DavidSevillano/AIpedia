package com.burixer85.aipedia.compare.domain.usecase

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetAllAisForPickerUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<List<Ai>> =
        homeRepository.getAllAis()
            .onStart {
                try { homeRepository.loadAndCacheInitialData() } catch (_: Exception) {}
            }
}
