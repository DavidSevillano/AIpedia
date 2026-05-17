package com.burixer85.aipedia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MdPrimary,
    onPrimary = MdOnPrimary,
    primaryContainer = MdPrimaryContainer,
    onPrimaryContainer = MdOnPrimaryContainer,
    secondary = MdSecondary,
    tertiary = MdTertiary,
    background = MdBackground,
    onBackground = MdOnSurfaceStrong,
    surface = MdSurface,
    surfaceVariant = MdSurfaceLow,
    surfaceContainerLow = MdSurfaceLow,
    surfaceContainer = MdSurfaceContainer,
    surfaceContainerHigh = MdSurfaceHigh,
    surfaceContainerHighest = MdSurfaceHighest,
    onSurface = MdOnSurface,
    onSurfaceVariant = MdOnSurfaceVariant,
    outline = MdOutline,
    outlineVariant = MdOutlineVariant,
)

private val LightColorScheme = darkColorScheme(
    primary = MdPrimary,
    onPrimary = MdOnPrimary,
    primaryContainer = MdPrimaryContainer,
    onPrimaryContainer = MdOnPrimaryContainer,
    secondary = MdSecondary,
    tertiary = MdTertiary,
    background = MdBackground,
    onBackground = MdOnSurfaceStrong,
    surface = MdSurface,
    surfaceVariant = MdSurfaceLow,
    surfaceContainerLow = MdSurfaceLow,
    surfaceContainer = MdSurfaceContainer,
    surfaceContainerHigh = MdSurfaceHigh,
    surfaceContainerHighest = MdSurfaceHighest,
    onSurface = MdOnSurface,
    onSurfaceVariant = MdOnSurfaceVariant,
    outline = MdOutline,
    outlineVariant = MdOutlineVariant,
)

@Composable
fun AIpediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
