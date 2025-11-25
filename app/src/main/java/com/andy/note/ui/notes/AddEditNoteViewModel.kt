package com.andy.note.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.note.domain.model.Note
import com.andy.note.domain.repository.NoteRepository
import com.andy.note.domain.usecase.AddNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase,
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditNoteState())
    val state: StateFlow<AddEditNoteState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: String? = null

    init {
        savedStateHandle.get<String>("noteId")?.let { noteId ->
            if (noteId != "null") {
                viewModelScope.launch {
                    repository.getNoteById(noteId)?.also { note ->
                        currentNoteId = note.id
                        _state.value = state.value.copy(
                            title = note.title,
                            content = note.content
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _state.value = state.value.copy(title = event.value)
            }
            is AddEditNoteEvent.EnteredContent -> {
                _state.value = state.value.copy(content = event.value)
            }
            is AddEditNoteEvent.SaveNote -> {
                // 1. Prevent double-clicking
                if (state.value.isLoading) return

                viewModelScope.launch {
                    _state.value = state.value.copy(isLoading = true)

                    try {
                        val idToUse = currentNoteId ?: UUID.randomUUID().toString()

                        // This saves to Local DB (Room) instantly.
                        // If Firebase fails (no internet), the Repository should swallow the error,
                        // leaving the note as isSynced = false.
                        addNoteUseCase(
                            Note(
                                id = idToUse,
                                title = state.value.title,
                                content = state.value.content,
                                timestamp = System.currentTimeMillis(),
                                isSynced = false
                            )
                        )

                        // If we get here, Local Save worked.
                        // We assume success and navigate back immediately.
                        _eventFlow.emit(UiEvent.ShowSnackbar("Note Saved"))
                        _eventFlow.emit(UiEvent.SaveNote) // Triggers navigation

                    } catch (e: Exception) {
                        // This only happens if the LOCAL database fails (very rare).
                        // Network errors should be handled inside Repository and not thrown here.
                        _eventFlow.emit(UiEvent.ShowSnackbar(
                            message = "Error saving note: ${e.message}"
                        ))
                    } finally {
                        // ALWAYS turn off the loader
                        _state.value = state.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}
