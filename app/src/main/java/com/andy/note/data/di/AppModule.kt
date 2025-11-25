package com.andy.note.data.di

import android.app.Application
import androidx.room.Room
import com.andy.note.data.local.NoteDao
import com.andy.note.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            "firenotes_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: NoteDatabase): NoteDao {
        return db.noteDao
    }

   /* @Provides
    @Singleton
    fun provideFirebaseFirestore(): com.google.firebase.firestore.FirebaseFirestore {
        return com.google.firebase.firestore.FirebaseFirestore.getInstance()
    }*/

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): com.google.firebase.database.FirebaseDatabase {
        // "https://YOUR-PROJECT-ID-default-rtdb.firebaseio.com/"
        // Usually getInstance() is enough if you are using the default instance location.
        return com.google.firebase.database.FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): com.google.firebase.auth.FirebaseAuth {
        return com.google.firebase.auth.FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        dao: NoteDao,
        db: com.google.firebase.database.FirebaseDatabase, // Changed from Firestore
        auth: com.google.firebase.auth.FirebaseAuth
    ): com.andy.note.domain.repository.NoteRepository {
        return com.andy.note.data.repository.NoteRepositoryImpl(dao, db, auth)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: com.google.firebase.auth.FirebaseAuth
    ): com.andy.note.domain.repository.AuthRepository {
        return com.andy.note.data.repository.AuthRepositoryImpl(firebaseAuth)
    }
}
