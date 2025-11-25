package com.andy.note.ui.notes

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.note.domain.usecase.DeleteNoteUseCase
import com.andy.note.domain.usecase.GetNotesUseCase
import com.andy.note.domain.usecase.SyncNotesUseCase // Import this
import com.andy.note.domain.usecase.SyncRealtimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val syncNotesUseCase: SyncNotesUseCase ,
    private val syncRealtimeUseCase: SyncRealtimeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    init {
        getNotes()
        // Trigger background sync on init
        syncPendingNotes()
        startRealtimeSync()
    }

    // ... existing onEvent ...
    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    deleteNoteUseCase(event.note.id)
                }
            }
            is NotesEvent.Refresh -> {
                refreshNotes()
            }
        }
    }

    private fun syncPendingNotes() {
        viewModelScope.launch {
            syncNotesUseCase()
        }
    }


    private fun refreshNotes() {
        // Prevent multiple refreshes
        if (state.value.isRefreshing) return

        viewModelScope.launch {
            // 1. Start Loading
            _state.value = state.value.copy(isRefreshing = true)

            try {
                // 2. Attempt Sync
                syncNotesUseCase()
            } catch (e: Exception) {
                e.printStackTrace() // Log error
            } finally {
                // 3. ALWAYS Stop Loading (even if error occurs)
                _state.value = state.value.copy(isRefreshing = false)
            }
        }
    }

    private fun getNotes() {
        // ... existing getNotes logic ...
        _state.value = state.value.copy(isLoading = true)

        getNotesUseCase()
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    isLoading = false,
                    error = null
                )
            }
            .catch { e ->
                _state.value = state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    private fun startRealtimeSync() {
        syncRealtimeUseCase()
            .catch { e ->
                e.printStackTrace()
            }
            .launchIn(viewModelScope)
    }
}
