package com.burixer85.aipedia.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burixer85.aipedia.R
import com.burixer85.aipedia.core.presentation.component.AIpediaEmptyState
import com.burixer85.aipedia.core.presentation.component.AIpediaErrorState
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.core.presentation.component.AiPediaCard
import com.burixer85.aipedia.core.presentation.component.AiPediaCardPlaceholder
import com.burixer85.aipedia.core.presentation.component.AiPediaCategoryFilter
import com.burixer85.aipedia.core.presentation.component.AiPediaSearchBar
import com.burixer85.aipedia.core.presentation.component.AIpediaNativeAdItem
import com.burixer85.aipedia.core.util.AdConfig

@Composable
fun HomeScreen(
    onAiClick: (String) -> Unit,
    homeViewmodel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewmodel.uiState.collectAsStateWithLifecycle()

    var localSearchText by remember(uiState.searchText) { mutableStateOf(uiState.searchText) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.HomeScreen_Text_Title_App),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(20.dp))

            AiPediaSearchBar(
                value = localSearchText,
                onValueChange = { newText ->
                    localSearchText = newText
                    homeViewmodel.onSearchTextChange(newText)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AiPediaCategoryFilter(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = homeViewmodel::onCategorySelected
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        items(5) { AiPediaCardPlaceholder() }
                    }

                    uiState.errorMessage != null -> {
                        item {
                            Box(modifier = Modifier.fillParentMaxHeight().fillMaxWidth()) {
                                AIpediaErrorState(
                                    onRetry = { homeViewmodel.onRetry() }
                                )
                            }
                        }
                    }

                    uiState.aiList.isEmpty() -> {
                        item {
                            Box(modifier = Modifier.fillParentMaxHeight().fillMaxWidth()) {
                                AIpediaEmptyState()
                            }
                        }
                    }

                    else -> {
                        val firstAdIndex = AdConfig.FIRST_AD_INDEX
                        val frequency = AdConfig.FREQUENCY

                        itemsIndexed(items = uiState.aiList, key = { _, ai -> ai.id }) { index, ai ->
                            AiPediaCard(
                                name = ai.name,
                                category = ai.categories?.firstOrNull()?.localizedName(),
                                price = ai.priceModel,
                                logo = ai.logo,
                                onClick = { onAiClick(ai.id) }
                            )

                            val isFirstAd = index == firstAdIndex
                            val isLaterAd = (index > firstAdIndex) && ((index - firstAdIndex) % frequency == 0)

                            if (isFirstAd || isLaterAd) {
                                val adPoolIndex = if (isFirstAd) 0 else ((index - firstAdIndex) / frequency)
                                if (adPoolIndex < uiState.adPool.size) {
                                    AIpediaNativeAdItem(
                                        nativeAd = uiState.adPool[adPoolIndex],
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

