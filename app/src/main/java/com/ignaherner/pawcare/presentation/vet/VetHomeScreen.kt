package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.settings.SettingsViewModel
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.DangerSoft
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetHomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToVetProfile: () -> Unit = {},
    onNavigateToQRScanner: () -> Unit = {},
    onNavigateToPetDetail: (String) -> Unit,
    scannedCode: String? = null,
    viewModel: VetViewModel = hiltViewModel(),
    vetProfileViewModel: VetProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()

) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val vetState by vetProfileViewModel.vetState.collectAsStateWithLifecycle()
    val recentSearches by settingsViewModel.recentSearches.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(Unit) {
        vetProfileViewModel.loadVeterinario()
    }

    LaunchedEffect(searchState) {
        if (searchState is VetSearchState.Success) {
            val pet = (searchState as VetSearchState.Success).pet
            onNavigateToPetDetail(pet.firestoreId)
            viewModel.resetSearch()
        }
    }

    LaunchedEffect(scannedCode) {
        scannedCode?.let { code ->
            val codigoLimpio = code.lines()
                .find { it.contains("Codigo:", ignoreCase = true) || it.contains("Código:", ignoreCase = true) }
                ?.substringAfter(":")
                ?.trim()
                ?: code.trim()

            android.util.Log.d("QRDebug", "Código limpio: $codigoLimpio")

            if (codigoLimpio.isNotBlank()) {
                viewModel.buscarMascota(codigoLimpio)
            }
        }
    }

    val nombreVet = when (val state = vetState) {
        is VetState.Success -> state.vet.nombre
        else -> "Veterinario"
    }

    // Estado del código — 7 chars máximo (3 letras + 4 números)
    var searchRaw by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Badge MODO VETERINARIO
                    Surface(
                        shape = RoundedCornerShape(PawRadio.xl),
                        color = VetPrimarySoft
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.sm),
                            horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PawCareIcon(
                                icon = Icons.Outlined.LocalHospital,
                                contentDescription = null,
                                size = PawIconSize.small,
                                tint = VetPrimaryInk
                            )
                            Text(
                                text = "MODO VETERINARIO",
                                style = MaterialTheme.typography.labelSmall,
                                color = VetPrimaryInk
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        PawCareIcon(
                            icon = Icons.Outlined.Settings,
                            contentDescription = "Configuración",
                            size = PawIconSize.medium
                        )
                    }
                    IconButton(onClick = onNavigateToVetProfile) {
                        PawCareIcon(
                            icon = Icons.Outlined.PersonOutline,
                            contentDescription = "Perfil",
                            size = PawIconSize.medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PawSpace.xl)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
        ) {
            Spacer(modifier = Modifier.height(PawSpace.sm))

            // Título
            Text(
                text = "Buscar mascota",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Escaneá el QR o ingresá el código manualmente.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Card Escanear QR
            Surface(
                onClick = onNavigateToQRScanner,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(PawRadio.md),
                color = VetPrimary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PawSpace.lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(PawRadio.sm))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.QrCode2,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Escanear QR",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Apuntá al código del dueño",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    PawCareIcon(
                        icon = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Separador "O INGRESAR CÓDIGO"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = "O INGRESAR CÓDIGO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // Cajas de código individual
            CodeInputRow(
                value = searchRaw,
                onValueChange = { input ->
                    val limpio = input
                        .filter { it.isLetterOrDigit() }
                        .uppercase()
                        .take(7)
                    searchRaw = limpio
                }
            )

            // Error
            val errorState = searchState as? VetSearchState.Error
            if (errorState != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(PawRadio.md),
                    color = DangerSoft
                ) {
                    Row(
                        modifier = Modifier.padding(PawSpace.md),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Warning,
                            contentDescription = null,
                            size = PawIconSize.default,
                            tint = Danger
                        )
                        Text(
                            text = errorState.mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            color = Danger
                        )
                    }
                }
            }

            // Botón buscar
            Button(
                onClick = {
                    val codigoFormateado = "${searchRaw.take(3)}-${searchRaw.drop(3)}"
                    viewModel.buscarMascota(codigoFormateado)
                },
                enabled = searchRaw.length == 7 && searchState !is VetSearchState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadio.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VetPrimary,
                    contentColor = Color.White
                )
            ) {
                if (searchState is VetSearchState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Buscar",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            // Consultas recientes
            if (recentSearches.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "Consultas recientes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                    recentSearches.take(5).forEach { entry ->
                        val parts = entry.split("|")
                        if (parts.size >= 4) {
                            val nombre = parts[0]
                            val codigo = parts[1]
                            val especie = parts[2]
                            val firestoreId = parts[3]

                            PawCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onNavigateToPetDetail(firestoreId) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(PawSpace.md),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(PawRadio.sm))
                                            .background(VetPrimarySoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        PawCareIcon(
                                            icon = Icons.Outlined.Pets,
                                            contentDescription = null,
                                            size = PawIconSize.default,
                                            tint = VetPrimary
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = nombre,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(PawRadio.xs),
                                                color = VetPrimarySoft
                                            ) {
                                                Text(
                                                    text = codigo,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = VetPrimaryInk,
                                                    fontFamily = FontFamily.Monospace,
                                                    modifier = Modifier.padding(horizontal = PawSpace.sm, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = especie,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    PawCareIcon(
                                        icon = Icons.Outlined.ChevronRight,
                                        contentDescription = null,
                                        size = PawIconSize.medium,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// CODE INPUT — cajas individuales para el código
// ═══════════════════════════════════════════════════════════
@Composable
private fun CodeInputRow(
    value: String,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    // Input invisible que captura el teclado
    Box {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (value.length < 3)
                    KeyboardType.Text
                else
                    KeyboardType.Number,
                capitalization = if (value.length < 3)
                    KeyboardCapitalization.Characters
                else
                    KeyboardCapitalization.None
            )
        )

        // Cajas visibles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusRequester.requestFocus() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3 letras
            for (i in 0..2) {
                CodeBox(
                    char = value.getOrNull(i)?.toString() ?: "",
                    isFocused = value.length == i
                )
                if (i < 2) Spacer(modifier = Modifier.width(PawSpace.sm))
            }

            // Guion
            Text(
                text = "—",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = PawSpace.sm)
            )

            // 4 números
            for (i in 3..6) {
                CodeBox(
                    char = value.getOrNull(i)?.toString() ?: "",
                    isFocused = value.length == i
                )
                if (i < 6) Spacer(modifier = Modifier.width(PawSpace.sm))
            }
        }
    }
}

@Composable
private fun CodeBox(
    char: String,
    isFocused: Boolean
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(PawRadio.sm))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused)
                    VetPrimary
                else if (char.isNotEmpty())
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(PawRadio.sm)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )
    }
}