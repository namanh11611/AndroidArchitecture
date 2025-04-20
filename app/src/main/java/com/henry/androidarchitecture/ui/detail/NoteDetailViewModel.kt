package com.henry.androidarchitecture.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteRepository
import com.henry.androidarchitecture.data.repository.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {
    
    private val _noteStream = MutableStateFlow<ResultState<Note>>(ResultState.Success(Note(0, null, null, null)))
    val noteStream: StateFlow<ResultState<Note>> = _noteStream
    
    private val _saveNoteState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val saveNoteState: StateFlow<ResultState<Unit>> = _saveNoteState
    
    private val _deleteNoteState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val deleteNoteState: StateFlow<ResultState<Unit>> = _deleteNoteState
    
    fun loadNote(noteId: Int) {
        if (noteId <= 0) {
            // This is a new note
            _noteStream.value = ResultState.Success(Note(id = 0, title = "", content = "", dateStamp = ""))
            return
        }
        
        viewModelScope.launch {
            try {
                _noteStream.value = noteRepo.getNote(noteId)
            } catch (e: Exception) {
                _noteStream.value = ResultState.Error(e.message)
            }
        }
    }
    
    fun saveNote(note: Note) {
        viewModelScope.launch {
            try {
                noteRepo.saveNote(note)
                _saveNoteState.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _saveNoteState.value = ResultState.Error(e.message)
            }
        }
    }
    
    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            try {
                noteRepo.deleteNote(noteId)
                _deleteNoteState.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _deleteNoteState.value = ResultState.Error(e.message)
            }
        }
    }
}