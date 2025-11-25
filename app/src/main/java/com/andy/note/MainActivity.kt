package com.andy.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.andy.note.domain.repository.AuthRepository
import com.andy.note.ui.navigation.FireNotesNavGraph
import com.andy.note.ui.navigation.Screen
import com.andy.note.ui.theme.NoteTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use your AppTheme here if you have one created, otherwise MaterialTheme defaults are fine for now
            NoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Check if user is logged in to decide start screen
                    val startScreen = if (authRepository.currentUser != null) {
                        Screen.NoteList
                    } else {
                        Screen.Login
                    }

                    FireNotesNavGraph(
                        navController = navController,
                        startDestination = startScreen
                    )
                }
            }
        }
    }
}
