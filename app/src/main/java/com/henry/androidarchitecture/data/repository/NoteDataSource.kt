package com.henry.androidarchitecture.data.repository

import com.henry.androidarchitecture.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteDataSource {

    fun getNotesStream(): Flow<ResultState<List<Note>>>

    suspend fun getNotes(): ResultState<List<Note>>

    fun getNoteStream(noteId: Int): Flow<ResultState<Note>>

    suspend fun getNote(noteId: Int): ResultState<Note>

    suspend fun searchNotes(query: String): ResultState<List<Note>>

    suspend fun saveNote(note: Note)

    suspend fun deleteAllNotes()

    suspend fun deleteNote(noteId: Int)
}