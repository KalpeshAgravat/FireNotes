package com.andy.note.ui.auth

// 1. Common State (can be shared or kept separate per screen)
// We will use separate states for clarity as used in the ViewModels above.

// --- LOGIN SPECIFIC ---

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    data class EnteredEmail(val value: String) : LoginEvent()
    data class EnteredPassword(val value: String) : LoginEvent()
    object Login : LoginEvent()
}

// --- REGISTER SPECIFIC ---

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RegisterEvent {
    data class EnteredEmail(val value: String) : RegisterEvent()
    data class EnteredPassword(val value: String) : RegisterEvent()
    object Register : RegisterEvent()
}
