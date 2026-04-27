package com.ignaherner.pawcare.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.worker.WorkManagerHelper
import com.ignaherner.pawcare.data.remote.firestore.AppointmentFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.ConditionFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.DewormingFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.MedicationFirestoreRepository
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.data.remote.firestore.PetFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.VaccineFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.WeightFirestoreRepository
import com.ignaherner.pawcare.data.repository.AppointmentRepository
import com.ignaherner.pawcare.data.repository.ConditionRepository
import com.ignaherner.pawcare.data.repository.DewormingRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.SyncRepository
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.PetSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val petFirestoreRepository: PetFirestoreRepository,
    private val syncRepository: SyncRepository,
    private val workManagerHelper: WorkManagerHelper,
    private val vaccineRepository: VaccineRepository,
    private val medicationRepository: MedicationRepository,
    private val weightRepository: WeightRepository,

) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            syncRepository.sincronizarTodo()
            loadHome()
        }
    }


    fun loadHome() {
        viewModelScope.launch {
            try {
                petRepository.getAllPets()
                    .collect { pets ->
                        if(pets.isEmpty()) {
                            _uiState.value = HomeUiState.Empty
                            return@collect
                        }
                        val summaries = pets.map { pet ->
                            val vacunas = vaccineRepository
                                .getVaccinesByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            val medicamentos = medicationRepository
                                .getMedicationByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            val pesos = weightRepository
                                .getWeightByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            PetSummary(
                                pet = pet,
                                proximaVacuna = vacunas
                                    .filter { it.proximaDosis != null }
                                    .minByOrNull { it.proximaDosis!! },
                                medicamentoActivo = medicamentos
                                    .firstOrNull { it.status == MedicationStatus.ACTIVO },
                                ultimoPeso = pesos.firstOrNull(),
                                totalVacunas = vacunas.size,
                                totalMedicamentos = medicamentos.size
                            )
                        }
                        _uiState.value = HomeUiState.Success(summaries)
                    }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deletePet(pet: Pet){
        viewModelScope.launch {
            try {
                workManagerHelper.cancelarTodosLosRecordatoriosDeMascota(pet.id)
                petRepository.deletePet(pet)
                if (pet.firestoreId.isNotBlank()){
                    petFirestoreRepository.eliminarPet(pet.firestoreId)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

    fun calcularAlertas(summaries: List<PetSummary>): List<HomeAlert> {
        val alertas = mutableListOf<HomeAlert>()
        val hoy = LocalDate.now()

        summaries.forEach { summary ->
            // Vacunas vencidas o proximas (en los proximos 7 dias)
            summary.proximaVacuna?.let { vacuna ->
                val proximaDosis = vacuna.proximaDosis ?: return@let
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaProxima = LocalDate.parse(proximaDosis, formatter)
                val diasRestantes = java.time.temporal.ChronoUnit.DAYS
                    .between(hoy, fechaProxima)

                when{
                    diasRestantes < 0 -> {
                        // Vacuna vencida
                        alertas.add(
                            HomeAlert(
                                tipo = AlertType.VACUNA_VENCIDA,
                                petName = summary.pet.nombre,
                                titulo = "Vacuna vencida",
                                descripcion = "${summary.pet.nombre} tiene la vacuna ${vacuna.tipo.displayName} vencida"
                            )
                        )
                    }
                    diasRestantes <= 7 -> {
                        // Vacuna proxima
                        alertas.add(
                            HomeAlert(
                                tipo = AlertType.VACUNA_PROXIMA,
                                petName = summary.pet.nombre,
                                titulo = "Vacuna próxima",
                                descripcion = "${summary.pet.nombre} necesita ${vacuna.tipo.displayName} en $diasRestantes días"
                            )
                        )
                    }
                }
            }
            // Medicamento activo
            summary.medicamentoActivo?.let { medicamento ->
                alertas.add(
                    HomeAlert(
                        tipo = AlertType.MEDICAMENTO_ACTIVO,
                        petName = summary.pet.nombre,
                        titulo = "Tratamiento en curso",
                        descripcion = "${summary.pet.nombre} está tomando ${medicamento.nombre}"
                    )
                )
            }
        }
        return alertas.take(2)
    }

    // Calcula el estado de salud general de una mascota
    fun calcularEstadoMascota(summary: PetSummary): EstadoMascota {
        val hoy = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // ¿Vacuna vencida?
        summary.proximaVacuna?.proximaDosis?.let { proximaDosis ->
            val fechaProxima = LocalDate.parse(proximaDosis, formatter)
            val diasRestantes = ChronoUnit.DAYS.between(hoy, fechaProxima)

            return when {
                diasRestantes < 0 -> EstadoMascota.URGENTE
                diasRestantes <= 14 -> EstadoMascota.ATENCION
                else -> EstadoMascota.OK
            }
        }

        return EstadoMascota.OK
    }
}

sealed class HomeUiState {
    object Loading: HomeUiState()
    object Empty: HomeUiState()
    data class Success(val summaries: List<PetSummary>) : HomeUiState()
    data class Error(val mensaje: String) : HomeUiState()
}

data class HomeAlert(
    val tipo: AlertType,
    val petName: String,
    val titulo: String,
    val descripcion: String
)

enum class AlertType{VACUNA_VENCIDA, VACUNA_PROXIMA, MEDICAMENTO_ACTIVO}

enum class EstadoMascota { OK, ATENCION, URGENTE }
