package com.burixer85.aipedia.core.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import coil.size.Size
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdSurfaceLow
import com.burixer85.aipedia.ui.theme.TagFree
import com.burixer85.aipedia.ui.theme.TagFreeBg
import com.burixer85.aipedia.ui.theme.TagFreemium
import com.burixer85.aipedia.ui.theme.TagFreemiumBg
import com.burixer85.aipedia.ui.theme.TagPaid
import com.burixer85.aipedia.ui.theme.TagPaidBg

private val logoPlaceholderColors = listOf(
    Color(0xFF354479) to Color(0xFFB5C5FF), // blue
    Color(0xFF2D4A3E) to Color(0xFF7ECFB0), // green
    Color(0xFF4A2D4A) to Color(0xFFD4A0D4), // purple
    Color(0xFF4A3A2D) to Color(0xFFD4B07E), // amber
    Color(0xFF2D3D4A) to Color(0xFF7EB8D4), // cyan
    Color(0xFF4A2D35) to Color(0xFFD47E8A), // rose
)

@Composable
fun AiLogoPlaceholder(name: String, modifier: Modifier = Modifier) {
    val index = (name.hashCode() and 0x7FFFFFFF) % logoPlaceholderColors.size
    val (bg, fg) = logoPlaceholderColors[index]
    Box(
        modifier = modifier.background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            color = fg,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AiPediaCard(
    name: String,
    category: String?,
    price: String,
    logo: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MdSurfaceLow),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(logo)
                    .size(Size(168, 168))
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                loading = { Box(modifier = Modifier.fillMaxSize()) },
                error = {
                    AiLogoPlaceholder(name = name, modifier = Modifier.fillMaxSize())
                },
                success = { SubcomposeAsyncImageContent() }
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.25).sp
                )
                if (category != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PricePill(price)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MdOnSurfaceMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun PricePill(price: String) {
    val (fg, bg) = when {
        price.equals("Free", ignoreCase = true) -> TagFree to TagFreeBg
        price.equals("Freemium", ignoreCase = true) -> TagFreemium to TagFreemiumBg
        else -> TagPaid to TagPaidBg
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = price.uppercase(),
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.66.sp
        )
    }
}
