package com.henry.androidarchitecture.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.henry.androidarchitecture.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun observeAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun observeNoteById(noteId: Int): Flow<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note

    @Query("SELECT * FROM note WHERE title LIKE :title LIMIT 1")
    suspend fun findByTitle(title: String): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Query("DELETE FROM note")
    suspend fun deleteAll()

    @Query("DELETE FROM note WHERE id = :noteId")
    suspend fun delete(noteId: Int)
}
