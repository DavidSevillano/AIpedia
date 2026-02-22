package com.burixer85.aipedia.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.detail.domain.usecase.GetAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getAiUseCase: GetAiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailScreenUI(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun loadAiDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val ai = getAiUseCase(UUID.fromString(id))

            _uiState.update { it.copy(ai = ai, isLoading = false) }
        }
    }
}

data class DetailScreenUI(
    val ai: Ai? = null,
    val isLoading: Boolean = false,
)
