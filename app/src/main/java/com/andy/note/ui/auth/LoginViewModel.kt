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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredEmail -> {
                _state.value = state.value.copy(email = event.value)
            }
            is LoginEvent.EnteredPassword -> {
                _state.value = state.value.copy(password = event.value)
            }
            is LoginEvent.Login -> {
                // Prevent multiple clicks while loading
                if (state.value.isLoading) return

                viewModelScope.launch {
                    // 1. Logic: Trim inputs
                    val email = state.value.email.trim()
                    val password = state.value.password.trim()

                    // 2. Validation
                    if (email.isBlank() || password.isBlank()) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Please fill in all fields"))
                        return@launch
                    }

                    // 3. Start Loading
                    _state.value = state.value.copy(isLoading = true)

                    // 4. Attempt Login
                    val result = authRepository.login(email, password)

                    // 5. Stop Loading
                    _state.value = state.value.copy(isLoading = false)

                    // 6. Handle Result
                    result.fold(
                        onSuccess = {
                            _eventFlow.emit(UiEvent.LoginSuccess)
                        },
                        onFailure = { e ->
                            _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Login failed"))
                        }
                    )
                }
            }
        }
    }

    // One-time events sent to the UI
    sealed class UiEvent {
        object LoginSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
