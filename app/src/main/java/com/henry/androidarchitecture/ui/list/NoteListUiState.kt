package com.henry.androidarchitecture.ui.list

import com.henry.androidarchitecture.data.model.Note

/**
 * Represents the UI state for the NoteListScreen
 */
data class NoteListUiState(
    // Notes data
    val notes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreData: Boolean = true,
    val error: String? = null,
    
    // Search state
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    
    // Sort state
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val showSortMenu: Boolean = false,
    
    // Layout state
    val useGridLayout: Boolean = false
)
