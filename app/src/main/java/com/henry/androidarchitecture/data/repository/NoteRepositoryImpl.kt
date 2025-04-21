package com.henry.androidarchitecture.data.repository

import com.henry.androidarchitecture.data.local.NoteLocalDataSource
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.remote.NoteRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteRemoteDataSource: NoteRemoteDataSource,
    private val noteLocalDataSource: NoteLocalDataSource
) : NoteRepository {

    override fun getNotesStream(): Flow<ResultState<List<Note>>> {
        return noteLocalDataSource.getNotesStream()
    }

    override suspend fun getNotes(forceUpdate: Boolean): ResultState<List<Note>> {
        if (forceUpdate) {
            try {
                updateNotesFromRemoteDataSource()
            } catch (exception: Exception) {
                return ResultState.Error(exception.message)
            }
        }
        return noteLocalDataSource.getNotes()
    }

    override suspend fun refreshNotes() {
        updateNotesFromRemoteDataSource()
    }

    override fun getNoteStream(noteId: Int): Flow<ResultState<Note>> {
        return noteLocalDataSource.getNoteStream(noteId)
    }

    override suspend fun getNote(noteId: Int, forceUpdate: Boolean): ResultState<Note> {
        if (forceUpdate) {
            try {
                updateNoteFromRemoteDataSource(noteId)
            } catch (exception: Exception) {
                return ResultState.Error(exception.message)
            }
        }
        return noteLocalDataSource.getNote(noteId)
    }
    
    override suspend fun searchNotes(query: String): ResultState<List<Note>> {
        return noteLocalDataSource.searchNotes(query)
    }

    override suspend fun saveNote(note: Note) {
        noteLocalDataSource.saveNote(note)
    }

    override suspend fun deleteAllNotes() {
        noteLocalDataSource.deleteAllNotes()
    }

    override suspend fun deleteNote(noteId: Int) {
        noteLocalDataSource.deleteNote(noteId)
    }

    private suspend fun updateNotesFromRemoteDataSource() {
        val remoteNotes = noteRemoteDataSource.getNotes()
        if (remoteNotes is ResultState.Success) {
            noteLocalDataSource.deleteAllNotes()
            noteLocalDataSource.saveAllNotes(remoteNotes.data)
        } else if (remoteNotes is ResultState.Error) {
            throw Exception(remoteNotes.message)
        }
    }

    private suspend fun updateNoteFromRemoteDataSource(noteId: Int) {
        val remoteNote = noteRemoteDataSource.getNote(noteId)
        if (remoteNote is ResultState.Success) {
            noteLocalDataSource.saveNote(remoteNote.data)
        }
    }
}