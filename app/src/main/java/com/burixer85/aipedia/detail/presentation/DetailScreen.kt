package com.burixer85.aipedia.detail.presentation

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.burixer85.aipedia.R
import com.burixer85.aipedia.core.domain.model.Feature
import com.burixer85.aipedia.core.presentation.component.AiLogoPlaceholder
import com.burixer85.aipedia.core.presentation.component.FeatureDetailModal
import com.burixer85.aipedia.core.presentation.component.PricePill
import com.burixer85.aipedia.core.util.localizedDescription
import com.burixer85.aipedia.core.util.localizedName
import com.burixer85.aipedia.core.util.localizedTagline
import com.burixer85.aipedia.ui.theme.MdBackground
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdOutline
import com.burixer85.aipedia.ui.theme.MdOutlineVariant
import com.burixer85.aipedia.ui.theme.MdSurfaceLow
import io.github.jan.supabase.SupabaseClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    aiId: String?,
    onBack: () -> Unit,
    supabase: SupabaseClient,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var selectedFeature by remember { mutableStateOf<Feature?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    val authSheetState = rememberModalBottomSheetState()
    val reviewSheetState = rememberModalBottomSheetState()
    var showAuthSheet by remember { mutableStateOf(false) }
    var showWriteReviewSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val deviceId = remember {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    LaunchedEffect(aiId) {
        aiId?.let { viewModel.loadAiDetails(it, deviceId) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MdBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top app bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.IosShare,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Scrollable content
            uiState.ai?.let { ai ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 120.dp)
                ) {
                    // Hero area
                    Column(modifier = Modifier.padding(horizontal = 22.dp)) {
                        SubcomposeAsyncImage(
                            model = ai.logo,
                            contentDescription = ai.name,
                            modifier = Modifier
                                .size(92.dp)
                                .clip(RoundedCornerShape(26.dp)),
                            contentScale = ContentScale.Crop,
                            loading = { AiLogoPlaceholder(name = ai.name, modifier = Modifier.fillMaxSize()) },
                            error = { AiLogoPlaceholder(name = ai.name, modifier = Modifier.fillMaxSize()) }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ai.name,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MdOnSurfaceStrong,
                                    letterSpacing = (-0.8).sp,
                                    lineHeight = 34.sp
                                )
                                ai.localizedTagline()?.takeIf { it.isNotBlank() }?.let { tagline ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = tagline,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MdOnSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                                if (ai.categories?.isNotEmpty() == true) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = ai.categories.firstOrNull()?.localizedName() ?: "",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MdOnSurfaceVariant,
                                        letterSpacing = (-0.07).sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            PricePill(price = ai.priceModel)
                        }

                        // Stats row: category chip + other badges
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ai.categories?.firstOrNull()?.let { cat ->
                                StatChip(text = cat.localizedName())
                            }
                        }
                    }

                    // Description section
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionLabel(
                        text = "Acerca de",
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ai.localizedDescription(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )

                    // Comparison data sections
                    uiState.comparison?.let { comparison ->
                        SpecsSection(specs = comparison.specs)
                        PricingSection(plans = comparison.plans)
                        ProsConsSection(pros = comparison.pros, cons = comparison.cons)
                    }

                    // Features section
                    if (ai.features.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(28.dp))
                        SectionLabel(
                            text = stringResource(R.string.DetailScreen_Text_Title_Features),
                            modifier = Modifier.padding(horizontal = 22.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier.padding(horizontal = 22.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ai.features.forEach { feature ->
                                FeatureCard(
                                    feature = feature,
                                    onClick = {
                                        selectedFeature = feature
                                        showSheet = true
                                    }
                                )
                            }
                        }
                    }

                    // Platforms section
                    if (ai.platforms.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(28.dp))
                        SectionLabel(
                            text = stringResource(R.string.DetailScreen_Text_Title_Platforms),
                            modifier = Modifier.padding(horizontal = 22.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 22.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(ai.platforms) { platform ->
                                PlatformChip(name = platform.name, icon = platform.icon)
                            }
                        }
                    }

                    // Integrations & use cases sections
                    uiState.comparison?.let { comparison ->
                        IntegrationsSection(integrations = comparison.integrations)
                        UseCasesSection(useCases = comparison.useCases)
                    }

                    // Valoración section
                    Spacer(modifier = Modifier.height(28.dp))
                    SectionLabel(
                        text = "Valoración",
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.ratingSummary?.let { summary ->
                            RatingSummaryCard(summary = summary)
                        }
                        AnonymousStarRow(
                            currentScore = uiState.reviews
                                .firstOrNull { it.userId == uiState.currentUser?.id }
                                ?.score ?: uiState.userRating
                        )
                    }

                    // Reseñas section
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionLabel(text = "Reseñas")
                        Text(
                            text = "+ Escribir",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                if (uiState.currentUser != null) showWriteReviewSheet = true
                                else showAuthSheet = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.reviews.take(5).forEach { review ->
                            ReviewCard(review = review)
                        }
                        if (uiState.currentUser == null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MdSurfaceLow)
                                    .clickable { showAuthSheet = true }
                                    .padding(vertical = 16.dp)
                            ) {
                                Text(
                                    text = "Inicia sesión para escribir una reseña",
                                    fontSize = 13.sp,
                                    color = MdOnSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // Sticky gradient CTA
        uiState.ai?.let { ai ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MdBackground, MdBackground),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 14.dp)
                    .padding(top = 28.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, ai.website.toUri())
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.DetailScreen_Text_Website),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            letterSpacing = (-0.08).sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    FeatureDetailModal(
        selectedFeature = selectedFeature,
        showSheet = showSheet,
        onDismissRequest = { showSheet = false },
        sheetState = sheetState,
    )

    if (showAuthSheet) {
        AuthBottomSheet(
            supabase = supabase,
            sheetState = authSheetState,
            onDismiss = { showAuthSheet = false },
            onAuthSuccess = {
                showWriteReviewSheet = true
                scope.launch { snackbarHostState.showSnackbar("Sesión iniciada con Google") }
            }
        )
    }

    if (showWriteReviewSheet) {
        uiState.ai?.let { ai ->
            val existingReview = uiState.reviews.firstOrNull { it.userId == uiState.currentUser?.id }
            WriteReviewBottomSheet(
                aiName = ai.name,
                initialScore = existingReview?.score ?: 0,
                initialBody = existingReview?.body ?: "",
                sheetState = reviewSheetState,
                onDismiss = { showWriteReviewSheet = false },
                onSubmit = { score, body ->
                    viewModel.submitReview(ai.id, score, body)
                    showWriteReviewSheet = false
                }
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MdOnSurfaceMuted,
        letterSpacing = 0.18.sp,
        modifier = modifier
    )
}

@Composable
private fun StatChip(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(30.dp)
            .border(1.dp, MdOutline, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.063).sp
        )
    }
}

@Composable
private fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MdSurfaceLow)
            .clickable(onClick = onClick)
            .padding(14.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            AsyncImage(
                model = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feature.localizedName(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MdOnSurfaceStrong,
                letterSpacing = (-0.15).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = feature.localizedDescription(),
                style = MaterialTheme.typography.bodySmall,
                color = MdOnSurfaceVariant,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MdOnSurfaceMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun PlatformChip(name: String, icon: String) {
    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MdSurfaceLow)
            .border(1.dp, MdOutlineVariant, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = icon,
            contentDescription = name,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.07).sp
        )
    }
}
