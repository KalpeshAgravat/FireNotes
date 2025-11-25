package com.andy.note.data.repository

import androidx.core.view.children
import com.andy.note.data.local.NoteDao
import com.andy.note.data.local.toEntity
import com.andy.note.domain.model.Note
import com.andy.note.domain.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    private val db: FirebaseDatabase,
    private val auth: FirebaseAuth
) : NoteRepository {

    private val notesRef
        get() = db.getReference("users")
            .child(auth.currentUser?.uid ?: "guest")
            .child("notes")

    override fun getNotes(): Flow<List<Note>> {
        return dao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return dao.getNoteById(id)?.toDomain()
    }

    override suspend fun insertNote(note: Note) {
        val entity = note.toEntity()
        dao.insertNote(entity) // Save locally first

        try {
            if (auth.currentUser != null) {
                notesRef.child(note.id).setValue(note).await()
                dao.insertNote(entity.copy(isSynced = true)) // Mark as synced
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteNote(id: String) {
        dao.deleteNoteById(id)
        try {
            if (auth.currentUser != null) {
                notesRef.child(id).removeValue().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This is for MANUAL refresh (Pull-to-Refresh).
     * It uploads local changes AND fetches remote changes.
     */
    override suspend fun syncNotes() {
        if (auth.currentUser == null) return

        // 1. Upload local unsynced notes first
        val unsyncedNotes = dao.getUnsyncedNotes()
        unsyncedNotes.forEach { entity ->
            try {
                val note = entity.toDomain()
                notesRef.child(note.id).setValue(note).await()
                dao.insertNote(entity.copy(isSynced = true))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Fetch all remote notes and merge them into the local DB
        fetchAndMergeRemoteNotes()
    }

    /**
     * This is for AUTOMATIC sync. It listens for any change on the server.
     */
    override fun syncRealtime(): Flow<Unit> = callbackFlow {
        if (auth.currentUser == null) {
            close(IllegalStateException("User not logged in"))
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // When data changes on the server, fetch and merge it.
                launch {
                    fetchAndMergeRemoteNotes(snapshot)
                    trySend(Unit) // Notify listener if needed
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        notesRef.addValueEventListener(listener)
        awaitClose { notesRef.removeEventListener(listener) }
    }

    /**
     * Helper function to get notes from Firebase and save them to Room.
     * Can be triggered by manual refresh or the realtime listener.
     */
    private suspend fun fetchAndMergeRemoteNotes(snapshot: DataSnapshot? = null) {
        try {
            val dataSnapshot = if (snapshot != null) {
                snapshot
            } else {
                // If user has no internet or DB is empty, await() might hang if not handled carefully.
                // However, for Realtime DB, get() is usually reliable.
                // Let's check if user is logged in first.
                if (auth.currentUser == null) return
                notesRef.get().await()
            }

            if (!dataSnapshot.exists()) return

            val remoteNotes = mutableListOf<Note>()

            dataSnapshot.children.forEach { child ->
                try {
                    // Manual mapping is safest
                    val id = child.child("id").getValue(String::class.java) ?: ""
                    val title = child.child("title").getValue(String::class.java) ?: ""
                    val content = child.child("content").getValue(String::class.java) ?: ""
                    val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L

                    if (id.isNotEmpty()) {
                        remoteNotes.add(Note(id, title, content, timestamp, isSynced = true))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Batch insert into Room
            remoteNotes.forEach { note ->
                dao.insertNote(note.toEntity().copy(isSynced = true))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // IMPORTANT: Even if it fails, we must not crash, allowing the ViewModel to finish "refreshing"
        }
    }
}
