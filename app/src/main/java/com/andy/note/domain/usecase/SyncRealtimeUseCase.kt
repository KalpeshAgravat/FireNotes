package com.andy.note.domain.usecase

import com.andy.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncRealtimeUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<Unit> {
        return repository.syncRealtime()
    }
}
