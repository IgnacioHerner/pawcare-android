package com.ignaherner.pawcare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════
// ROL DEL USUARIO — define qué paleta usar
// ═══════════════════════════════════════════════════════════
enum class UserRole { OWNER, VET }

// ═══════════════════════════════════════════════════════════
// COLOR SCHEME DUEÑO — verde oliva
// ═══════════════════════════════════════════════════════════
private val OwnerLight = lightColorScheme(
    primary = OwnerPrimary,
    onPrimary = OnPrimary,
    primaryContainer = OwnerPrimarySoft,
    onPrimaryContainer = OwnerPrimaryInk,
    secondary = OwnerPrimary,
    secondaryContainer = OwnerPrimarySoft,
    onSecondaryContainer = OwnerPrimaryInk,
    background = BgCream,
    onBackground = InkPrimary,
    surface = Surface,
    onSurface = InkPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = InkMuted,
    outline = LineStrong,
    outlineVariant = Line,
    error = Danger,
    errorContainer = DangerSoft,
    onErrorContainer = InkPrimary,
    tertiary = Info,
    tertiaryContainer = InfoSoft,
)

// ═══════════════════════════════════════════════════════════
// COLOR SCHEME VETERINARIO — azul petróleo
// Comparte todo con OwnerLight, solo cambia el primario
// ═══════════════════════════════════════════════════════════
private val VetLight = OwnerLight.copy(
    primary = VetPrimary,
    onPrimary = OnPrimary,
    primaryContainer = VetPrimarySoft,
    onPrimaryContainer = VetPrimaryInk,
)

// ═══════════════════════════════════════════════════════════
// THEME COMPOSABLE
// Usar: PawCareTheme(role = UserRole.OWNER) { ... }
// ═══════════════════════════════════════════════════════════
@Composable
fun PawCareTheme(
    role: UserRole = UserRole.OWNER,
    content: @Composable () -> Unit
) {
    val colors = if (role == UserRole.VET) VetLight else OwnerLight
    MaterialTheme(
        colorScheme = colors,
        typography = PawCareType,
        content = content
    )
}