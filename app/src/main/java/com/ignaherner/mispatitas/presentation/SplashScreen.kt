package com.ignaherner.mispatitas.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.R
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PawSpace.lg),
            modifier = Modifier.alpha(alpha)
        ) {
            // Logo — cuadrado redondeado con patita
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(PawRadio.lg))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mispatitas_logo),
                    contentDescription = "MisPatitas logo",
                    modifier = Modifier.size(44.dp)
                )
            }

            // Nombre
            Text(
                text = "MisPatitas",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Subtítulo
            Text(
                text = "La libreta sanitaria de tu mascota",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

