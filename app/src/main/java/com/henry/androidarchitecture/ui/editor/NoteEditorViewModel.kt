package com.henry.androidarchitecture.ui.editor

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState = _uiState.asStateFlow()

    fun loadNote(noteId: Int) {
        if (noteId <= 0) return
        
        _uiState.update { it.copy(isLoading = true, noteId = noteId) }
        
        viewModelScope.launch {
            when (val result = noteRepository.getNote(noteId)) {
                is ResultState.Success -> {
                    _uiState.update { 
                        NoteEditorUiState.fromNote(result.data).copy(isLoading = false) 
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { 
                        it.copy(error = result.message, isLoading = false) 
                    }
                }
                else -> {
                    // Handle loading state (already set above)
                }
            }
        }
    }

    fun saveNote() {
        val currentState = _uiState.value
        
        if (!currentState.isTitleValid) {
            _uiState.update { it.copy(error = "Please enter a title") }
            return
        }
        
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        val note = Note(
            id = currentState.noteId,
            title = currentState.title,
            content = currentState.content,
            dateStamp = formattedDate
        )
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                noteRepository.saveNote(note)
                _uiState.update { 
                    it.copy(isSaved = true, isLoading = false, dateStamp = formattedDate) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message, isLoading = false) 
                }
            }
        }
    }

    fun deleteNote() {
        val noteId = _uiState.value.noteId
        if (noteId <= 0) return
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(noteId)
                _uiState.update { 
                    it.copy(isLoading = false, isSaved = true) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message, isLoading = false) 
                }
            }
        }
    }

    // Update UI state functions
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun updateCursorPosition(position: Int) {
        _uiState.update { it.copy(cursorPosition = position) }
    }
    
    fun toggleFormatBold() {
        _uiState.update { it.copy(formatBold = !it.formatBold) }
    }
    
    fun toggleFormatItalic() {
        _uiState.update { it.copy(formatItalic = !it.formatItalic) }
    }
    
    fun toggleFormatBulletList() {
        _uiState.update { 
            it.copy(
                formatBulletList = !it.formatBulletList,
                formatNumberedList = if (!it.formatBulletList) false else it.formatNumberedList
            ) 
        }
    }
    
    fun toggleFormatNumberedList() {
        _uiState.update { 
            it.copy(
                formatNumberedList = !it.formatNumberedList,
                formatBulletList = if (!it.formatNumberedList) false else it.formatBulletList
            ) 
        }
    }
    
    fun showDiscardDialog(show: Boolean) {
        _uiState.update { it.copy(showDiscardDialog = show) }
    }
    
    fun showDeleteDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
