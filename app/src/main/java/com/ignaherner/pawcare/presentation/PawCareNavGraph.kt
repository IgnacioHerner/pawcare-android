package com.ignaherner.pawcare.presentation

import android.R.attr.type
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ignaherner.pawcare.presentation.appointments.AppointmentFormScreen
import com.ignaherner.pawcare.presentation.appointments.AppointmentScreen
import com.ignaherner.pawcare.presentation.home.HomeScreen
import com.ignaherner.pawcare.presentation.medications.MedicationDetailScreen
import com.ignaherner.pawcare.presentation.medications.MedicationFormScreen
import com.ignaherner.pawcare.presentation.medications.MedicationScreen
import com.ignaherner.pawcare.presentation.medications.MedicationViewModel
import com.ignaherner.pawcare.presentation.owners.OwnerDetailScreen
import com.ignaherner.pawcare.presentation.owners.OwnerFormScreen
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.presentation.pets.PetDetailScreen
import com.ignaherner.pawcare.presentation.pets.PetFormScreen
import com.ignaherner.pawcare.presentation.settings.SettingsScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineFormScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineViewModel
import com.ignaherner.pawcare.presentation.weight.WeightFormScreen
import com.ignaherner.pawcare.presentation.weight.WeightScreen
import java.net.URLDecoder
import java.net.URLEncoder

object PawCareDestinations {

    // Home
    const val HOME = "home"

    // Pets
    const val PET_DETAIL = "pet_detail/{petId}"
    const val PET_FORM = "pet_form?petId={petId}"

    // Vaccines
    const val VACCINE_LIST = "vaccine_list/{petId}/{petName}"
    const val VACCINE_FORM = "vaccine_form/{petId}/{petName}?vaccineId={vaccineId}"

    // Appointments
    const val APPOINTMENT_LIST = "appointment_list/{petId}/{petName}"
    const val APPOINTMENT_FORM = "appointment_form/{petId}/{petName}?appointmentId={appointmentId}"

    // Medications
    const val MEDICATION_LIST = "medication_list/{petId}/{petName}"
    const val MEDICATION_FORM = "medication_form/{petId}/{petName}?medicationId={medicationId}"
    const val MEDICATION_DETAIL = "medication_detail/{medicationId}/{petId}/{petName}"

    // Weights
    const val WEIGHT_LIST = "weight_list/{petId}"
    const val WEIGHT_FORM = "weight_form/{petId}?weightId={weightId}"

    // Settings
    const val SETTINGS = "settings"

    // Owner
    const val OWNER_FORM = "owner_form"
    const val OWNER_EDIT = "owner_edit"
    const val OWNER_DETAIL = "owner_detail"

    // Splash
    const val SPLASH = "splash"

    // Funciones para construir rutas con argumentos
    fun petDetail(petId: Long) = "pet_detail/$petId"
    fun petForm(petId: Long? = null) = if (petId != null) "pet_form?petId=$petId" else "pet_form"

    // Funciones para vaccines
    fun vaccineList(petId: Long, petName: String) = "vaccine_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"
    fun vaccineForm(petId: Long, petName: String, vaccineId: Long? = null) =
        if (vaccineId != null)
            "vaccine_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?vaccineId=$vaccineId"
        else
            "vaccine_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    // Funciones para appoinments
    fun appointmentList(petId: Long, petName: String) = "appointment_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"
    fun appointmentForm(petId: Long, petName: String, appointmentId: Long? = null) =
        if (appointmentId != null)
            "appointment_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?appointmentId=$appointmentId"
        else
            "appointment_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    // Funciones para medications
    fun medicationList(petId: Long, petName: String) = "medication_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"
    fun medicationForm(petId: Long, petName: String, medicationId: Long? = null) =
        if(medicationId != null)
            "medication_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?medicationId=$medicationId"
        else
            "medication_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun medicationDetail(medicationId: Long, petId: Long, petName: String) =
        "medication_detail/$medicationId/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    // Funciones para weights
    fun weightList(petId: Long) = "weight_list/$petId"
    fun weightForm(petId: Long, weightId: Long? = null) =
        if(weightId != null) "weight_form/$petId?weightId=$weightId" else "weight_form/$petId"
}

