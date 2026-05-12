package com.ignaherner.mispatitas.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ignaherner.mispatitas.domain.model.Especie
import com.ignaherner.mispatitas.domain.model.Rol
import com.ignaherner.mispatitas.domain.model.VetHistorialTipo
import com.ignaherner.mispatitas.presentation.appointments.AppointmentDetailScreen
import com.ignaherner.mispatitas.presentation.appointments.AppointmentFormScreen
import com.ignaherner.mispatitas.presentation.appointments.AppointmentScreen
import com.ignaherner.mispatitas.presentation.appointments.AppointmentViewModel
import com.ignaherner.mispatitas.presentation.auth.AuthViewModel
import com.ignaherner.mispatitas.presentation.auth.LoginScreen
import com.ignaherner.mispatitas.presentation.auth.OnboardingScreen
import com.ignaherner.mispatitas.presentation.auth.RoleSelectScreen
import com.ignaherner.mispatitas.presentation.auth.WelcomeScreen
import com.ignaherner.mispatitas.presentation.condition.ConditionDetailScreen
import com.ignaherner.mispatitas.presentation.condition.ConditionFormScreen
import com.ignaherner.mispatitas.presentation.condition.ConditionScreen
import com.ignaherner.mispatitas.presentation.condition.ConditionViewModel
import com.ignaherner.mispatitas.presentation.deworming.DewormingDetailScreen
import com.ignaherner.mispatitas.presentation.deworming.DewormingFormScreen
import com.ignaherner.mispatitas.presentation.deworming.DewormingScreen
import com.ignaherner.mispatitas.presentation.deworming.DewormingViewModel
import com.ignaherner.mispatitas.presentation.medications.MedicationDetailScreen
import com.ignaherner.mispatitas.presentation.medications.MedicationFormScreen
import com.ignaherner.mispatitas.presentation.medications.MedicationScreen
import com.ignaherner.mispatitas.presentation.medications.MedicationViewModel
import com.ignaherner.mispatitas.presentation.owners.OwnerDetailScreen
import com.ignaherner.mispatitas.presentation.owners.OwnerFormScreen
import com.ignaherner.mispatitas.presentation.owners.OwnerViewModel
import com.ignaherner.mispatitas.presentation.pets.PetDetailScreen
import com.ignaherner.mispatitas.presentation.pets.PetFormScreen
import com.ignaherner.mispatitas.presentation.pets.QRScreen
import com.ignaherner.mispatitas.presentation.settings.SettingsScreen
import com.ignaherner.mispatitas.presentation.settings.SettingsViewModel
import com.ignaherner.mispatitas.presentation.vaccines.VaccineDetailScreen
import com.ignaherner.mispatitas.presentation.vaccines.VaccineFormScreen
import com.ignaherner.mispatitas.presentation.vaccines.VaccineScreen
import com.ignaherner.mispatitas.presentation.vaccines.VaccineViewModel
import com.ignaherner.mispatitas.presentation.vet.VetFormScreen
import com.ignaherner.mispatitas.presentation.vet.VetHistorialScreen
import com.ignaherner.mispatitas.presentation.vet.VetHomeScreen
import com.ignaherner.mispatitas.presentation.auth.VetLoginScreen
import com.ignaherner.mispatitas.presentation.auth.VetRegisterScreen
import com.ignaherner.mispatitas.presentation.auth.VetWelcomeScreen
import com.ignaherner.mispatitas.presentation.pets.PetDetailState
import com.ignaherner.mispatitas.presentation.pets.PetViewModel
import com.ignaherner.mispatitas.presentation.vet.QRScannerScreen
import com.ignaherner.mispatitas.presentation.vet.VetOwnerDetailScreen
import com.ignaherner.mispatitas.presentation.vet.VetPetDetailScreen
import com.ignaherner.mispatitas.presentation.vet.VetProfileDetailScreen
import com.ignaherner.mispatitas.presentation.vet.VetProfileViewModel
import com.ignaherner.mispatitas.presentation.vet.VetViewModel
import com.ignaherner.mispatitas.presentation.weight.WeightFormScreen
import com.ignaherner.mispatitas.presentation.weight.WeightScreen
import com.ignaherner.mispatitas.presentation.weight.WeightViewModel
import kotlinx.coroutines.delay
import java.net.URLDecoder
import java.net.URLEncoder

object MisPatitasDestinations {

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
    const val CONDITION_DETAIL = "condition_detail/{conditionId}"

