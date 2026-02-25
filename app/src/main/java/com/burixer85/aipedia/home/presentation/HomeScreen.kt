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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burixer85.aipedia.R
import com.burixer85.aipedia.core.domain.util.localizedName
import com.burixer85.aipedia.core.presentation.component.AiPediaCard
import com.burixer85.aipedia.core.presentation.component.AiPediaCardPlaceholder
import com.burixer85.aipedia.core.presentation.component.AiPediaSearchBar
import com.burixer85.aipedia.core.presentation.component.NativeAdItem

@Composable
fun HomeScreen(
    onAiClick: (String) -> Unit,
    homeViewmodel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewmodel.uiState.collectAsStateWithLifecycle()

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
                value = uiState.searchText,
                onValueChange = homeViewmodel::onSearchTextChange,
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                if (uiState.isLoading) {
                    items(5) {
                        AiPediaCardPlaceholder()
                    }
                } else {
                    itemsIndexed(items = uiState.aiList, key = { _, ai -> ai.id }) { index, ai ->
                        val firsCategory = ai.categories?.firstOrNull()?.localizedName()

                        AiPediaCard(
                            name = ai.name,
                            category = firsCategory,
                            price = ai.priceModel,
                            logo = ai.logo,
                            onClick = { onAiClick(ai.id) }
                        )

                        if (index == 1) {
                            NativeAdItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
