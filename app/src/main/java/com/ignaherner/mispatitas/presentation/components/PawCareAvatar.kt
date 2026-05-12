package com.ignaherner.mispatitas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun PawCareAvatar(
    fotoUri: String?,
    nombre: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) {
    var imagenCargada by remember(fotoUri) { mutableStateOf(fotoUri != null) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (fotoUri != null && imagenCargada) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fotoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { imagenCargada = false }
            )
        } else {
            Text(
                text = if (nombre.isNotBlank())
                    nombre.first().uppercaseChar().toString()
                else
                    "?",
                style = textStyle,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
