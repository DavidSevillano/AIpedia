package com.burixer85.aipedia.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.domain.model.toPresentation
import com.burixer85.aipedia.domain.usecase.HomeUseCase
import com.burixer85.aipedia.presentation.model.Ai
import com.burixer85.aipedia.presentation.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUI(isLoading = true))
    val uiState: StateFlow<HomeScreenUI> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            homeUseCase().collect { ais ->
                _uiState.update { it -> it.copy(aiList = ais.map { it.toPresentation() }, isLoading = false) }
            }
        }
    }
}

data class HomeScreenUI(
    val aiList: List<Ai> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)

