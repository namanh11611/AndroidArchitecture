package com.henry.androidarchitecture.ui.detail

import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.ResultState

/**
 * Represents the UI state for the NoteDetailScreen
 */
data class NoteDetailUiState(
    // Note data
    val noteId: Int = 0,
    val title: String = "",
    val content: String = "",
    val dateStamp: String = "",
    
    // Loading and error states
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    
    // Dialog states
    val showDeleteConfirmation: Boolean = false,
    
    // Edit state
    val hasUnsavedChanges: Boolean = false
) {
    companion object {
        fun fromNote(note: Note): NoteDetailUiState {
            return NoteDetailUiState(
                noteId = note.id,
                title = note.title ?: "",
                content = note.content ?: "",
                dateStamp = note.dateStamp ?: ""
            )
        }
    }
    
    val isExistingNote: Boolean get() = noteId > 0
    val isTitleValid: Boolean get() = title.isNotBlank()
}