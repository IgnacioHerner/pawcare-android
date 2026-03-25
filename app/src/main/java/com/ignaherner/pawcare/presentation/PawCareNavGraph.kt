package com.ignaherner.pawcare.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ignaherner.pawcare.presentation.appointments.AppointmentFormScreen
import com.ignaherner.pawcare.presentation.appointments.AppointmentScreen
import com.ignaherner.pawcare.presentation.medications.MedicationFormScreen
import com.ignaherner.pawcare.presentation.medications.MedicationScreen
import com.ignaherner.pawcare.presentation.pets.PetDetailScreen
import com.ignaherner.pawcare.presentation.pets.PetFormScreen
import com.ignaherner.pawcare.presentation.pets.PetListScreen
import com.ignaherner.pawcare.presentation.settings.SettingsScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineFormScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineScreen
import com.ignaherner.pawcare.presentation.weight.WeightFormScreen
import com.ignaherner.pawcare.presentation.weight.WeightScreen
import java.net.URLDecoder
import java.net.URLEncoder

object PawCareDestinations {

    // Pets
    const val PET_LIST = "pet_list"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val PET_FORM = "pet_form?petId={petId}"

    // Vaccines
    const val VACCINE_LIST = "vaccine_list/{petId}"
    const val VACCINE_FORM = "vaccine_form/{petId}?vaccineId={vaccinId}"

    // Appointments
    const val APPOINTMENT_LIST = "appointment_list/{petId}"
    const val APPOINTMENT_FORM = "appointment_form/{petId}?appointmentId={appointmentId}"

    // Medications
    const val MEDICATION_LIST = "medication_list/{petId}/{petName}"
    const val MEDICATION_FORM = "medication_form/{petId}/{petName}?medicationId={medicationId}"

    // Weights
    const val WEIGHT_LIST = "weight_list/{petId}"
    const val WEIGHT_FORM = "weight_form/{petId}?weightId={weightId}"

    // Settings
    const val SETTINGS = "settings"

    // Funciones para construir rutas con argumentos
    fun petDetail(petId: Long) = "pet_detail/$petId"
    fun petForm(petId: Long? = null) = if (petId != null) "pet_form?petId=$petId" else "pet_form"

    // Funciones para vaccines
    fun vaccineList(petId: Long) = "vaccine_list/$petId"
    fun vaccineForm(petId: Long, vaccineId: Long? = null) =
        if (vaccineId != null) "vaccine_form/$petId?vaccineId=$vaccineId" else "vaccine_form/$petId"

    // Funciones para appoinments
    fun appointmentList(petId: Long) = "appointment_list/$petId"
    fun appointmentForm(petId: Long, appointmentId: Long? = null) =
        if (appointmentId != null) "appointment_form/$petId?appointmentId=$appointmentId" else "appointment_form/$petId"

    // Funciones para medications
    fun medicationList(petId: Long, petName: String) = "medication_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"
    fun medicationForm(petId: Long, petName: String, medicationId: Long? = null) =
        if(medicationId != null)
            "medication_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?medicationId=$medicationId"
        else
            "medication_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    // Funciones para weights
    fun weightList(petId: Long) = "weight_list/$petId"
    fun weightForm(petId: Long, weightId: Long? = null) =
        if(weightId != null) "weight_form/$petId?weightId=$weightId" else "weight_form/$petId"
}

