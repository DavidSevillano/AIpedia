package com.burixer85.aipedia.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burixer85.aipedia.R
import com.burixer85.aipedia.core.presentation.component.AIpediaEmptyState
import com.burixer85.aipedia.core.presentation.component.AIpediaErrorState
import com.burixer85.aipedia.core.presentation.component.AIpediaNativeAdItem
import com.burixer85.aipedia.core.presentation.component.AiPediaCard
import com.burixer85.aipedia.core.presentation.component.AiPediaCardPlaceholder
import com.burixer85.aipedia.core.presentation.component.AiPediaSearchBar
import com.burixer85.aipedia.core.presentation.component.CategoryChipItem
import com.burixer85.aipedia.core.util.AdConfig
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.ui.theme.MdBackground
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant

@Composable
fun HomeScreen(
    onAiClick: (String) -> Unit,
    homeViewmodel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewmodel.uiState.collectAsStateWithLifecycle()
    var localSearchText by remember(uiState.searchText) { mutableStateOf(uiState.searchText) }

    Box(modifier = Modifier.fillMaxSize().background(MdBackground)) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header: eyebrow + title
            item {
                Column(modifier = Modifier.padding(horizontal = 22.dp)) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(8.dp)
                    )
                    Text(
                        text = stringResource(R.string.HomeScreen_Text_Eyebrow).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MdOnSurfaceMuted
                    )
                    Text(
                        text = stringResource(R.string.HomeScreen_Text_Title_App),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MdOnSurfaceStrong,
                        letterSpacing = (-0.85).sp,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    AiPediaSearchBar(
                        value = localSearchText,
                        onValueChange = { newText ->
                            localSearchText = newText
                            homeViewmodel.onSearchTextChange(newText)
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Category filter chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryChipItem(
                            label = stringResource(R.string.AiPediaCategoryFilter_Text_All),
                            selected = uiState.selectedCategory == null,
                            onClick = { homeViewmodel.onCategorySelected(null) }
                        )
                    }
                    items(uiState.categories) { category ->
                        CategoryChipItem(
                            label = category.localizedName(),
                            selected = uiState.selectedCategory == category,
                            onClick = { homeViewmodel.onCategorySelected(category) }
                        )
                    }
                }
            }

            // Section heading
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (uiState.selectedCategory == null)
                                stringResource(R.string.HomeScreen_Text_Section_Popular)
                            else
                                uiState.selectedCategory!!.localizedName(),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MdOnSurfaceStrong,
                            letterSpacing = (-0.285).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.aiList.size} ${if (uiState.aiList.size == 1) "resultado" else "resultados"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MdOnSurfaceVariant
                        )
                    }
                }
            }

            // List states
            when {
                uiState.isLoading -> {
                    items(5) {
                        AiPediaCardPlaceholder(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AIpediaErrorState(onRetry = { homeViewmodel.onRetry() })
                        }
                    }
                }

                uiState.aiList.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AIpediaEmptyState()
                        }
                    }
                }

                else -> {
                    val firstAdIndex = AdConfig.FIRST_AD_INDEX
                    val frequency = AdConfig.FREQUENCY

                    itemsIndexed(
                        items = uiState.aiList,
                        key = { _, ai -> ai.id }
                    ) { index, ai ->
                        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                            AiPediaCard(
                                name = ai.name,
                                category = ai.categories?.firstOrNull()?.localizedName(),
                                price = ai.priceModel,
                                logo = ai.logo,
                                onClick = { onAiClick(ai.id) }
                            )

                            val isFirstAd = index == firstAdIndex
                            val isLaterAd = index > firstAdIndex &&
                                (index - firstAdIndex) % frequency == 0

                            if (isFirstAd || isLaterAd) {
                                val adIdx = if (isFirstAd) 0
                                else (index - firstAdIndex) / frequency
                                if (adIdx < uiState.adPool.size) {
                                    AIpediaNativeAdItem(
                                        nativeAd = uiState.adPool[adIdx],
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

    }
}