    // Desparasitacion
    const val DEWORMING_LIST = "deworming_list/{petId}/{petName}"
    const val DEWORMING_FORM = "deworming_form/{petId}/{petName}?dewormingId={dewormingId}"

    const val DEWORMING_DETAIL = "deworming_detail/{dewormingId}"

    // Splash
    const val SPLASH = "splash"

    const val QR_SCREEN = "qr_screen/{petId}"

    const val QR_SCANNER = "qr_scanner"

    // Login y Register
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val ROLE_SELECT = "role_select"
    const val WELCOME = "welcome/{nombre}"
    const val VET_WELCOME = "vet_welcome/{nombre}"
    const val VET_LOGIN = "vet_login"
    const val VET_REGISTER = "vet_register"


    // VETERINARIO
    const val VET_HOME = "vet_home"
    const val VET_PET_DETAIL = "vet_pet_detail/{firestoreId}"
    const val VET_FORM = "vet_form"
    const val VET_HISTORIAL = "vet_historial/{firestoreId}/{tipo}"
    const val VET_OWNER_DETAIL = "vet_owner_detail/{ownerId}"

    const val VET_PROFILE_DETAIL = "vet_profile_detail"
    const val VET_VACCINE_FORM = "vet_vaccine_form/{firestoreId}"
    const val VET_MEDICATION_FORM = "vet_medication_form/{firestoreId}"
    const val VET_WEIGHT_FORM = "vet_weight_form/{firestoreId}"
    const val VET_APPOINTMENT_FORM = "vet_appointment_form/{firestoreId}"
    const val VET_CONDITION_FORM = "vet_condition_form/{firestoreId}"
    const val VET_DEWORMING_FORM = "vet_deworming_form/{firestoreId}"

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

    fun conditionDetail(conditionId: Long) = "condition_detail/$conditionId"

    fun dewormingList(petId: Long, petName: String) =
        "deworming_list/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun dewormingForm(petId: Long, petName: String, dewormingId: Long? = null) =
        if (dewormingId != null)
            "deworming_form/$petId/${URLEncoder.encode(petName, "UTF-8")}?dewormingId=$dewormingId"
        else
            "deworming_form/$petId/${URLEncoder.encode(petName, "UTF-8")}"

    fun dewormingDetail(dewormingId: Long) = "deworming_detail/$dewormingId"

    fun qrScreen(petId: Long) = "qr_screen/$petId"

    fun vetPetDetail(firestoreId: String) = "vet_pet_detail/$firestoreId"

    fun vetHistorial(firestoreId: String, tipo: VetHistorialTipo) = "vet_historial/$firestoreId/${tipo.name}"

    fun vetOwnerDetail(ownerId: String) = "vet_owner_detail/$ownerId"
    fun vetVaccineForm(firestoreId: String) = "vet_vaccine_form/$firestoreId"
    fun vetMedicationForm(firestoreId: String) = "vet_medication_form/$firestoreId"
    fun vetWeightForm(firestoreId: String) = "vet_weight_form/$firestoreId"
    fun vetAppointmentForm(firestoreId: String) = "vet_appointment_form/$firestoreId"
    fun vetConditionForm(firestoreId: String) = "vet_condition_form/$firestoreId"
    fun vetDewormingForm(firestoreId: String) = "vet_deworming_form/$firestoreId"

    fun welcome(nombre: String) = "welcome/${URLEncoder.encode(nombre, "UTF-8")}"

    fun vetWelcome(nombre: String) = "vet_welcome/${URLEncoder.encode(nombre, "UTF-8")}"


}

