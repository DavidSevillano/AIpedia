package com.burixer85.aipedia.compare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.burixer85.aipedia.core.domain.model.Ai
import com.burixer85.aipedia.core.domain.model.AiSpec
import com.burixer85.aipedia.core.domain.model.ComparisonData
import com.burixer85.aipedia.core.domain.model.PricingPlan
import com.burixer85.aipedia.core.domain.model.ProCon
import com.burixer85.aipedia.core.presentation.component.PricePill
import com.burixer85.aipedia.core.util.localizedDescription
import com.burixer85.aipedia.core.util.localizedLabel
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.core.util.localizedText
import com.burixer85.aipedia.core.util.localizedValue
import com.burixer85.aipedia.ratings.domain.model.RatingSummary
import com.burixer85.aipedia.ui.theme.MdBackground
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdOutline
import com.burixer85.aipedia.ui.theme.MdOutlineVariant
import com.burixer85.aipedia.ui.theme.MdPrimary
import com.burixer85.aipedia.ui.theme.MdPrimaryContainer
import com.burixer85.aipedia.ui.theme.MdSurfaceContainer

@Composable
fun CompareScreen(
    viewModel: CompareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MdBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Comparar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MdOnSurfaceStrong,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AiSlotCard(
                ai = uiState.aiA,
                onTap = { viewModel.openPicker(PickerTarget.A) },
                onClear = { viewModel.clearSlot(PickerTarget.A) },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "VS",
                color = MdPrimaryContainer,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
            AiSlotCard(
                ai = uiState.aiB,
                onTap = { viewModel.openPicker(PickerTarget.B) },
                onClear = { viewModel.clearSlot(PickerTarget.B) },
                modifier = Modifier.weight(1f)
            )
        }

        val aiA = uiState.aiA
        val aiB = uiState.aiB
        if (aiA != null && aiB != null) {
            Spacer(Modifier.height(16.dp))
            ComparisonTable(
                aiA = aiA,
                aiB = aiB,
                summaryA = uiState.summaryA,
                summaryB = uiState.summaryB,
                comparisonA = uiState.comparisonA,
                comparisonB = uiState.comparisonB,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    if (uiState.pickerTarget != null) {
        AiPickerBottomSheet(
            ais = uiState.filteredAis,
            query = uiState.pickerQuery,
            onQueryChange = viewModel::onQueryChange,
            onSelect = viewModel::selectAi,
            onDismiss = viewModel::closePicker
        )
    }
}

// ── Slot card ────────────────────────────────────────────────────────────────

@Composable
private fun AiSlotCard(
    ai: Ai?,
    onTap: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MdSurfaceContainer)
            .border(
                width = if (ai == null) 1.5.dp else 0.dp,
                color = if (ai == null) MdOutline else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onTap)
            .padding(horizontal = 10.dp)
    ) {
        if (ai == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+", color = MdPrimary, fontSize = 20.sp, fontWeight = FontWeight.Light)
                Text("Elegir IA", color = MdPrimary, fontSize = 11.sp)
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ai.logo,
                    contentDescription = ai.name,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(7.dp))
                )
                Text(
                    text = ai.name,
                    color = MdOnSurfaceStrong,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Quitar",
                        tint = MdOnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ── Comparison table ─────────────────────────────────────────────────────────

private fun formatStartingPrice(ai: Ai): String {
    val price = ai.startingPrice
    return when {
        price != null && price % 1.0 == 0.0 -> "${price.toInt()} €/mes"
        price != null -> "%.2f €/mes".format(price)
        ai.hasFreeTier -> "Gratis"
        else -> "–"
    }
}

@Composable
private fun ComparisonTable(
    aiA: Ai,
    aiB: Ai,
    summaryA: RatingSummary?,
    summaryB: RatingSummary?,
    comparisonA: ComparisonData?,
    comparisonB: ComparisonData?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MdSurfaceContainer)
    ) {
        CompareRow(label = "Descripción") {
            Text(
                text = aiA.localizedDescription(),
                color = MdOnSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = aiB.localizedDescription(),
                color = MdOnSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        RowDivider()
        CompareRow(label = "Precio") {
            Box(modifier = Modifier.weight(1f)) { PricePill(price = aiA.priceModel) }
            Box(modifier = Modifier.weight(1f)) { PricePill(price = aiB.priceModel) }
        }
        RowDivider()
        CompareRow(label = "Valoración") {
            RatingCell(summary = summaryA, modifier = Modifier.weight(1f))
            RatingCell(summary = summaryB, modifier = Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "Plataformas") {
            ChipCell(items = aiA.platforms.map { it.name }, modifier = Modifier.weight(1f))
            ChipCell(items = aiB.platforms.map { it.name }, modifier = Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "Categorías") {
            ChipCell(
                items = aiA.categories.map { it.localizedName() },
                modifier = Modifier.weight(1f)
            )
            ChipCell(
                items = aiB.categories.map { it.localizedName() },
                modifier = Modifier.weight(1f)
            )
        }
        RowDivider()
        CompareRow(label = "Características") {
            val allFeatures = (aiA.features + aiB.features).distinctBy { it.id }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                allFeatures.forEach { feature ->
                    FeatureCheckRow(
                        name = feature.localizedName(),
                        has = aiA.features.any { it.id == feature.id }
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                allFeatures.forEach { feature ->
                    FeatureCheckRow(
                        name = feature.localizedName(),
                        has = aiB.features.any { it.id == feature.id }
                    )
                }
            }
        }
        RowDivider()
        CompareRow(label = "Empresa") {
            TextCell(aiA.company, Modifier.weight(1f))
            TextCell(aiB.company, Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "Año") {
            TextCell(aiA.releaseYear?.toString(), Modifier.weight(1f))
            TextCell(aiB.releaseYear?.toString(), Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "Precio inicial") {
            TextCell(formatStartingPrice(aiA), Modifier.weight(1f))
            TextCell(formatStartingPrice(aiB), Modifier.weight(1f))
        }
        RowDivider()
        CompareRow(label = "API disponible") {
            BoolCell(aiA.hasApi, Modifier.weight(1f))
            BoolCell(aiB.hasApi, Modifier.weight(1f))
        }
        if (comparisonA != null || comparisonB != null) {
            RowDivider()
            CompareRow(label = "Ficha técnica") {
                SpecCell(comparisonA?.specs.orEmpty(), Modifier.weight(1f))
                SpecCell(comparisonB?.specs.orEmpty(), Modifier.weight(1f))
            }
            RowDivider()
            CompareRow(label = "Planes") {
                PlanCell(comparisonA?.plans.orEmpty(), Modifier.weight(1f))
                PlanCell(comparisonB?.plans.orEmpty(), Modifier.weight(1f))
            }
            RowDivider()
            CompareRow(label = "Ventajas") {
                ProConListCell(comparisonA?.pros.orEmpty(), true, Modifier.weight(1f))
                ProConListCell(comparisonB?.pros.orEmpty(), true, Modifier.weight(1f))
            }
            RowDivider()
            CompareRow(label = "Desventajas") {
                ProConListCell(comparisonA?.cons.orEmpty(), false, Modifier.weight(1f))
                ProConListCell(comparisonB?.cons.orEmpty(), false, Modifier.weight(1f))
            }
            RowDivider()
            CompareRow(label = "Integraciones") {
                ChipCell(comparisonA?.integrations.orEmpty().map { it.name }, Modifier.weight(1f))
                ChipCell(comparisonB?.integrations.orEmpty().map { it.name }, Modifier.weight(1f))
            }
            RowDivider()
            CompareRow(label = "Casos de uso") {
                ChipCell(
                    comparisonA?.useCases.orEmpty().map { it.localizedName() },
                    Modifier.weight(1f)
                )
                ChipCell(
                    comparisonB?.useCases.orEmpty().map { it.localizedName() },
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TextCell(text: String?, modifier: Modifier = Modifier) {
    Text(
        text = text?.takeIf { it.isNotBlank() } ?: "–",
        color = if (text.isNullOrBlank()) MdOnSurfaceMuted else MdOnSurfaceStrong,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
private fun BoolCell(value: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (value) "✔" else "✘",
        color = if (value) Color(0xFF34D399) else Color(0xFFF87171),
        fontSize = 13.sp,
        modifier = modifier
    )
}

@Composable
private fun SpecCell(specs: List<AiSpec>, modifier: Modifier = Modifier) {
    if (specs.isEmpty()) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 11.sp, modifier = modifier)
        return
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        specs.forEach { spec ->
            Column {
                Text(spec.localizedLabel(), color = MdOnSurfaceMuted, fontSize = 9.sp)
                Text(
                    text = spec.localizedValue(),
                    color = MdOnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PlanCell(plans: List<PricingPlan>, modifier: Modifier = Modifier) {
    if (plans.isEmpty()) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 11.sp, modifier = modifier)
        return
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        plans.forEach { plan ->
            val price = plan.priceMonthly
            val priceStr = when {
                price <= 0.0 -> "Gratis"
                price % 1.0 == 0.0 -> "${price.toInt()} €"
                else -> "%.2f €".format(price)
            }
            Text(
                text = "${plan.localizedName()} · $priceStr",
                color = MdOnSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProConListCell(items: List<ProCon>, isPro: Boolean, modifier: Modifier = Modifier) {
    if (items.isEmpty()) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 11.sp, modifier = modifier)
        return
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isPro) "✔" else "✘",
                    color = if (isPro) Color(0xFF34D399) else Color(0xFFF87171),
                    fontSize = 9.sp
                )
                Text(
                    text = item.localizedText(),
                    color = MdOnSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun CompareRow(label: String, content: @Composable RowScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
        Text(
            text = label.uppercase(),
            color = MdOnSurfaceMuted,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
private fun RowDivider() =
    HorizontalDivider(color = MdOutlineVariant, thickness = 1.dp)

@Composable
private fun RatingCell(summary: RatingSummary?, modifier: Modifier = Modifier) {
    if (summary == null) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 12.sp, modifier = modifier)
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("★", color = Color(0xFFF59E0B), fontSize = 13.sp)
            Text(
                text = "%.1f".format(summary.average),
                color = MdOnSurfaceStrong,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = "(${summary.totalCount})",
                color = MdOnSurfaceMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ChipCell(items: List<String>, modifier: Modifier = Modifier) {
    if (items.isEmpty()) {
        Text("–", color = MdOnSurfaceMuted, fontSize = 11.sp, modifier = modifier)
    } else {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            items.forEach { label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MdOutlineVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(label, color = MdOnSurfaceVariant, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun FeatureCheckRow(name: String, has: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (has) "✔" else "✘",
            color = if (has) Color(0xFF34D399) else Color(0xFFF87171),
            fontSize = 10.sp
        )
        Text(
            text = name,
            color = if (has) MdOnSurfaceVariant else MdOnSurfaceMuted,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
