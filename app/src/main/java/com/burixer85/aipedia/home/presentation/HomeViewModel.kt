package com.burixer85.aipedia.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.home.domain.usecase.GetAllAisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllAisUseCase: GetAllAisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUI(isLoading = true))
    val uiState: StateFlow<HomeScreenUI> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAllAisUseCase().collect { ais ->
                _uiState.update { it.copy(aiList = ais, isLoading = false) }
            }
        }
    }
}

data class HomeScreenUI(
    val aiList: List<Ai> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)

