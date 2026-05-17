package com.burixer85.aipedia.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ratings.domain.model.Review
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdSurfaceLow

private val StarYellow = Color(0xFFF59E0B)

@Composable
fun RatingSummaryCard(summary: RatingSummary, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MdSurfaceLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
            Text(
                text = if (summary.totalCount > 0) "%.1f".format(summary.average) else "–",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MdOnSurfaceStrong
            )
            StarRow(score = summary.average.toInt(), size = 13.dp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${summary.totalCount} votos",
                fontSize = 11.sp,
                color = MdOnSurfaceMuted
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            (5 downTo 1).forEach { star ->
                val count = summary.distribution[star] ?: 0
                val fraction = if (summary.totalCount > 0) count.toFloat() / summary.totalCount else 0f
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "$star", fontSize = 11.sp, color = MdOnSurfaceMuted, modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = StarYellow,
                        trackColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }
        }
    }
}

@Composable
fun AnonymousStarRow(currentScore: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MdSurfaceLow)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Tu valoración", fontSize = 13.sp, color = MdOnSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            (1..5).forEach { star ->
                Text(
                    text = "★",
                    fontSize = 26.sp,
                    color = if (star <= currentScore) StarYellow else MdOnSurfaceMuted.copy(alpha = 0.35f)
                )
            }
        }
    }
}

@Composable
fun ReviewCard(review: Review, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MdSurfaceLow)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = review.displayName.take(2).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = review.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MdOnSurfaceStrong)
                StarRow(score = review.score, size = 12.dp)
            }
            Text(text = formatRelativeDate(review.createdAt), fontSize = 11.sp, color = MdOnSurfaceMuted)
        }
        if (!review.body.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = review.body, fontSize = 13.sp, color = MdOnSurfaceVariant, lineHeight = 20.sp)
        }
    }
}

@Composable
internal fun StarRow(score: Int, size: Dp) {
    Row {
        (1..5).forEach { star ->
            Text(
                text = "★",
                fontSize = size.value.sp,
                color = if (star <= score) StarYellow else MaterialTheme.colorScheme.surfaceContainer
            )
        }
    }
}

private fun formatRelativeDate(isoDate: String): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = sdf.parse(isoDate.substringBefore("+").substringBefore("Z")) ?: return ""
        val diffMs = System.currentTimeMillis() - date.time
        val diffDays = (diffMs / 86_400_000).toInt()
        when {
            diffDays == 0 -> "hoy"
            diffDays == 1 -> "ayer"
            diffDays < 7 -> "hace ${diffDays}d"
            diffDays < 30 -> "hace ${diffDays / 7}sem"
            else -> "hace ${diffDays / 30}mes"
        }
    } catch (_: Exception) { "" }
}
