package com.burixer85.aipedia.home.presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.AiSummary
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.core.domain.repository.AdRepository
import com.burixer85.aipedia.core.util.AdConfig
import com.burixer85.aipedia.home.domain.usecase.GetAllAisUseCase
import com.burixer85.aipedia.home.domain.usecase.GetAllCategoriesUseCase
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
        getAllAisUseCase()
            .catch { e ->
                Log.e("HomeViewModel", "Error cargando AIs", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudieron cargar los datos. Inténtalo de nuevo."
                    )
                }
            }
            .onEach { ais ->
                _originalAiList.value = ais
                val count = calculateAdCount(ais.size)
                if (count > 0) adRepository.loadAds(count)
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
            val isSpanish = context.resources.configuration.locales[0].language == "es"

            val filteredSummaries = originalList
                .filter { ai ->
                    val matchesSearch =
                        searchText.isBlank() || ai.name.contains(searchText, ignoreCase = true)
                    val matchesCategory =
                        selectedCategory == null || ai.categories.any { it.id == selectedCategory.id }
                    matchesSearch && matchesCategory
                }
                .map { ai -> ai.toSummary(isSpanish) }

            val mergedItems = mergeAdsIntoList(filteredSummaries, adPool)
            Triple(mergedItems, selectedCategory, searchText)
        }
            .flowOn(Dispatchers.Default)
            .onEach { (items, selectedCat, searchTxt) ->
                _uiState.update {
                    it.copy(
                        items = items,
                        selectedCategory = selectedCat,
                        searchText = searchTxt,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun Ai.toSummary(isSpanish: Boolean): AiSummary {
        val cat = categories.firstOrNull()
        return AiSummary(
            id = id,
            name = name,
            priceModel = priceModel,
            logo = logo,
            categoryName = cat?.let { if (isSpanish) it.nameEs else it.nameEn }
        )
    }

    private fun mergeAdsIntoList(
        summaries: List<AiSummary>,
        adPool: List<NativeAd>
    ): List<HomeListItem> {
        if (summaries.isEmpty()) return emptyList()
        val firstAdIndex = AdConfig.FIRST_AD_INDEX
        val frequency = AdConfig.FREQUENCY
        val result = ArrayList<HomeListItem>(summaries.size + adPool.size)
        var adIdx = 0
        summaries.forEachIndexed { index, ai ->
            result.add(HomeListItem.AiCard(ai))
            val isAdSlot = index == firstAdIndex ||
                (index > firstAdIndex && (index - firstAdIndex) % frequency == 0)
            if (isAdSlot && adIdx < adPool.size) {
                result.add(HomeListItem.AdCard(adPool[adIdx++]))
            }
        }
        return result
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
        val filteredSize = _originalAiList.value.filter { ai ->
            category == null || ai.categories.any { it.id == category.id }
        }.size
        val count = calculateAdCount(filteredSize)
        adRepository.refreshAds(count)
    }

    fun onRetry() {
        loadAis()
    }
}

@Stable
data class HomeScreenUI(
    val items: List<HomeListItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val searchText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
