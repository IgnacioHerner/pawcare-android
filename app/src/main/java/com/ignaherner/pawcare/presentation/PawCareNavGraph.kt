package com.ignaherner.pawcare.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ignaherner.pawcare.presentation.pets.PetDetailScreen
import com.ignaherner.pawcare.presentation.pets.PetFormScreen
import com.ignaherner.pawcare.presentation.pets.PetListScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineFormScreen
import com.ignaherner.pawcare.presentation.vaccines.VaccineScreen

object PawCareDestinations {
    const val PET_LIST = "pet_list"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val PET_FORM = "pet_form?petId={petId}"

    const val VACCINE_LIST = "vaccine_list/{petId}"
    const val VACCINE_FORM = "vaccine_form/{petId}?vaccineId={vaccinId}"

    // Funciones para construir rutas con argumentos
    fun petDetail(petId: Long) = "pet_detail/$petId"
    fun petForm(petId: Long? = null) = if (petId != null) "pet_form?petId=$petId" else "pet_form"

    fun vaccineList(petId: Long) = "vaccine_list/$petId"
    fun vaccineForm(petId: Long, vaccineId: Long? = null) =
        if (vaccineId != null) "vaccine_form/$petId?vaccineId=$vaccineId" else "vaccine_form/$petId"
}

@Composable
fun PawCareNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = PawCareDestinations.PET_LIST
    ) {
        // Lista de mascotas
        composable(PawCareDestinations.PET_LIST) {
            PetListScreen(
                onNavigateToDetail = { petId ->
                    navController.navigate(PawCareDestinations.petDetail(petId))
                },
                onNavigateToForm = {
                    navController.navigate(PawCareDestinations.petForm())
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

        // Detalle - lo completamos cuando hagamos PetDetailScreen
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
                onNavigateToAppointments = { id ->
                    navController.navigate(PawCareDestinations.petDetail(id))
                },
                onNavigateToWeight = { id ->
                    navController.navigate(PawCareDestinations.petDetail(id))
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

    }
}
