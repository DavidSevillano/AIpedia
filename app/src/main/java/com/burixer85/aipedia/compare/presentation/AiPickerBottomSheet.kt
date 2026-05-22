package com.burixer85.aipedia.compare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.presentation.component.AiLogoPlaceholder
import com.burixer85.aipedia.core.presentation.component.AIpediaEmptyState
import com.burixer85.aipedia.core.presentation.component.PricePill
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOutline
import com.burixer85.aipedia.ui.theme.MdPrimary
import com.burixer85.aipedia.ui.theme.MdSurfaceContainer
import com.burixer85.aipedia.ui.theme.MdSurfaceHigh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPickerBottomSheet(
    ais: List<Ai>,
    allAisLoaded: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onSelect: (Ai) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MdSurfaceContainer
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Elige una IA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MdOnSurfaceStrong,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar…", color = MdOnSurfaceMuted) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = MdOnSurfaceMuted)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MdPrimary,
                    unfocusedBorderColor = MdOutline,
                    cursorColor = MdPrimary
                )
            )
            if (allAisLoaded && ais.isEmpty()) {
                AIpediaEmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    items(ais, key = { it.id }) { ai ->
                        AiPickerRow(ai = ai, onClick = { onSelect(ai) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AiPickerRow(ai: Ai, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MdSurfaceHigh)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SubcomposeAsyncImage(
            model = ai.logo,
            contentDescription = ai.name,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp)),
            loading = { AiLogoPlaceholder(name = ai.name, modifier = Modifier.fillMaxSize()) },
            error = { AiLogoPlaceholder(name = ai.name, modifier = Modifier.fillMaxSize()) },
            success = { SubcomposeAsyncImageContent() }
        )
        Text(
            text = ai.name,
            color = MdOnSurfaceStrong,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        PricePill(price = ai.priceModel)
    }
}
