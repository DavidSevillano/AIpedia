package com.burixer85.aipedia.core.presentation.component


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.burixer85.aipedia.core.domain.model.Feature
import com.burixer85.aipedia.core.domain.util.localizedDescription
import com.burixer85.aipedia.core.domain.util.localizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureDetailModal(
    selectedFeature: Feature?,
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
) {
    if (showSheet && selectedFeature != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = selectedFeature.icon,
                        contentDescription = selectedFeature.localizedName(),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = selectedFeature.localizedName(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = selectedFeature.localizedDescription(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 24.sp
                )
            }
        }
    }
}