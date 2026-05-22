package com.burixer85.aipedia.home.domain.usecase

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAllAisUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    operator fun invoke(): Flow<List<Ai>> = channelFlow {
        launch {
            try { homeRepository.loadAndCacheInitialData() } catch (_: Exception) {}
        }
        homeRepository.getAllAis().collect { send(it) }
    }
}