@Composable
fun MisPatitasNavGraph(
    navController: NavHostController = rememberNavController()
) {

    val authViewModel: AuthViewModel = hiltViewModel()

    navController.addOnDestinationChangedListener { _, destination, _ ->
        Log.d("NavDebug", "Navegando a: ${destination.route}")
    }

    fun NavHostController.safeNavigate(route: String) {
        val currentRoute = currentBackStackEntry?.destination?.route
        if (currentRoute != route) {
            navigate(route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = MisPatitasDestinations.SPLASH
    ) {
        composable(MisPatitasDestinations.SPLASH) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val onboardingCompleted by settingsViewModel.onboardingCompleted.collectAsStateWithLifecycle(
                initialValue = null
            )

            SplashScreen(
                onSplashFinished = {
                    when {
                        // Ya logueado → cargar app
                        authViewModel.isLoggedIn -> {
                            navController.navigate(MisPatitasDestinations.LOADING) {
                                popUpTo(MisPatitasDestinations.SPLASH) { inclusive = true }
                            }
                        }
                        // Primera vez → onboarding
                        onboardingCompleted == false -> {
                            navController.navigate(MisPatitasDestinations.ONBOARDING) {
                                popUpTo(MisPatitasDestinations.SPLASH) { inclusive = true }
                            }
                        }
                        // Ya vio onboarding → login
                        else -> {
                            navController.navigate(MisPatitasDestinations.LOGIN) {
                                popUpTo(MisPatitasDestinations.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // OWNER ONBOARDING
        composable(MisPatitasDestinations.ONBOARDING) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            OnboardingScreen(
                onFinished = {
                    settingsViewModel.completeOnboarding()
                    navController.navigate(MisPatitasDestinations.LOGIN) {
                        popUpTo(MisPatitasDestinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // WELCOME
        composable(
            route = MisPatitasDestinations.WELCOME,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            WelcomeScreen(
                nombreUsuario = URLDecoder.decode(nombre, "UTF-8"),
                onNavigateToAddPet = {
                    navController.navigate(MisPatitasDestinations.petForm()) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // BIENVENIDA VETERINARIO
        composable(
            route = MisPatitasDestinations.VET_WELCOME,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = URLDecoder.decode(
                backStackEntry.arguments?.getString("nombre") ?: "", "UTF-8"
            )
            VetWelcomeScreen(
                nombreVet = nombre,
                onStart = {
                    navController.navigate(MisPatitasDestinations.LOADING) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable(MisPatitasDestinations.LOADING) {
            val ownerViewModel: OwnerViewModel = hiltViewModel()
            val vetViewModel: VetProfileViewModel = hiltViewModel()
            val rol by authViewModel.rol.collectAsStateWithLifecycle()
            val ownerExists by ownerViewModel.ownerExists.collectAsStateWithLifecycle()
            val vetExists by vetViewModel.vetExists.collectAsStateWithLifecycle()
            var timeoutFired by remember { mutableStateOf(false) }

            // Timeout: si en 5 seg no resuelve, ir a login
            LaunchedEffect(Unit) {
                delay(5000)
                if (rol == null && !timeoutFired) {
                    timeoutFired = true
                    authViewModel.logout()
                    navController.navigate(MisPatitasDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            LaunchedEffect(rol, ownerExists, vetExists) {
                if (rol == null) return@LaunchedEffect

                when {
                    rol == Rol.VETERINARIO && vetExists == null -> return@LaunchedEffect
                    rol == Rol.VETERINARIO && vetExists == false -> {
                        navController.navigate(MisPatitasDestinations.VET_FORM) {
                            popUpTo(MisPatitasDestinations.LOADING) { inclusive = true }
                        }
                    }
                    rol == Rol.VETERINARIO && vetExists == true -> {
                        navController.navigate(MisPatitasDestinations.VET_HOME) {
                            popUpTo(MisPatitasDestinations.LOADING) { inclusive = true }
                        }
                    }
                    ownerExists == null -> return@LaunchedEffect
                    ownerExists == false -> {
                        navController.navigate(MisPatitasDestinations.OWNER_FORM) {
                            popUpTo(MisPatitasDestinations.LOADING) { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate(MisPatitasDestinations.HOME) {
                            popUpTo(MisPatitasDestinations.LOADING) { inclusive = true }
                        }
                    }
                }
            }

            LoadingScreen()
        }

        // Login dueno
        composable(MisPatitasDestinations.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    authViewModel.setSelectedRole("DUENO")
                    navController.navigate(MisPatitasDestinations.REGISTER)
                },
                onNavigateToVetRegister = {
                    authViewModel.setSelectedRole("VETERINARIO")
                    navController.navigate(MisPatitasDestinations.VET_REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(MisPatitasDestinations.LOADING) {
                        popUpTo(MisPatitasDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Register VET
        composable(MisPatitasDestinations.VET_REGISTER) {
            VetRegisterScreen(
                viewModel = authViewModel,
                onNavigateToVetLogin = {
                    navController.navigate(MisPatitasDestinations.VET_LOGIN) {
                        popUpTo(MisPatitasDestinations.VET_REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = { nombre ->
                    navController.navigate(MisPatitasDestinations.vetWelcome(nombre)) {
                        popUpTo(MisPatitasDestinations.VET_REGISTER) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                }
            )
        }

        // Elegir rol DUENO - VETERINARIO
        composable(MisPatitasDestinations.ROLE_SELECT) {
            RoleSelectScreen(
                onRoleSelected = { rol ->
                    authViewModel.setSelectedRole(rol)
                    if (rol == "VETERINARIO") {
                        navController.navigate(MisPatitasDestinations.VET_LOGIN) {
                            popUpTo(MisPatitasDestinations.ROLE_SELECT) { inclusive = true }
                        }
                    } else {
                        navController.navigate(MisPatitasDestinations.REGISTER) {
                            popUpTo(MisPatitasDestinations.ROLE_SELECT) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Login veterinario
        composable(MisPatitasDestinations.VET_LOGIN) {
            VetLoginScreen(
                viewModel = authViewModel,
                onNavigateToVetRegister = {
                    navController.navigate(MisPatitasDestinations.VET_REGISTER) {
                        popUpTo(MisPatitasDestinations.VET_LOGIN) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(MisPatitasDestinations.LOADING) {
                        popUpTo(MisPatitasDestinations.VET_LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                }
            )
        }

        // VetHome
        composable(MisPatitasDestinations.VET_HOME) {
            val scannedCode = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("scannedCode")

            // Limpiar después de leer
            LaunchedEffect(scannedCode) {
                if (scannedCode != null) {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<String>("scannedCode")
                }
            }

            VetHomeScreen(
                scannedCode = scannedCode,
                onNavigateToSettings = {
                    navController.navigate(MisPatitasDestinations.SETTINGS)
                },
                onNavigateToVetProfile = { navController.navigate(MisPatitasDestinations.VET_PROFILE_DETAIL) },
                onNavigateToPetDetail = { firestoreId ->
                    navController.navigate(MisPatitasDestinations.vetPetDetail(firestoreId))
                },
                onNavigateToQRScanner = {
                    navController.navigate(MisPatitasDestinations.QR_SCANNER)
                }
            )
        }

        // QR SCREEN
        composable(MisPatitasDestinations.QR_SCANNER) {
            QRScannerScreen(
                onCodeScanned = { code ->
                    // Guardar el código escaneado y volver
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("scannedCode", code)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // DETALLE DEL PERFIL VETERINARIO
        composable(MisPatitasDestinations.VET_PROFILE_DETAIL) {
            VetProfileDetailScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = {
                    navController.navigate(MisPatitasDestinations.VET_FORM)
                }
            )
        }

        // VetPetDetail
        composable(
            route = MisPatitasDestinations.VET_PET_DETAIL,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            VetPetDetailScreen(
                firestoreId = firestoreId,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToHistorial = { id, tipo ->
                    navController.navigate(MisPatitasDestinations.vetHistorial(id, tipo))
                },
                onNavigateToOwnerDetail = { ownerId ->
                    navController.navigate(MisPatitasDestinations.vetOwnerDetail(ownerId))
                },
                onNavigateToVetVaccineForm = { id ->
                    navController.navigate(MisPatitasDestinations.vetVaccineForm(id))
                },
                onNavigateToVetMedicationForm = { id ->
                    navController.navigate(MisPatitasDestinations.vetMedicationForm(id))
                },
                onNavigateToVetConditionForm = { id ->
                    navController.navigate(MisPatitasDestinations.vetConditionForm(id))
                },
                onNavigateToVetDewormingForm = { id ->
                    navController.navigate(MisPatitasDestinations.vetDewormingForm(id))
                },
                onNavigateToVetAppointmentForm = { id ->
                    navController.navigate(MisPatitasDestinations.vetAppointmentForm(id))
                },
                onNavigateToVetWeightForm = { id ->
                    navController.navigate(MisPatitasDestinations.vetWeightForm(id))
                }
            )
        }

        // VetForm
        composable(MisPatitasDestinations.VET_FORM) {
            VetFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(MisPatitasDestinations.VET_HOME) {
                            popUpTo(MisPatitasDestinations.VET_FORM) { inclusive = true }
                        }
                    }
                },
            )
        }

        // VetVaccineForm
        composable(
            route = MisPatitasDestinations.VET_VACCINE_FORM,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val vetViewModel: VetViewModel = hiltViewModel()
            val vetProfileViewModel: VetProfileViewModel = hiltViewModel()

            VaccineFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                isVetMode = true,
                vetProfileViewModel = vetProfileViewModel,
                onSave = { vaccine ->
                    vetViewModel.guardarVacuna(vaccine, firestoreId)
                }
            )
        }

        // VetOwnerDetail
        composable(
            route = MisPatitasDestinations.VET_OWNER_DETAIL,
            arguments = listOf(
                navArgument("ownerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ownerId = backStackEntry.arguments?.getString("ownerId") ?: return@composable
            VetOwnerDetailScreen(
                ownerId = ownerId,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToPetDetail = { firestoreId ->
                    navController.navigate(MisPatitasDestinations.vetPetDetail(firestoreId))
                }
            )
        }

        // VetMedicationForm
        composable(
            route = MisPatitasDestinations.VET_MEDICATION_FORM,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val vetViewModel: VetViewModel = hiltViewModel()
            val vetProfileViewModel: VetProfileViewModel = hiltViewModel()

            MedicationFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                isVetMode = true,
                vetProfileViewModel = vetProfileViewModel,
                onSave = { medication ->
                    vetViewModel.guardarMedicamento(medication, firestoreId)
                }
            )
        }

        // VetWeightForm
        composable(
            route = MisPatitasDestinations.VET_WEIGHT_FORM,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val vetViewModel: VetViewModel = hiltViewModel()

            WeightFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                isVetMode = true,
                onSave = { weight ->
                    vetViewModel.guardarPeso(weight, firestoreId)
                }
            )
        }

        // VetAppointmentForm
        composable(
            route = MisPatitasDestinations.VET_APPOINTMENT_FORM,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val vetViewModel: VetViewModel = hiltViewModel()
            val vetProfileViewModel: VetProfileViewModel = hiltViewModel()
            AppointmentFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                isVetMode = true,
                vetProfileViewModel = vetProfileViewModel,
                onSave = { appointment ->
                    vetViewModel.guardarTurno(appointment, firestoreId)
                }
            )
        }

        // VetConditionForm
        composable(
            route = MisPatitasDestinations.VET_CONDITION_FORM,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val vetViewModel: VetViewModel = hiltViewModel()
            val vetProfileViewModel: VetProfileViewModel = hiltViewModel()

            ConditionFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                isVetMode = true,
                vetProfileViewModel = vetProfileViewModel,
                onSave = { condition ->
                    vetViewModel.guardarCondicion(condition, firestoreId)
                }
            )
        }


        // VetDewormingForm
        composable(
            route = MisPatitasDestinations.VET_DEWORMING_FORM,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val vetViewModel: VetViewModel = hiltViewModel()
            val vetProfileViewModel: VetProfileViewModel = hiltViewModel()
            DewormingFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                isVetMode = true,
                vetProfileViewModel = vetProfileViewModel,
                onSave = {deworming ->
                    vetViewModel.guardarDesparasitacion(deworming, firestoreId)
                }
            )
        }

        // VetHistorial
        composable(
            route = MisPatitasDestinations.VET_HISTORIAL,
            arguments = listOf(
                navArgument("firestoreId") { type = NavType.StringType },
                navArgument("tipo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firestoreId = backStackEntry.arguments?.getString("firestoreId") ?: return@composable
            val tipo = VetHistorialTipo.valueOf(
                backStackEntry.arguments?.getString("tipo") ?: return@composable
            )
            VetHistorialScreen(
                firestoreId = firestoreId,
                tipo = tipo,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToForm = {
                    val route = when (tipo) {
                        VetHistorialTipo.VACUNAS -> MisPatitasDestinations.vetVaccineForm(firestoreId)
                        VetHistorialTipo.MEDICAMENTOS -> MisPatitasDestinations.vetMedicationForm(firestoreId)
                        VetHistorialTipo.PESOS -> MisPatitasDestinations.vetWeightForm(firestoreId)
                        VetHistorialTipo.TURNOS -> MisPatitasDestinations.vetAppointmentForm(firestoreId)
                        VetHistorialTipo.CONDICIONES -> MisPatitasDestinations.vetConditionForm(firestoreId)
                        VetHistorialTipo.DESPARASITACIONES -> MisPatitasDestinations.vetDewormingForm(firestoreId)
                    }
                    navController.navigate(route)
                }
            )
        }

        // Home
        composable(MisPatitasDestinations.HOME) {
            var isNavigating by remember { mutableStateOf(false) }

            MainScreen(
                navController = navController,
                onNavigateToPetDetail = { petId ->
                    navController.navigate(MisPatitasDestinations.petDetail(petId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAddPet = {
                    navController.navigate(MisPatitasDestinations.petForm()) {
                        launchSingleTop = true
                    }
                },
                onNavigateToEdit = { petId ->
                    navController.navigate(MisPatitasDestinations.petForm(petId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToOwnerDetail = {
                    navController.navigate(MisPatitasDestinations.OWNER_DETAIL) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(MisPatitasDestinations.SETTINGS) {
                        launchSingleTop = true
                    }
                },
                authViewModel = authViewModel
            )

            // Resetear cuando volvemos a HOME
            LaunchedEffect(navController.currentDestination?.route) {
                if (navController.currentDestination?.route == MisPatitasDestinations.HOME) {
                    isNavigating = false
                }
            }
        }

        // Formulario - sirve para crear y editar el dueno
        composable(MisPatitasDestinations.OWNER_FORM) {
            OwnerFormScreen(
                ownerId = null,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(MisPatitasDestinations.HOME) {
                            popUpTo(MisPatitasDestinations.OWNER_FORM) {inclusive = true}
                        }
                    }
                },
            )
        }

        // Owner Detail
        composable(MisPatitasDestinations.OWNER_DETAIL) {
            OwnerDetailScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = {
                    navController.navigate(MisPatitasDestinations.OWNER_EDIT)
                },
                onNavigateToPetDetail = { petId ->
                    navController.navigate(MisPatitasDestinations.petDetail(petId))
                }
            )
        }

        // Editar dueño
        composable(MisPatitasDestinations.OWNER_EDIT){
            OwnerFormScreen(
                ownerId = 1L, // Siempre hay un solo Owner
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                }
            )
        }

        // Settings DataStore
        composable(MisPatitasDestinations.SETTINGS) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                authViewModel = authViewModel,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(MisPatitasDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToOwnerDetail = {
                    navController.navigate(MisPatitasDestinations.OWNER_DETAIL)
                },
                onNavigateToVetForm = {
                    navController.navigate(MisPatitasDestinations.VET_PROFILE_DETAIL)
                }
            )
        }

        // QR
        composable(
            route = MisPatitasDestinations.QR_SCREEN,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType}
            )
        ){ backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            QRScreen(
                petId = petId,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
            )
        }

        // Formulario - sirve para crear y editar masctoas
        composable(
            route = MisPatitasDestinations.PET_FORM,
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId")?.takeIf { it != -1L }
            PetFormScreen(
                petId = petId,
                onNavigateBack = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        val popped = navController.popBackStack()
                        if (!popped) {
                            navController.navigate(MisPatitasDestinations.LOADING) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

            )
        }

        // PET DETAIL
        composable(
            route = MisPatitasDestinations.PET_DETAIL,
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            PetDetailScreen(
                petId = petId,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { id ->
                    navController.navigate(MisPatitasDestinations.petForm(id))
                },
                onNavigateToVaccines = { id, nombre ->
                    navController.navigate(MisPatitasDestinations.vaccineList(id, nombre))
                },
                onNavigateToAppointments = { id, nombre ->
                    navController.navigate(MisPatitasDestinations.appointmentList(id, nombre))
                },
                onNavigateToWeight = { id ->
                    navController.navigate(MisPatitasDestinations.weightList(id))
                },
                onNavigateToMedication = { id, nombre ->
                    navController.navigate(MisPatitasDestinations.medicationList(id, nombre))
                },
                onNavigateToOwnerDetail = {
                    navController.navigate(MisPatitasDestinations.OWNER_DETAIL)
                },
                onNavigateToConditions = { id, nombre ->
                    navController.navigate(MisPatitasDestinations.conditionList(id, nombre))
                },
                onNavigateToQR = { petId ->
                    navController.navigate(MisPatitasDestinations.qrScreen(petId))
                },
                onNavigateToDeworming = { id, nombre ->
                    navController.navigate(MisPatitasDestinations.dewormingList(id, nombre))
                }
            )
        }

        // Lista de vacunas
        composable(
            route = MisPatitasDestinations.VACCINE_LIST,
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
                isVeterinario = true,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { vaccineId ->
                    navController.navigate(MisPatitasDestinations.vaccineForm(petId, petName, vaccineId))},
                onNavigateToForm = {
                    navController.navigate(MisPatitasDestinations.vaccineForm(petId, petName))
                },
                onNavigateToDetail = { vaccineId ->
                    navController.navigate(
                        MisPatitasDestinations.vaccineDetail(vaccineId, petId, petName)
                    )}
            )
        }

        // Formulario de vacunas
        composable(
            route = MisPatitasDestinations.VACCINE_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType },
                navArgument("petName") { type = NavType.StringType },
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
                navController.getBackStackEntry(MisPatitasDestinations.VACCINE_LIST)
            }
            val vaccineViewModel: VaccineViewModel = hiltViewModel(parentEntry)
            val petViewModel: PetViewModel = hiltViewModel()

            val petDetailState by petViewModel.detailState.collectAsStateWithLifecycle()

            LaunchedEffect(petId) {
                petViewModel.loadPetById(petId)
            }

            val especie = when (val state = petDetailState) {
                is PetDetailState.Success -> state.pet.especie
                else -> Especie.PERRO
            }

            VaccineFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                vaccineId = vaccineId,
                especie = especie,
                vaccineViewModel = vaccineViewModel,
                onSave = { vaccine ->
                    val vaccineConPetId = vaccine.copy(petId = petId)
                    if (vaccineId == null) {
                        vaccineViewModel.insertVaccine(vaccineConPetId, petName)
                    } else {
                        vaccineViewModel.updateVaccine(vaccineConPetId, petName)
                    }
                }
            )
        }

        // Vacunas detalle
        composable(
            route = MisPatitasDestinations.VACCINE_DETAIL,
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
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { id ->
                    navController.navigate(
                        MisPatitasDestinations.vaccineForm(petId,petName, id)
                    )
                }
            )
        }

        // Lista de turnos
        composable(
            route = MisPatitasDestinations.APPOINTMENT_LIST,
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
                isVeterinario = true,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { appointmentId ->
                    navController.navigate(MisPatitasDestinations.appointmentForm(petId, petName, appointmentId))},
                onNavigateToForm = {
                    navController.navigate(MisPatitasDestinations.appointmentForm(petId, petName))
                },
                onNavigateToDetail = { appointmentId ->
                    navController.navigate(MisPatitasDestinations.appointmentDetail(appointmentId, petId, petName))
                }
            )
        }

        // Formulario de turnos
        composable(
            route = MisPatitasDestinations.APPOINTMENT_FORM,
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

            val appointmentViewModel: AppointmentViewModel = hiltViewModel()
            AppointmentFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                appointmentId = appointmentId,
                appointmentViewModel = appointmentViewModel,
                onSave = { appointment ->
                    val appointmentConPetId = appointment.copy(petId = petId)
                    if(appointmentId == null) {
                        appointmentViewModel.insertAppointment(appointmentConPetId)
                    } else {
                        appointmentViewModel.updateAppointment(appointmentConPetId)
                    }
                }
            )
        }

        // Turnos detalle
        composable(
            route = MisPatitasDestinations.APPOINTMENT_DETAIL,
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
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { id ->
                    navController.navigate(
                        MisPatitasDestinations.appointmentForm(petId, petName, id)
                    )
                }
            )
        }

        // Lista de medicamentos
        composable(
            route = MisPatitasDestinations.MEDICATION_LIST,
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
                isVeterinario = true,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { medicationId ->
                    navController.navigate(
                        MisPatitasDestinations.medicationForm(
                            petId,
                            petName,
                            medicationId
                        )
                    )
                },
                onNavigateToForm = {
                    navController.navigate(
                        MisPatitasDestinations.medicationForm(petId, petName)
                    )
                },
                onNavigateToDetail = { medicationId ->
                    navController.navigate(
                        MisPatitasDestinations.medicationDetail(medicationId, petId, petName)
                    )
                }
            )
        }
        // Formulario de medicamentos
        composable(
            route = MisPatitasDestinations.MEDICATION_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType },
                navArgument("petName") { type = NavType.StringType },
                navArgument("medicationId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val petName = URLDecoder.decode(
                backStackEntry.arguments?.getString("petName") ?: "", "UTF-8"
            )
            val medicationId = backStackEntry.arguments?.getLong("medicationId")
                ?.takeIf { it != -1L }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(
                    MisPatitasDestinations.medicationList(petId, petName)
                )
            }
            val medicationViewModel: MedicationViewModel = hiltViewModel(parentEntry)

            MedicationFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                medicationId = medicationId,
                medicationViewModel = medicationViewModel,
                onSave = { medication ->
                    val medicationConPetId = medication.copy(petId = petId)
                    if (medicationId == null) {
                        medicationViewModel.insertMedication(medicationConPetId, petName)
                    } else {
                        medicationViewModel.updateMedication(medicationConPetId, petName)
                    }
                }
            )
        }

        // Detalle de Medicacion
        composable(
            route = MisPatitasDestinations.MEDICATION_DETAIL,
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
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { id  ->
                    navController.navigate(
                        MisPatitasDestinations.medicationForm(petId, petName, id)
                    )
                }
            )
        }

        // Lista de weighst
        composable(
            route = MisPatitasDestinations.WEIGHT_LIST,
            arguments = listOf(
                navArgument("petId") {type = NavType.LongType}
            )
        ) {backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            WeightScreen(
                petId = petId,
                isVeterinario = true,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToForm = { navController.navigate(MisPatitasDestinations.weightForm(petId)) },
                onNavigateToEdit = { weightId ->
                    navController.navigate(
                        MisPatitasDestinations.weightForm(petId, weightId)
                    )
                }
            )
        }

        // Formulario de weights
        composable(
            route = MisPatitasDestinations.WEIGHT_FORM,
            arguments = listOf(
                navArgument("petId") { type = NavType.LongType },
                navArgument("weightId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong("petId") ?: return@composable
            val weightId = backStackEntry.arguments?.getLong("weightId")
                ?.takeIf { it != -1L }
            val weightViewModel: WeightViewModel = hiltViewModel()

            WeightFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                weightId = weightId,
                weightViewModel = weightViewModel,
                onSave = { weight ->
                    val weightConPetId = weight.copy(petId = petId)
                    if (weightId == null) {
                        weightViewModel.insertWeight(weightConPetId)
                    } else {
                        weightViewModel.updateWeight(weightConPetId)
                    }
                }
            )
        }

        // Lista de condiciones
        composable(
            route = MisPatitasDestinations.CONDITION_LIST,
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
                isVeterinario = true,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToForm = {
                    navController.navigate(MisPatitasDestinations.conditionForm(petId, petName))
                },
                onNavigateToEdit = { conditionId ->
                    navController.navigate(
                        MisPatitasDestinations.conditionForm(petId, petName, conditionId)
                    )},
                onNavigateToDetail = { conditionId ->
                    navController.navigate(
                        MisPatitasDestinations.conditionDetail(conditionId)
                    )
                }
            )
        }

        // Formulario de condiciones
        composable(
            route = MisPatitasDestinations.CONDITION_FORM,
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
                    MisPatitasDestinations.conditionList(petId, petName)
                )
            }
            val conditionViewModel: ConditionViewModel = hiltViewModel(parentEntry)

            ConditionFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                conditionId = conditionId,
                conditionViewModel = conditionViewModel,
                onSave = { condition ->
                    val conditionConPetId = condition.copy(petId = petId)
                    if (conditionId == null) {
                        conditionViewModel.insertCondition(conditionConPetId)
                    } else {
                        conditionViewModel.updateCondition(conditionConPetId)
                    }
                }
            )
        }

        // Condition Detail Screen
        composable(
            route = MisPatitasDestinations.CONDITION_DETAIL,
            arguments = listOf(
                navArgument("conditionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val conditionId = backStackEntry.arguments?.getLong("conditionId") ?: return@composable
            ConditionDetailScreen(
                conditionId = conditionId,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { id ->
                    // TODO: navegar a editar condición
                }
            )
        }

        // Lista desparasitacion
        composable(
            route = MisPatitasDestinations.DEWORMING_LIST,
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
                isVeterinario = true,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToForm = {
                    navController.navigate(MisPatitasDestinations.dewormingForm(petId, petName))
                },
                onNavigateToEdit = { dewormingId ->
                    navController.navigate(
                        MisPatitasDestinations.dewormingForm(petId, petName, dewormingId)
                    )
                },
                onNavigateToDetail = { dewormingId ->
                    navController.navigate(
                        MisPatitasDestinations.dewormingDetail(dewormingId)
                    )
                }
            )
        }

        // Formulario desparasitacion
        composable(
            route = MisPatitasDestinations.DEWORMING_FORM,
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
                    MisPatitasDestinations.dewormingList(petId, petName)
                )
            }
            val dewormingViewModel: DewormingViewModel = hiltViewModel(parentEntry)
            DewormingFormScreen(
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                dewormingId = dewormingId,
                dewormingViewModel = dewormingViewModel,
                onSave = { deworming ->
                    val dewormingConPetId = deworming.copy(petId = petId)
                    if (dewormingId == null){
                        dewormingViewModel.insertDeworming(dewormingConPetId)
                    } else {
                        dewormingViewModel.updateDeworming(dewormingConPetId)
                    }
                }
            )
        }

        // Deworming Detail Screen
        composable(
            route = MisPatitasDestinations.DEWORMING_DETAIL,
            arguments = listOf(
                navArgument("dewormingId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val dewormingId = backStackEntry.arguments?.getLong("dewormingId") ?: return@composable
            DewormingDetailScreen(
                dewormingId = dewormingId,
                onNavigateBack = {
                    if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { id ->

                }
            )
        }
    }
}

