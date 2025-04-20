package com.henry.androidarchitecture.data.repository

import com.henry.androidarchitecture.data.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface NoteRepository {
    fun getNotesStream(): Flow<ResultState<List<Note>>>

    suspend fun getNotes(forceUpdate: Boolean = false): ResultState<List<Note>>

    suspend fun refreshNotes()

    fun getNoteStream(noteId: Int): Flow<ResultState<Note>>

    suspend fun getNote(noteId: Int, forceUpdate: Boolean = false): ResultState<Note>

    suspend fun saveNote(note: Note)

    suspend fun deleteAllNotes()

    suspend fun deleteNote(noteId: Int)
}
