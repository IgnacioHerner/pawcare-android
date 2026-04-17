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
import com.ignaherner.pawcare.domain.model.Rol
import com.ignaherner.pawcare.presentation.appointments.AppointmentDetailScreen
import com.ignaherner.pawcare.presentation.appointments.AppointmentFormScreen
import com.ignaherner.pawcare.presentation.appointments.AppointmentScreen
import com.ignaherner.pawcare.presentation.auth.AuthViewModel
import com.ignaherner.pawcare.presentation.auth.LoginScreen
import com.ignaherner.pawcare.presentation.auth.RegisterScreen
import com.ignaherner.pawcare.presentation.condition.ConditionFormScreen
import com.ignaherner.pawcare.presentation.condition.ConditionScreen
import com.ignaherner.pawcare.presentation.condition.ConditionViewModel
import com.ignaherner.pawcare.presentation.deworming.DewormingFormScreen
import com.ignaherner.pawcare.presentation.deworming.DewormingScreen
import com.ignaherner.pawcare.presentation.deworming.DewormingViewModel
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
import com.ignaherner.pawcare.presentation.pets.QRScreen
import com.ignaherner.pawcare.presentation.settings.SettingsScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineDetailScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineFormScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineViewModel
import com.ignaherner.pawcare.presentation.vet.VetHomeScreen
import com.ignaherner.pawcare.presentation.vet.VetPetDetailScreen
import com.ignaherner.pawcare.presentation.weight.WeightFormScreen
import com.ignaherner.pawcare.presentation.weight.WeightScreen
import kotlinx.coroutines.delay
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
    const val VACCINE_DETAIL = "vaccine_detail/{vaccineId}/{petId}/{petName}"

    // Appointments
    const val APPOINTMENT_LIST = "appointment_list/{petId}/{petName}"
    const val APPOINTMENT_FORM = "appointment_form/{petId}/{petName}?appointmentId={appointmentId}"
    const val APPOINTMENT_DETAIL = "appointment_detail/{appointmentId}/{petId}/{petName}"

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


    // Condition
    const val CONDITION_LIST = "condition_list/{petId}/{petName}"
    const val CONDITION_FORM = "condition_form/{petId}/{petName}?conditionId={conditionId}"

    //
    const val DEWORMING_LIST = "deworming_list/{petId}/{petName}"
    const val DEWORMING_FORM = "deworming_form/{petId}/{petName}?dewormingId={dewormingId}"

    // Splash
    const val SPLASH = "splash"

    const val QR_SCREEN = "qr_screen/{petId}"

    // Login y Register
    const val LOGIN = "login"
    const val REGISTER = "register"

    // VETERINARIO
    const val VET_HOME = "vet_home"
    const val VET_PET_DETAIL = "vet_pet_detail/{firestoreId}"

    const val LOADING = "loading"



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
    fun vaccineDetail(vaccineId: Long, petId: Long, petName: String) =
        "vaccine_detail/$vaccineId/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    // Funciones para appoinments
    fun appointmentList(petId: Long, petName: String) = "appointment_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"
    fun appointmentForm(petId: Long, petName: String, appointmentId: Long? = null) =
        if (appointmentId != null)
            "appointment_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?appointmentId=$appointmentId"
        else
            "appointment_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"
    fun appointmentDetail(appointmentId: Long, petId: Long, petName: String) =
        "appointment_detail/$appointmentId/$petId/${URLEncoder.encode(petName, "UTF-8")}"

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

    // Funciones para conditions
    fun conditionList(petId: Long, petName: String) =
        "condition_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun conditionForm(petId: Long, petName: String, conditionId: Long? = null) =
        if (conditionId != null)
            "condition_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?conditionId=$conditionId"
        else
            "condition_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun dewormingList(petId: Long, petName: String) =
        "deworming_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun dewormingForm(petId: Long, petName: String, dewormingId: Long? = null) =
        if (dewormingId != null)
            "deworming_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?deworming=$dewormingId"
        else
            "deworming_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun qrScreen(petId: Long) = "qr_screen/$petId"

    fun vetPetDetail(firestoreId: String) = "vet_pet_detail/$firestoreId"

}

