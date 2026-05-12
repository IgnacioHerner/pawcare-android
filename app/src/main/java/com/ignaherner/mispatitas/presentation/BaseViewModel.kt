package com.ignaherner.mispatitas.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    protected fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    protected fun safeLaunch(
        onError: String = "Error inesperado",
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(NonCancellable) {
                    block()
                }
            } catch (e: Exception) {
                showSnackbar(onError)
            }
        }
    }
}
