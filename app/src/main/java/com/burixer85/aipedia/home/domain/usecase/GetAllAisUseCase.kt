package com.burixer85.aipedia.home.domain.usecase

import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAllAisUseCase @Inject constructor(private val homeRepository: HomeRepository) {

    private val _syncCompleted = MutableStateFlow(false)
    val syncCompleted: StateFlow<Boolean> = _syncCompleted.asStateFlow()

    operator fun invoke(): Flow<List<Ai>> = channelFlow {
        launch {
            try { homeRepository.loadAndCacheInitialData() } catch (_: Exception) {}
            _syncCompleted.value = true
        }
        homeRepository.getAllAis().collect { send(it) }
    }
}