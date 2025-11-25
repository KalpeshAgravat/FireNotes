package com.andy.note.domain.model

data class Note(
    val id: String = "", // UUID for local, Document ID for Firestore
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false // Key for offline-first logic
)

