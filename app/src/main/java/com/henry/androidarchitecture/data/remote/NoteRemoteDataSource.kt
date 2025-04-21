package com.henry.androidarchitecture.data.remote

import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteDataSource
import com.henry.androidarchitecture.data.repository.ResultState
import com.henry.androidarchitecture.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRemoteDataSource @Inject constructor(
    @DispatcherIO private val dispatcherIO: CoroutineDispatcher
) : NoteDataSource {
    override fun getNotesStream(): Flow<ResultState<List<Note>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotes(): ResultState<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun getNoteStream(noteId: Int): Flow<ResultState<Note>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNote(noteId: Int): ResultState<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun searchNotes(query: String): ResultState<List<Note>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveNote(note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllNotes() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(noteId: Int) {
        TODO("Not yet implemented")
    }
}
