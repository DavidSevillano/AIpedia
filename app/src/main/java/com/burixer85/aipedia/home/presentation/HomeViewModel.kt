package com.burixer85.aipedia.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.home.domain.usecase.GetAllAisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllAisUseCase: GetAllAisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUI(isLoading = true))
    val uiState: StateFlow<HomeScreenUI> = _uiState.asStateFlow()

    private val _originalAiList = MutableStateFlow<List<Ai>>(emptyList())

    init {
        loadData()
        setupFilteringLogic()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getAllAisUseCase()
                .onEach { ais ->
                    _originalAiList.value = ais
                }
                .collect { ais ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            aiList = ais,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun setupFilteringLogic() {
        combine(
            _originalAiList,
            _uiState.map { it.searchText }.debounce(300L)
        ) { originalList, searchText ->
            if (searchText.isBlank()) {
                originalList
            } else {
                originalList.filter { ai ->
                    ai.name.contains(searchText, ignoreCase = true)
                }
            }
        }
            .onEach { filteredList ->
                _uiState.update { it.copy(aiList = filteredList) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchTextChange(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
    }
}

data class HomeScreenUI(
    val aiList: List<Ai> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchText: String = "",
    val isLoading: Boolean = false
)

