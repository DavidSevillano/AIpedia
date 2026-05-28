package com.burixer85.aipedia.core.presentation.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burixer85.aipedia.R
import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant

@Composable
fun AiPediaCategoryFilter(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            CategoryChipItem(
                label = stringResource(R.string.AiPediaCategoryFilter_Text_All),
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }

        items(categories) { category ->
            CategoryChipItem(
                label = category.localizedName(),
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryChipPlaceholder(width: Dp = 72.dp) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.13f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmer-alpha"
    )
    Box(
        modifier = Modifier
            .width(width)
            .height(36.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(MdOnSurfaceVariant.copy(alpha = alpha))
    )
}

@Composable
fun CategoryChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val chipShape = RoundedCornerShape(999.dp)
    val bg = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(36.dp)
            .clip(chipShape)
            .background(bg, chipShape)
            .then(
                if (!selected) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, chipShape)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.067).sp,
            maxLines = 1
        )
    }
}
