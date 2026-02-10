package com.burixer85.aipedia.presentation

import androidx.lifecycle.ViewModel
import com.burixer85.aipedia.presentation.models.AI
import com.burixer85.aipedia.presentation.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUI())
    val uiState: StateFlow<HomeScreenUI> = _uiState

}

data class HomeScreenUI(
    val aiList: List<AI> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)

