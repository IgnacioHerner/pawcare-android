package com.ignaherner.pawcare.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ignaherner.pawcare.presentation.pets.PetListScreen

object PawCareDestinations {
    const val PET_LIST = "pet_list"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val PET_FORM = "pet_form?petId={petId}"

    // Funciones para construir rutas con argumentos
    fun petDetail(petId: Long) = "pet_detail/$petId"
    fun petForm(petId: Long? = null) = if (petId != null) "pet_form?petId=$petId" else "pet_form"
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
            // PetDetailScreen(petId = petId) — lo agregamos en la próxima sesión
        }
    }
}

@Composable
fun PetFormScreen(petId: Long?, onNavigateBack: () -> Boolean) {
    TODO("Not yet implemented")
}