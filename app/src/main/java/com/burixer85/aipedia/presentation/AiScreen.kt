package com.burixer85.aipedia.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AiScreen(aiId: String?) {
    Text(modifier = Modifier.padding(16.dp), text = "Estás viendo la IA con ID: $aiId")
}