@Composable
fun PawCareNavGraph(
    navController: NavHostController = rememberNavController()
) {

    val authViewModel: AuthViewModel = hiltViewModel()

    navController.addOnDestinationChangedListener { _, destination, _ ->
        android.util.Log.d("NavDebug", "Navegando a: ${destination.route}")
    }

    NavHost(
        navController = navController,
        startDestination = PawCareDestinations.SPLASH
    ) {
        composable(PawCareDestinations.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    if (authViewModel.isLoggedIn) {
                        navController.navigate(PawCareDestinations.LOADING) {
                            popUpTo(PawCareDestinations.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(PawCareDestinations.LOGIN) {
                            popUpTo(PawCareDestinations.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(PawCareDestinations.LOADING) {
            val ownerViewModel: OwnerViewModel = hiltViewModel()
            val rol by authViewModel.rol.collectAsStateWithLifecycle()
            val ownerExists by ownerViewModel.ownerExists.collectAsStateWithLifecycle()

            LaunchedEffect(rol) {
                if (rol == Rol.DUENO) {
                    ownerViewModel.sincronizarOwner() // ← suspend, espera hasta terminar
                }
            }

            LaunchedEffect(rol, ownerExists) {

                if (rol == null) return@LaunchedEffect

                when {
                    rol == Rol.VETERINARIO -> {
                        navController.navigate(PawCareDestinations.VET_HOME) {
                            popUpTo(PawCareDestinations.LOADING) { inclusive = true }
                        }
                    }
                    ownerExists == null -> return@LaunchedEffect
                    ownerExists == false -> {
                        navController.navigate(PawCareDestinations.OWNER_FORM) {
                            popUpTo(PawCareDestinations.LOADING) { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate(PawCareDestinations.HOME) {
                            popUpTo(PawCareDestinations.LOADING) { inclusive = true }
                        }
                    }
                }
            }

            LoadingScreen()
        }

        // Login
        composable(PawCareDestinations.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(PawCareDestinations.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(PawCareDestinations.LOADING) {
                        popUpTo(PawCareDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Register
        composable(PawCareDestinations.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(PawCareDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(PawCareDestinations.LOADING) {
                        popUpTo(PawCareDestinations.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // VetHome
        composable(PawCareDestinations.VET_HOME) {
            VetHomeScreen(
                onNavigateToSettings = {
                    navController.navigate(PawCareDestinations.SETTINGS)
                },
                onNavigateToPetDetail = { firestoreId ->
                    navController.navigate(PawCareDestinations.vetPetDetail(firestoreId))
                }
            )
        }

        // VetPetDetail
        composable(
            route = PawCareDestinations.VET_PET_DETAIL,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType}
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            VetPetDetailScreen(
                firestoreId = firestoreId,
                onNavigateBack = { navController.popBackStack()}
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
                },
                onNavigateToEdit = { petId ->
                    navController.navigate(PawCareDestinations.petForm(petId)) {
                        launchSingleTop = true
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
                viewModel = hiltViewModel(),
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToLogin = {
                    navController.navigate(PawCareDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // QR
        composable(
            route = PawCareDestinations.QR_SCREEN,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType}
            )
        ){ backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            QRScreen(
                petId = petId,
                onNavigateBack = { navController.popBackStack() }
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
                },
                onNavigateToConditions = { id, nombre ->
                    navController.navigate(PawCareDestinations.conditionList(id, nombre))
                },
                onNavigateToQR = { petId ->
                    navController.navigate(PawCareDestinations.qrScreen(petId))
                },
                onNavigateToDeworming = { id, nombre ->
                    navController.navigate(PawCareDestinations.dewormingList(id, nombre))
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
                },
                onNavigateToDetail = { vaccineId ->
                    navController.navigate(
                        PawCareDestinations.vaccineDetail(vaccineId, petId, petName)
                    )}
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

        // Vacunas detalle
        composable(
            route = PawCareDestinations.VACCINE_DETAIL,
            arguments = listOf(
                navArgument("vaccineId") {type = NavType.LongType},
                navArgument("petId") {type = NavType.LongType},
                navArgument("petName") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val vaccineId = backStackEntry.arguments?.getLong("vaccineId") ?: return@composable
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            VaccineDetailScreen(
                vaccineId = vaccineId,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToEdit = { id ->
                    navController.navigate(
                        PawCareDestinations.vaccineForm(petId,petName, id)
                    )
                }
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
                },
                onNavigateToDetail = { appointmentId ->
                    navController.navigate(PawCareDestinations.appointmentDetail(appointmentId, petId, petName))
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

        // Turnos detalle
        composable(
            route = PawCareDestinations.APPOINTMENT_DETAIL,
            arguments = listOf(
                navArgument("appointmentId") {type = NavType.LongType},
                navArgument("petId") {type = NavType.LongType},
                navArgument("petName") {type = NavType.StringType}
            )
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getLong("appointmentId") ?: return@composable
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            AppointmentDetailScreen(
                appointmentId = appointmentId,
                onNavigateBack = { navController.popBackStack()},
                onNavigateToEdit = { id ->
                    navController.navigate(
                        PawCareDestinations.appointmentForm(petId, petName, id)
                    )
                }
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

        // Lista de condiciones
        composable(
            route = PawCareDestinations.CONDITION_LIST,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType },
                navArgument("petName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val viewModel: ConditionViewModel = hiltViewModel()
            ConditionScreen(
                petId = petId,
                petName = petName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.conditionForm(petId, petName))
                },
                onNavigateToEdit = { conditionId ->
                    navController.navigate(
                        PawCareDestinations.conditionForm(petId, petName, conditionId)
                    )}
            )
        }

        // Formulario de condiciones
        composable(
            route = PawCareDestinations.CONDITION_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType },
                navArgument("petName") { type = NavType.StringType },
                navArgument("conditionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val conditionId = backStackEntry.arguments?.getLong("conditionId")
                ?.takeIf { it != -1L }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(
                    PawCareDestinations.conditionList(petId, petName)
                )
            }
            val viewModel: ConditionViewModel = hiltViewModel(parentEntry)
            ConditionFormScreen(
                petId = petId,
                petName = petName,
                conditionId = conditionId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // Lista desparasitacion
        composable(
            route = PawCareDestinations.DEWORMING_LIST,
            arguments = listOf(
                navArgument("petId") {type = NavType.LongType},
                navArgument("petName") {type = NavType.StringType}
            )
        ){ backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val viewModel: DewormingViewModel = hiltViewModel()
            DewormingScreen(
                petId = petId,
                petName = petName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.dewormingForm(petId, petName))
                },
                onNavigateToEdit = { dewormingId ->
                    navController.navigate(
                        PawCareDestinations.dewormingForm(petId, petName, dewormingId)
                    )
                }
            )
        }

        // Formulario desparasitacion
        composable(
            route = PawCareDestinations.DEWORMING_FORM,
            arguments = listOf(
                navArgument("petId") {type = NavType.LongType},
                navArgument("petName") {type = NavType.StringType},
                navArgument("dewormingId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8")
            val dewormingId = backStackEntry.arguments?.getLong("dewormingId")
                ?.takeIf { it != -1L }
            val parentEntry = remember (backStackEntry) {
                navController.getBackStackEntry(
                    PawCareDestinations.dewormingList(petId, petName)
                )
            }
            val viewModel: DewormingViewModel = hiltViewModel(parentEntry)
            DewormingFormScreen(
                petId = petId,
                petName = petName,
                dewormingId = dewormingId,
                onNavigateBack = { navController.popBackStack()},
                viewModel = viewModel
            )
        }
    }
}

