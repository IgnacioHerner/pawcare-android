package com.ignaherner.mispatitas

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.ignaherner.mispatitas.presentation.PawCareNavGraph
import com.ignaherner.mispatitas.ui.theme.PawCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // No necesitamos hacer nada especial con el resultado
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // Pedir permisos en runtime
        val permisosNecesarios = mutableListOf<String>()

        // Notificaciones — Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisosNecesarios.add(android.Manifest.permission.POST_NOTIFICATIONS)
            permisosNecesarios.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (permisosNecesarios.isNotEmpty()) {
            requestPermissionLauncher.launch(permisosNecesarios.toTypedArray())
        }

        setContent {
            PawCareTheme {
                PawCareNavGraph()
            }
        }
    }
}
