package com.andy.note.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.andy.note.ui.auth.login.LoginScreen
import com.andy.note.ui.auth.register.RegisterScreen
import com.andy.note.ui.notes.AddEditNoteScreen
import com.andy.note.ui.notes.NotesScreen
import com.andy.note.ui.profile.ProfileScreen

@Composable
fun FireNotesNavGraph(
    navController: NavHostController,
    startDestination: Screen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Login Screen
        composable<Screen.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register) },
                onLoginSuccess = {
                    navController.navigate(Screen.NoteList) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        // 2. Register Screen
        composable<Screen.Register> {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.NoteList) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                }
            )
        }

        // 3. Note List Screen (We will build this next)
        composable<Screen.NoteList> {
            // Placeholder: Text("Notes Screen")
        }

        // 4. Add/Edit Note Screen (We will build this next)
        composable<Screen.AddEditNote> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.AddEditNote>()
            // Placeholder: Text("Add Note Screen")
        }

        // ... Login & Register Composables above ...

        // 3. Note List Screen
        composable<Screen.NoteList> {
            NotesScreen(
                onNavigateToAddNote = { navController.navigate(Screen.AddEditNote(noteId = null)) },
                onNavigateToEditNote = { noteId -> navController.navigate(Screen.AddEditNote(noteId = noteId)) },
                onNavigateToProfile = {navController.navigate(Screen.Profile)},
            )
        }

        // 4. Add/Edit Note Screen
        composable<Screen.AddEditNote> { backStackEntry ->
            // The ViewModel uses SavedStateHandle to read this "noteId" automatically
            // because the Screen.AddEditNote data class has a property named `noteId`.
            // No extra code needed here, just ensuring the screen is called.

            AddEditNoteScreen(
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable<Screen.Profile> {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogoutSuccess = {
                    // Clear backstack and go to Login
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

    }




}
