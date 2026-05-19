package com.burixer85.aipedia.compare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.compare.domain.usecase.GetAllAisForPickerUseCase
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.usecase.GetRatingSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PickerTarget { A, B }

data class CompareUiState(
    val aiA: Ai? = null,
    val aiB: Ai? = null,
    val summaryA: RatingSummary? = null,
    val summaryB: RatingSummary? = null,
    val pickerTarget: PickerTarget? = null,
    val allAis: List<Ai> = emptyList(),
    val pickerQuery: String = ""
) {
    val filteredAis: List<Ai>
        get() = if (pickerQuery.isBlank()) allAis
                else allAis.filter { it.name.contains(pickerQuery, ignoreCase = true) }
}

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getAllAisForPickerUseCase: GetAllAisForPickerUseCase,
    private val getRatingSummaryUseCase: GetRatingSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllAisForPickerUseCase()
                .catch { }
                .collect { ais -> _uiState.update { it.copy(allAis = ais) } }
        }
    }

    fun openPicker(target: PickerTarget) =
        _uiState.update { it.copy(pickerTarget = target, pickerQuery = "") }

    fun closePicker() =
        _uiState.update { it.copy(pickerTarget = null, pickerQuery = "") }

    fun onQueryChange(query: String) =
        _uiState.update { it.copy(pickerQuery = query) }

    fun selectAi(ai: Ai) {
        val target = _uiState.value.pickerTarget ?: return
        _uiState.update {
            when (target) {
                PickerTarget.A -> it.copy(aiA = ai, pickerTarget = null, pickerQuery = "")
                PickerTarget.B -> it.copy(aiB = ai, pickerTarget = null, pickerQuery = "")
            }
        }
        fetchSummary(ai.id, target)
    }

    fun clearSlot(target: PickerTarget) =
        _uiState.update {
            when (target) {
                PickerTarget.A -> it.copy(aiA = null, summaryA = null)
                PickerTarget.B -> it.copy(aiB = null, summaryB = null)
            }
        }

    private fun fetchSummary(aiId: String, target: PickerTarget) {
        viewModelScope.launch {
            try {
                val summary = getRatingSummaryUseCase(aiId)
                _uiState.update {
                    when (target) {
                        PickerTarget.A -> it.copy(summaryA = summary)
                        PickerTarget.B -> it.copy(summaryB = summary)
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