@Composable
fun PawCareNavGraph(
    navController: NavHostController = rememberNavController()
) {

    navController.addOnDestinationChangedListener { _, destination, _ ->
        android.util.Log.d("NavDebug", "Navegando a: ${destination.route}")
    }

    NavHost(
        navController = navController,
        startDestination = PawCareDestinations.SPLASH
    ) {

        composable(PawCareDestinations.SPLASH){

            val ownerViewModel: OwnerViewModel = hiltViewModel()
            val ownerExist by ownerViewModel.ownerExists.collectAsStateWithLifecycle()

            SplashScreen(
                onSplashFinished = {
                    navController.navigate(
                        if (ownerExist == true)
                            PawCareDestinations.HOME
                        else
                            PawCareDestinations.OWNER_FORM
                    ) {
                        popUpTo(PawCareDestinations.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(PawCareDestinations.HOME) {
            var isNavigating by remember { mutableStateOf(false) }

            HomeScreen(
                onNavigateToPetDetail = { petId ->
                    if (!isNavigating) {
                        isNavigating = true
                        navController.navigate(PawCareDestinations.petDetail(petId)) {
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToSettings = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.navigate(PawCareDestinations.SETTINGS) {
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToOwnerDetail = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.navigate(PawCareDestinations.OWNER_DETAIL) {
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToAddPet = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.navigate(PawCareDestinations.petForm()) {
                            launchSingleTop = true
                        }
                    }
                }
            )

            // Resetear cuando volvemos a HOME
            LaunchedEffect(navController.currentDestination?.route) {
                if (navController.currentDestination?.route == PawCareDestinations.HOME) {
                    isNavigating = false
                }
            }
        }

        // Formulario - sirve para crear y editar
        composable(PawCareDestinations.OWNER_FORM) {
            OwnerFormScreen(
                ownerId = null,
                onNavigateBack = {
                    navController.navigate(PawCareDestinations.HOME) {
                        popUpTo(PawCareDestinations.OWNER_FORM) { inclusive = true}
                    }
                }
            )
        }

        composable(PawCareDestinations.OWNER_DETAIL) {
            OwnerDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(PawCareDestinations.OWNER_EDIT)
                }
            )
        }

        // Editar dueño
        composable(PawCareDestinations.OWNER_EDIT){
            OwnerFormScreen(
                ownerId = 1L, // Siempre hay un solo Owner
                onNavigateBack = { navController.popBackStack()}
            )
        }

        // Settings DataStore
        composable(PawCareDestinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack()}
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
                onNavigateToVaccines = { id, nombre ->
                    navController.navigate(PawCareDestinations.vaccineList(id, nombre))
                },
                onNavigateToAppointments = { id, nombre ->
                    navController.navigate(PawCareDestinations.appointmentList(id, nombre))
                },
                onNavigateToWeight = { id ->
                    navController.navigate(PawCareDestinations.weightList(id))
                },
                onNavigateToMedication = { id, nombre ->
                    navController.navigate(PawCareDestinations.medicationList(id, nombre))
                },
                onNavigateToOwnerDetail = {
                    navController.navigate(PawCareDestinations.OWNER_DETAIL)
                }
            )
        }

        // Lista de vacunas
        composable(
            route = PawCareDestinations.VACCINE_LIST,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType },
                navArgument("petName") { type = NavType.StringType}
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val viewModel: VaccineViewModel = hiltViewModel()
            VaccineScreen(
                petId = petId,
                petName = petName,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToEdit = { vaccineId ->
                    navController.navigate(PawCareDestinations.vaccineForm(petId, petName, vaccineId))},
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.vaccineForm(petId, petName))
                }
            )
        }

        // Formulario de vacunas
        composable(
            route = PawCareDestinations.VACCINE_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType},
                navArgument("petName") {type = NavType.StringType},
                navArgument("vaccineId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val vaccineId = backStackEntry.arguments?.getLong("vaccineId")
                ?.takeIf { it != -1L }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(PawCareDestinations.VACCINE_LIST)
            }
            val viewModel: VaccineViewModel = hiltViewModel(parentEntry)
            VaccineFormScreen(
                viewModel = viewModel,
                petId = petId,
                petName = petName,
                vaccineId = vaccineId,
                onNavigateBack = { navController.popBackStack()}
            )
        }

        // Lista de turnos
        composable(
            route = PawCareDestinations.APPOINTMENT_LIST,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType},
                navArgument("petName") { type = NavType.StringType}
            )
        ) {backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            AppointmentScreen(
                petId = petId,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToEdit = { appointmentId ->
                    navController.navigate(PawCareDestinations.appointmentForm(petId, petName, appointmentId))},
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.appointmentForm(petId, petName))
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
            val viewModel: MedicationViewModel = hiltViewModel()
            MedicationScreen(
                viewModel = viewModel,
                petId = petId,
                petName = petName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { medicationId ->
                    navController.navigate(
                        PawCareDestinations.medicationForm(
                            petId,
                            petName,
                            medicationId
                        )
                    )
                },
                onNavigateToForm = {
                    navController.navigate(
                        PawCareDestinations.medicationForm(petId, petName)
                    )
                },
                onNavigateToDetail = { medicationId ->
                    navController.navigate(
                        PawCareDestinations.medicationDetail(medicationId, petId, petName)
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
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(PawCareDestinations.MEDICATION_LIST)
            }
            val viewModel: MedicationViewModel = hiltViewModel(parentEntry)
            MedicationFormScreen(
                viewModel = viewModel,
                petId = petId,
                petName = petName,
                medicationId = medicationId,
                onNavigateBack = {navController.popBackStack()},
            )
        }

        // Detalle de Medicacion
        composable(
            route = PawCareDestinations.MEDICATION_DETAIL,
            arguments = listOf(
                navArgument("medicationId") { type = NavType.LongType },
                navArgument("petId") { type = NavType.LongType },
                navArgument("petName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val medicationId = backStackEntry.arguments?.getLong("medicationId")
                ?: return@composable
            val petId = backStackEntry.arguments?.getLong("petId")
                ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            MedicationDetailScreen(
                medicationId = medicationId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id  ->
                    navController.navigate(
                        PawCareDestinations.medicationForm(petId, petName, id)
                    )
                }
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

