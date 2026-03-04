package com.burixer85.aipedia.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.core.domain.repository.AdRepository
import com.burixer85.aipedia.core.util.AdConfig
import com.burixer85.aipedia.home.domain.usecase.GetAllAisUseCase
import com.burixer85.aipedia.home.domain.usecase.GetAllCategoriesUseCase
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
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
    private val getAllAisUseCase: GetAllAisUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val adRepository: AdRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUI(isLoading = true))
    val uiState: StateFlow<HomeScreenUI> = _uiState.asStateFlow()

    private val _originalAiList = MutableStateFlow<List<Ai>>(emptyList())

    private val _searchText = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    init {
        loadAis()
        loadCategories()
        setupFilteringLogic()
    }

    private fun loadAis() {
        _uiState.update { it.copy(isLoading = true) }

        getAllAisUseCase()
            .onEach { ais ->
                _originalAiList.value = ais
                _uiState.update { it.copy(aiList = ais, isLoading = false) }
                val count = calculateAdCount(ais.size)
                if (count > 0) {
                    adRepository.loadAds(count)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadCategories() {
        getAllCategoriesUseCase()
            .onEach { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
            .launchIn(viewModelScope)
    }

    private fun setupFilteringLogic() {
        combine(
            _originalAiList,
            _searchText.debounce(300L),
            _selectedCategory,
            adRepository.adPool
        ) { originalList, searchText, selectedCategory, adPool ->

            val filteredList = originalList.filter { ai ->
                val matchesSearch =
                    searchText.isBlank() || ai.name.contains(searchText, ignoreCase = true)
                val matchesCategory =
                    selectedCategory == null || ai.categories.any { it.id == selectedCategory.id }
                matchesSearch && matchesCategory
            }

            Triple(filteredList, adPool, selectedCategory)
        }
            .flowOn(Dispatchers.Default)
            .onEach { (filteredList, adPool, selectedCat) ->
                _uiState.update {
                    it.copy(
                        aiList = filteredList,
                        adPool = adPool,
                        selectedCategory = selectedCat,
                        searchText = _searchText.value
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun calculateAdCount(listSize: Int): Int {
        val firstAdIndex = AdConfig.FIRST_AD_INDEX
        val frequency = AdConfig.FREQUENCY

        if (listSize <= firstAdIndex) return 0

        val remainingItems = listSize - 2
        val additionalAds = remainingItems / frequency

        return 1 + additionalAds
    }

    fun onSearchTextChange(newText: String) {
        val wasSearching = _searchText.value.isNotEmpty()

        _searchText.value = newText

        if (wasSearching && newText.isEmpty()) {
            val count = calculateAdCount(_originalAiList.value.size)
            adRepository.refreshAds(count)
        }
    }

    fun onCategorySelected(category: Category?) {
        if (_selectedCategory.value == category) return

        _selectedCategory.value = category

        val count = calculateAdCount(_originalAiList.value.size)

        adRepository.refreshAds(count)
    }
}

data class HomeScreenUI(
    val aiList: List<Ai> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val searchText: String = "",
    val adPool: List<NativeAd> = emptyList(),
    val isLoading: Boolean = false
)

