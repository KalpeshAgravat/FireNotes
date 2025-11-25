package com.andy.note.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false // Set to true if you want to track schema versions
)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
}
