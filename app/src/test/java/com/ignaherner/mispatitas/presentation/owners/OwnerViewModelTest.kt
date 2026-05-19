package com.ignaherner.mispatitas.presentation.owners

import app.cash.turbine.test
import com.ignaherner.mispatitas.data.repository.OwnerRepository
import com.ignaherner.mispatitas.domain.model.Owner
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OwnerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: OwnerRepository
    private lateinit var viewModel: OwnerViewModel

    companion object {
        val ownerDePrueba = Owner(
            id = 1L,
            nombre = "Juan",
            apellido = "García",
            telefono = "1134567890",
            email = "juan@email.com",
            ciudad = "Buenos Aires",
            direccion = "Av. Corrientes 1234"
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando el repository devuelve un owner, el estado es Success`() = runTest {
        // Arrange: el repository devuelve un owner válido
        coEvery { repository.getOwner() } returns ownerDePrueba
        coEvery { repository.ownerExists() } returns true

        // Act: crear el ViewModel dispara el init, que llama checkOwnerExists() y loadOwner()
        viewModel = OwnerViewModel(repository)

        // Assert: el estado debe ser Success con el owner de prueba
        viewModel.ownerState.test {
            assertEquals(OwnerState.Success(ownerDePrueba), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cuando el repository devuelve null, el estado es Empty`() = runTest {
        // Arrange: el repository no tiene ningún owner guardado
        coEvery { repository.getOwner() } returns null
        coEvery { repository.ownerExists() } returns false

        // Act: crear el ViewModel dispara el init
        viewModel = OwnerViewModel(repository)

        // Assert: el estado debe ser Empty
        viewModel.ownerState.test {
            assertEquals(OwnerState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertOwner actualiza el estado a Success y muestra snackbar`() = runTest {
        // Arrange: init con perfil vacío; insertOwner retorna éxito
        coEvery { repository.getOwner() } returns null
        coEvery { repository.ownerExists() } returns false
        coEvery { repository.insertOwner(any()) } returns Result.success(Unit)

        viewModel = OwnerViewModel(repository)

        // Act: insertar un owner nuevo
        viewModel.insertOwner(ownerDePrueba)

        // Assert: el estado es Success con el owner insertado y el snackbar muestra el mensaje correcto
        assertEquals(OwnerState.Success(ownerDePrueba), viewModel.ownerState.value)
        assertEquals("Perfil creado", viewModel.snackbarMessage.value)
    }

    @Test
    fun `cuando insertOwner falla, muestra snackbar de error`() = runTest {
        // Arrange: init con perfil vacío; insertOwner lanza una excepción
        coEvery { repository.getOwner() } returns null
        coEvery { repository.ownerExists() } returns false
        coEvery { repository.insertOwner(any()) } throws Exception("Error de red")

        viewModel = OwnerViewModel(repository)

        // Act: intentar insertar un owner cuando el repository falla
        viewModel.insertOwner(ownerDePrueba)

        // Assert: safeLaunch captura la excepción y muestra el mensaje de error configurado
        assertEquals("Error al crear el perfil", viewModel.snackbarMessage.value)
    }
}
