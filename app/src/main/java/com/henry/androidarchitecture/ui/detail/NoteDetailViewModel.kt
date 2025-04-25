package com.henry.androidarchitecture.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteRepository
import com.henry.androidarchitecture.data.repository.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadNote(noteId: Int) {
        if (noteId <= 0) {
            // This is a new note
            val emptyNote = Note(id = 0, title = "", content = "", dateStamp = "")
            _uiState.update { NoteDetailUiState.fromNote(emptyNote) }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, noteId = noteId) }
        
        viewModelScope.launch {
            try {
                val result = noteRepo.getNote(noteId)
                
                when (result) {
                    is ResultState.Success -> {
                        _uiState.update { NoteDetailUiState.fromNote(result.data) }
                    }
                    is ResultState.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                    }
                    else -> { /* Loading state already set above */ }
                }
            } catch (e: Exception) {
                val errorMsg = e.message
                _uiState.update { it.copy(error = errorMsg, isLoading = false) }
            }
        }
    }
    
    fun saveNote(note: Note) {
        if (!_uiState.value.isTitleValid) {
            _uiState.update { it.copy(error = "Please enter a title") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                noteRepo.saveNote(note)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSaved = true,
                        hasUnsavedChanges = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                val errorMsg = e.message
                _uiState.update { it.copy(error = errorMsg, isLoading = false) }
            }
        }
    }
    
    fun deleteNote(noteId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                noteRepo.deleteNote(noteId)
                _uiState.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                val errorMsg = e.message
                _uiState.update { it.copy(error = errorMsg, isLoading = false) }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { 
            it.copy(title = title, hasUnsavedChanges = true) 
        }
    }
    
    fun updateContent(content: String) {
        _uiState.update { 
            it.copy(content = content, hasUnsavedChanges = true) 
        }
    }
    
    fun showDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmation = show) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}