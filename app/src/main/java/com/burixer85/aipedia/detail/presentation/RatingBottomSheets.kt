package com.burixer85.aipedia.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burixer85.aipedia.ui.theme.MdOnSurfaceMuted
import com.burixer85.aipedia.ui.theme.MdSurfaceLow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

private val StarYellow = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    supabase: SupabaseClient,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val signInState = supabase.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                NativeSignInResult.Success -> { onDismiss(); onAuthSuccess() }
                NativeSignInResult.ClosedByUser -> onDismiss()
                is NativeSignInResult.Error -> onDismiss()
                is NativeSignInResult.NetworkError -> onDismiss()
            }
        },
        fallback = {}
    )

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inicia sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Para escribir una reseña necesitas una cuenta",
                fontSize = 13.sp,
                color = MdOnSurfaceMuted
            )
            Spacer(modifier = Modifier.height(24.dp))
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
                    "Continuar con Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Solo para escribir reseñas · Puedes valorar sin cuenta",
                fontSize = 11.sp,
                color = MdOnSurfaceMuted
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewBottomSheet(
    aiName: String,
    initialScore: Int,
    initialBody: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSubmit: (score: Int, body: String?) -> Unit
) {
    var score by remember { mutableIntStateOf(if (initialScore > 0) initialScore else 0) }
    var body by remember { mutableStateOf(initialBody) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(aiName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Tu reseña", fontSize = 12.sp, color = MdOnSurfaceMuted)
            Spacer(modifier = Modifier.height(20.dp))

            Text("PUNTUACIÓN", fontSize = 10.sp, color = MdOnSurfaceMuted, letterSpacing = 1.2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                (1..5).forEach { star ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clickable { score = star }
                    ) {
                        Text(
                            text = "★",
                            fontSize = 34.sp,
                            color = if (star <= score) StarYellow else MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("TU OPINIÓN", fontSize = 10.sp, color = MdOnSurfaceMuted, letterSpacing = 1.2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MdSurfaceLow)
                    .padding(12.dp)
            ) {
                if (body.isEmpty()) {
                    Text("Escribe tu opinión (opcional)...", fontSize = 13.sp, color = MdOnSurfaceMuted)
                }
                BasicTextField(
                    value = body,
                    onValueChange = { if (it.length <= 500) body = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                )
            }
            Text(
                "${body.length} / 500",
                fontSize = 11.sp,
                color = MdOnSurfaceMuted,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (score > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceContainer
                    )
                    .clickable(enabled = score > 0) { onSubmit(score, body.ifBlank { null }) }
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    "Publicar reseña",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (score > 0) MaterialTheme.colorScheme.onPrimary else MdOnSurfaceMuted
                )
            }
        }
    }
}
