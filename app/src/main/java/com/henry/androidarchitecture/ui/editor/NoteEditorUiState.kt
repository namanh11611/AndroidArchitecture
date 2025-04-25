package com.henry.androidarchitecture.ui.editor

import com.henry.androidarchitecture.data.model.Note

/**
 * Represents the UI state for the NoteEditorScreen
 */
data class NoteEditorUiState(
    // Note data
    val noteId: Int = 0,
    val title: String = "",
    val content: String = "",
    val dateStamp: String = "",
    
    // Loading and error states
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    
    // Dialog states
    val showDiscardDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    
    // Formatting states
    val formatBold: Boolean = false,
    val formatItalic: Boolean = false,
    val formatBulletList: Boolean = false,
    val formatNumberedList: Boolean = false,
    
    // Cursor position for formatting
    val cursorPosition: Int = 0
) {
    companion object {
        fun fromNote(note: Note): NoteEditorUiState {
            return NoteEditorUiState(
                noteId = note.id,
                title = note.title ?: "",
                content = note.content ?: "",
                dateStamp = note.dateStamp ?: ""
            )
        }
    }
    
    val isExistingNote: Boolean get() = noteId > 0
    val hasUnsavedChanges: Boolean get() = title.isNotEmpty() || content.isNotEmpty()
    val isTitleValid: Boolean get() = title.isNotBlank()
}