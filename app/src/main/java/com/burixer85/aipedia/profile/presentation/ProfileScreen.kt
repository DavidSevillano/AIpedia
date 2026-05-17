package com.burixer85.aipedia.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burixer85.aipedia.ui.theme.CatEducacionA
import com.burixer85.aipedia.ui.theme.MdBackground
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdOnSurfaceStrong
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdPrimary
import com.burixer85.aipedia.ui.theme.MdSurfaceContainer
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@Composable
fun ProfileScreen(
    supabase: SupabaseClient,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.signOutEvent.collect { onSignOut() }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MdOnSurfaceStrong
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres cerrar sesión?",
                    fontSize = 14.sp,
                    color = MdOnSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    viewModel.signOut()
                }) {
                    Text(
                        text = "Cerrar sesión",
                        color = Color(0xFFFF7B7B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(text = "Cancelar", color = MdOnSurfaceVariant)
                }
            },
            containerColor = MdSurfaceContainer
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MdBackground)
    ) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> ProfileLoadingContent()
            is ProfileUiState.NotLoggedIn -> ProfileNotLoggedInContent(supabase = supabase)
            is ProfileUiState.Success -> ProfileSuccessContent(
                state = state,
                onSignOutClick = { showConfirmDialog = true }
            )
            is ProfileUiState.Error -> ProfileErrorContent(state.message)
        }
    }
}

@Composable
private fun ProfileLoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Cargando perfil...", color = MdOnSurfaceVariant, fontSize = 15.sp)
    }
}

@Composable
private fun ProfileNotLoggedInContent(supabase: SupabaseClient) {
    val signInState = supabase.composeAuth.rememberSignInWithGoogle(
        onResult = { },
        fallback = {}
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                tint = MdOnSurfaceMuted,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "No has iniciado sesión",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MdOnSurfaceStrong
            )
            Text(
                text = "Inicia sesión para ver tu perfil, escribir reseñas y guardar favoritos.",
                fontSize = 14.sp,
                color = MdOnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { signInState.startFlow() }
                    .padding(14.dp)
            ) {
                Text(
                    text = "Continuar con Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
            }
            Text(
                text = "Solo para reseñas · Puedes valorar sin cuenta",
                fontSize = 11.sp,
                color = MdOnSurfaceMuted
            )
        }
    }
}

@Composable
private fun ProfileErrorContent(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MdOnSurfaceVariant,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun ProfileSuccessContent(
    state: ProfileUiState.Success,
    onSignOutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { ProfileHeader(state.displayName, state.email) }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            ProfileStatsRow(
                reviewCount = state.reviewCount,
                averageRating = state.averageRating,
                favoriteCount = state.favoriteCount,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            ProfileSettingsSection(modifier = Modifier.padding(horizontal = 16.dp))
        }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            Button(
                onClick = onSignOutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x1AFF6464),
                    contentColor = Color(0xFFFF7B7B)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(displayName: String, email: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(colors = listOf(Color(0xFF1C2550), Color(0xFF1C2026)))
            )
            .statusBarsPadding()
            .padding(top = 24.dp, bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(CatEducacionA, MdPrimary))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.toInitials(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2E61)
                )
            }
            Text(
                text = displayName,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MdOnSurfaceStrong
            )
            Text(text = email, fontSize = 12.sp, color = MdOnSurfaceVariant)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MdPrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "Cuenta Google",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MdPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsRow(
    reviewCount: Int,
    averageRating: Float?,
    favoriteCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(value = reviewCount.toString(), label = "Resenas", modifier = Modifier.weight(1f))
        StatCard(
            value = averageRating?.let { "%.1f".format(it) } ?: "--",
            label = "Rating medio",
            modifier = Modifier.weight(1f)
        )
        StatCard(value = favoriteCount.toString(), label = "Favoritos", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MdSurfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MdPrimary)
            Text(text = label, fontSize = 10.sp, color = MdOnSurfaceMuted)
        }
    }
}

@Composable
private fun ProfileSettingsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SettingsItem(label = "Idioma", value = "Espanol")
        SettingsItem(label = "Notificaciones", value = "Activadas")
        SettingsItem(label = "Version", value = "1.0.0")
    }
}

@Composable
private fun SettingsItem(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MdSurfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = label, fontSize = 13.sp, color = MdOnSurfaceStrong, modifier = Modifier.weight(1f))
            Text(text = value, fontSize = 13.sp, color = MdOnSurfaceVariant)
        }
    }
}

private fun String.toInitials(): String =
    split(" ").filter { it.isNotEmpty() }.take(2).map { it.first().uppercaseChar() }.joinToString("")
