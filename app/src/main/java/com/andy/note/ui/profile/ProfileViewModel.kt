package com.andy.note.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.note.data.local.NoteDao
import com.andy.note.data.local.ThemePreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val email: String = "",
    val userId: String = "",
    val isDarkMode: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dao: NoteDao,
    private val auth: FirebaseAuth,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserProfile()

        // Observe theme changes
        viewModelScope.launch {
            themePreferences.isDarkMode.collect { isDark ->
                _state.value = _state.value.copy(isDarkMode = isDark)
            }
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        _state.value = _state.value.copy(
            email = user?.email ?: "No Email",
            userId = user?.uid ?: "No ID"
        )
    }

    fun onLogout() {
        viewModelScope.launch {
            // 2. Clear local data first
            dao.clearAllNotes()

            // 3. Then sign out from Firebase
            auth.signOut()
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.toggleTheme(isDark)
        }
    }
}