package com.stickerpack.maker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VibrantNoirColorScheme = darkColorScheme(
    primary = PrimaryLightMint,
    onPrimary = OnPrimaryContainerMint,
    primaryContainer = SpringMint,
    onPrimaryContainer = OnPrimaryContainerMint,
    secondary = SecondaryCharcoal,
    onSecondary = OnPrimaryContainerMint,
    secondaryContainer = SecondaryContainerCharcoal,
    onSecondaryContainer = TextOnSurfaceVariant,
    background = SurfaceBackgroundNavy,
    onBackground = TextOnSurface,
    surface = SurfaceBackgroundNavy,
    onSurface = TextOnSurface,
    surfaceVariant = CardSurfaceCharcoal,
    onSurfaceVariant = TextOnSurfaceVariant,
    outline = OutlineGreen,
    outlineVariant = OutlineVariantGreen,
    error = ErrorPink,
    errorContainer = ErrorContainerRed
)

@Composable
fun StickerPackMakerTheme(
    darkTheme: Boolean = true, // Default to Vibrant Noir Dark Theme per Google Stitch (StickerDrop) design spec
    content: @Composable () -> Unit
) {
    val colorScheme = VibrantNoirColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
