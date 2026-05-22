package com.burixer85.aipedia.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.burixer85.aipedia.core.domain.model.AiSpec
import com.burixer85.aipedia.core.domain.model.AiUseCase
import com.burixer85.aipedia.core.domain.model.Integration
import com.burixer85.aipedia.core.domain.model.PricingPlan
import com.burixer85.aipedia.core.domain.model.ProCon
import com.burixer85.aipedia.core.util.localizedBillingNote
import com.burixer85.aipedia.core.util.localizedIncludes
import com.burixer85.aipedia.core.util.localizedLabel
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.core.util.localizedText
import com.burixer85.aipedia.core.util.localizedValue
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdOutlineVariant
import com.burixer85.aipedia.ui.theme.MdSurfaceLow

private val ProGreen = Color(0xFF34D399)
private val ConRed = Color(0xFFF87171)

private fun formatPrice(monthly: Double): String =
    if (monthly <= 0.0) "Gratis"
    else if (monthly % 1.0 == 0.0) "${monthly.toInt()} €/mes"
    else "%.2f €/mes".format(monthly)

@Composable
private fun ComparisonSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MdOnSurfaceMuted,
        letterSpacing = 0.18.sp,
        modifier = Modifier.padding(horizontal = 22.dp)
    )
}

@Composable
fun SpecsSection(specs: List<AiSpec>) {
    if (specs.isEmpty()) return
    Spacer(Modifier.height(28.dp))
    ComparisonSectionLabel("Ficha técnica")
    Spacer(Modifier.height(10.dp))
    Column(
        modifier = Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MdSurfaceLow)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        specs.forEach { spec ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = spec.localizedLabel(),
                    fontSize = 13.sp,
                    color = MdOnSurfaceMuted,
                    modifier = Modifier.width(130.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = spec.localizedValue(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MdOnSurfaceStrong,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PricingSection(plans: List<PricingPlan>) {
    if (plans.isEmpty()) return
    Spacer(Modifier.height(28.dp))
    ComparisonSectionLabel("Precios")
    Spacer(Modifier.height(10.dp))
    Column(
        modifier = Modifier.padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        plans.forEach { plan ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MdSurfaceLow)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.localizedName(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MdOnSurfaceStrong
                    )
                    Text(
                        text = formatPrice(plan.priceMonthly),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                plan.localizedBillingNote()?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(text = it, fontSize = 11.sp, color = MdOnSurfaceMuted)
                }
                plan.localizedIncludes()?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MdOnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProsConsSection(pros: List<ProCon>, cons: List<ProCon>) {
    if (pros.isEmpty() && cons.isEmpty()) return
    Spacer(Modifier.height(28.dp))
    ComparisonSectionLabel("Ventajas y desventajas")
    Spacer(Modifier.height(10.dp))
    Column(
        modifier = Modifier.padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        pros.forEach { ProConRow(it) }
        cons.forEach { ProConRow(it) }
    }
}

@Composable
private fun ProConRow(item: ProCon) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = if (item.isPro) "✔" else "✘",
            color = if (item.isPro) ProGreen else ConRed,
            fontSize = 13.sp
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = item.localizedText(),
            style = MaterialTheme.typography.bodyMedium,
            color = MdOnSurfaceVariant
        )
    }
}

@Composable
fun IntegrationsSection(integrations: List<Integration>) {
    if (integrations.isEmpty()) return
    Spacer(Modifier.height(28.dp))
    ComparisonSectionLabel("Integraciones")
    Spacer(Modifier.height(14.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(integrations) { integration ->
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MdSurfaceLow)
                    .border(1.dp, MdOutlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!integration.iconUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = integration.iconUrl,
                        contentDescription = integration.name,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = integration.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MdOnSurfaceStrong
                )
            }
        }
    }
}

@Composable
fun UseCasesSection(useCases: List<AiUseCase>) {
    if (useCases.isEmpty()) return
    Spacer(Modifier.height(28.dp))
    ComparisonSectionLabel("Casos de uso")
    Spacer(Modifier.height(14.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(useCases) { useCase ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MdSurfaceLow)
                    .border(1.dp, MdOutlineVariant, RoundedCornerShape(999.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = useCase.localizedName(),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = MdOnSurfaceVariant
                )
            }
        }
    }
}