@Composable
fun PawCareNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = PawCareDestinations.PET_LIST
    ) {
        // Settings DataStore
        composable(PawCareDestinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack()}
            )
        }

        // Lista de mascotas
        composable(PawCareDestinations.PET_LIST) {
            PetListScreen(
                onNavigateToDetail = { petId ->
                    navController.navigate(PawCareDestinations.petDetail(petId))
                },
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.petForm())
                },
                onNavigateToSettings = {
                    navController.navigate(PawCareDestinations.SETTINGS)
                }
            )
        }

        // Formulario - sirve para crear y editar
        composable(
            route = PawCareDestinations.PET_FORM,
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") // devuelve -1L si no hay id
                ?.takeIf { it != -1L } // Si es -1L -> devuelve null (es creacion nueva)
                                      // Si es 5L -> devuelve 5L (es edicion
            PetFormScreen(
                petId = petId,
                onNavigateBack = { navController.popBackStack()}
            )
        }

        // PET DETAIL
        composable(
            route = PawCareDestinations.PET_DETAIL,
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            PetDetailScreen(
                petId = petId,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToEdit = { id ->
                    navController.navigate(PawCareDestinations.petForm(id))
                },
                onNavigateToVaccines = { id ->
                    navController.navigate(PawCareDestinations.vaccineList(id))
                },
                onNavigateToMedication = { id, nombre ->
                    navController.navigate(PawCareDestinations.medicationList(id, nombre))
                },
                onNavigateToAppointments = { id ->
                    navController.navigate(PawCareDestinations.appointmentList(id))
                },
                onNavigateToWeight = { id ->
                    navController.navigate(PawCareDestinations.weightList(id))
                }
            )
        }

        // Lista de vacunas
        composable(
            route = PawCareDestinations.VACCINE_LIST,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            VaccineScreen(
                petId = petId,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.vaccineForm(petId))
                }
            )
        }

        // Formulario de vacunas
        composable(
            route = PawCareDestinations.VACCINE_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType},
                navArgument("vaccineId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val vaccineId = backStackEntry.arguments?.getLong("vaccineId")
                ?.takeIf { it != -1L }
            VaccineFormScreen(
                petId = petId,
                vaccineId = vaccineId,
                onNavigateBack = { navController.popBackStack()}
            )
        }

        // Lista de turnos
        composable(
            route = PawCareDestinations.APPOINTMENT_LIST,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType}
            )
        ) {backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            AppointmentScreen(
                petId = petId,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.appointmentForm(petId))
                }
            )
        }

        // Formulario de turnos
        composable(
            route = PawCareDestinations.APPOINTMENT_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType},
                navArgument("appointmentId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ){ backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val appointmentId = backStackEntry.arguments?.getLong("appointmentId")
                ?.takeIf { it != -1L }
            AppointmentFormScreen(
                petId = petId,
                appointmentId = appointmentId,
                onNavigateBack = { navController.popBackStack()}
            )
        }

        // Lista de medicamentos
        composable(
            route = PawCareDestinations.MEDICATION_LIST,
            arguments = listOf(
                navArgument("petId") {type = NavType.LongType},
                navArgument("petName") {type = NavType.StringType}
            )
        ) {backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            MedicationScreen(
                petId = petId,
                petName = petName,
                onNavigateBack = {navController.popBackStack()},
                onNavigateToForm = {
                    navController.navigate(
                        PawCareDestinations.medicationForm(petId, petName)
                    )
                }
            )
        }
        // Formulario de medicamentos
        composable(
            route = PawCareDestinations.MEDICATION_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType},
                navArgument("petName") { type = NavType.StringType},
                navArgument("medicationId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ){backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val medicationId = backStackEntry.arguments?.getLong("medicationId")
                ?.takeIf { it != -1L }
            MedicationFormScreen(
                petId = petId,
                petName = petName,
                medicationId = medicationId,
                onNavigateBack = {navController.popBackStack()},
            )
        }

        // Lista de weighst
        composable(
            route = PawCareDestinations.WEIGHT_LIST,
            arguments = listOf(
                navArgument("petId") {type = NavType.LongType}
            )
        ) {backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            WeightScreen(
                petId = petId,
                onNavigateBack = {navController.popBackStack()},
                onNavigateToForm = {navController.navigate(PawCareDestinations.weightForm(petId))}
            )
        }

        // Formulario de weights
        composable(
            route = PawCareDestinations.WEIGHT_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType},
                navArgument("weightId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ){backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val weightId = backStackEntry.arguments?.getLong("weightId")
                ?.takeIf { it != -1L }
            WeightFormScreen(
                petId = petId,
                weightId = weightId,
                onNavigateBack = {navController.popBackStack()},
            )
        }
    }
}
