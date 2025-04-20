package com.henry.androidarchitecture.data.local

import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteDataSource
import com.henry.androidarchitecture.data.repository.ResultState
import com.henry.androidarchitecture.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteLocalDataSource @Inject constructor(
    private val noteDao: NoteDao,
    @DispatcherIO private val dispatcherIO: CoroutineDispatcher
) : NoteDataSource {
    override fun getNotesStream(): Flow<ResultState<List<Note>>> =
        noteDao.observeAllNotes().map {
            ResultState.Success(it)
        }

    override suspend fun getNotes(): ResultState<List<Note>> = withContext(dispatcherIO) {
        try {
            ResultState.Success(noteDao.getAllNotes())
        } catch (exception: Exception) {
            ResultState.Error(exception.message)
        }
    }

    override fun getNoteStream(noteId: Int): Flow<ResultState<Note>> {
        return noteDao.observeNoteById(noteId).map {
            ResultState.Success(it)
        }
    }

    override suspend fun getNote(noteId: Int): ResultState<Note> = withContext(dispatcherIO) {
        try {
            ResultState.Success(noteDao.getNoteById(noteId))
        } catch (exception: Exception) {
            ResultState.Error(exception.message)
        }
    }

    override suspend fun saveNote(note: Note) {
        noteDao.insert(note)
    }

    override suspend fun deleteAllNotes() {
        noteDao.deleteAll()
    }

    override suspend fun deleteNote(noteId: Int) {
        noteDao.delete(noteId)
    }

    suspend fun saveAllNotes(notes: List<Note>) {
        noteDao.insertAll(notes)
    }
}
