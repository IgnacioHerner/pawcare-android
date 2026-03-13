package com.ignaherner.pawcare.presentation.appointments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.presentation.components.AppointmentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadAppointments(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Turnos ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, contentDescription = "Agregar turno")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AppointmentUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AppointmentUiState.Empty -> {
                    Text(
                        text = "Agrega un turno para tus mascotas",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AppointmentUiState.Success -> {
                    LazyColumn {
                        items(
                            items = state.appointments,
                            key = { it.id }
                        ) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onClick = {},
                                onDeleteClick = { viewModel.deleteAppointment(appointment)}
                            )
                        }
                    }
                }
                is AppointmentUiState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}