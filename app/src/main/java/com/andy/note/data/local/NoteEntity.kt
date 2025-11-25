package com.andy.note.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andy.note.domain.model.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isSynced: Boolean
) {
    // Helper to convert Entity -> Domain
    fun toDomain(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            timestamp = timestamp,
            isSynced = isSynced
        )
    }
}

// Helper to convert Domain -> Entity
fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        timestamp = timestamp,
        isSynced = isSynced
    )
}
