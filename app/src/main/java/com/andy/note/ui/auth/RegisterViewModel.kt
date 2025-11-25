package com.andy.note.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.note.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is RegisterEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is RegisterEvent.Register -> {
                if (state.value.isLoading) return

                viewModelScope.launch {
                    // 1. Logic: Trim inputs
                    val email = state.value.email.trim()
                    val password = state.value.password.trim()

                    // 2. Validation
                    if (email.isBlank() || password.isBlank()) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Fields cannot be empty"))
                        return@launch
                    }

                    // Optional: Add password length check if needed
                    if (password.length < 6) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Password must be at least 6 characters"))
                        return@launch
                    }

                    // 3. Start Loading
                    _state.value = state.value.copy(isLoading = true)

                    // 4. Attempt Register
                    val result = authRepository.register(email, password)

                    // 5. Stop Loading
                    _state.value = state.value.copy(isLoading = false)

                    // 6. Handle Result
                    result.fold(
                        onSuccess = {
                            _eventFlow.emit(UiEvent.RegisterSuccess)
                        },
                        onFailure = { e ->
                            _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Registration failed"))
                        }
                    )
                }
            }
        }
    }

    // One-time events sent to the UI
    sealed class UiEvent {
        object RegisterSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
