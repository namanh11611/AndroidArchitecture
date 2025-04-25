package com.henry.androidarchitecture.ui.list

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
class NoteListViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState = _uiState.asStateFlow()

    // Pagination state
    private val _currentPage = MutableStateFlow(0)
    private val _pageSize = MutableStateFlow(20)

    // All notes from repository
    private val allNotes = noteRepo.getNotesStream()

    init {
        viewModelScope.launch {
            allNotes.collect { resultState ->
                when (resultState) {
                    is ResultState.Success -> {
                        val allNotesList = resultState.data
                        val endIndex =
                            minOf((_currentPage.value + 1) * _pageSize.value, allNotesList.size)
                        _uiState.update { currentState ->
                            currentState.copy(
                                notes = allNotesList.take(endIndex),
                                hasMoreData = endIndex < allNotesList.size,
                                isLoading = false
                            )
                        }
                    }

                    is ResultState.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                error = resultState.message,
                                isLoading = false
                            )
                        }
                    }

                    is ResultState.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order, showSortMenu = false) }
    }

    fun toggleSortMenu() {
        _uiState.update { it.copy(showSortMenu = !it.showSortMenu) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchNotes(query)
    }

    fun setSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
    }

    fun setGridLayout(useGrid: Boolean) {
        _uiState.update { it.copy(useGridLayout = useGrid) }
    }

    fun searchNotes(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(filteredNotes = emptyList()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = noteRepo.searchNotes(query)

                if (result is ResultState.Success) {
                    _uiState.update {
                        it.copy(
                            filteredNotes = result.data,
                            isLoading = false
                        )
                    }
                } else if (result is ResultState.Error) {
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                noteRepo.refreshNotes()

                // Reset pagination when refreshing
                _currentPage.value = 0
                _uiState.update {
                    it.copy(
                        hasMoreData = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadMoreNotes() {
        if (_uiState.value.isLoading || !_uiState.value.hasMoreData) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate network delay for smoother UX
            kotlinx.coroutines.delay(300)
            _currentPage.value = _currentPage.value + 1
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            try {
                noteRepo.deleteNote(noteId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun sortNotes(notes: List<Note>): List<Note> {
        return when (_uiState.value.sortOrder) {
            SortOrder.TITLE_ASC -> notes.sortedBy { it.title }
            SortOrder.DATE_ASC -> notes.sortedBy { it.dateStamp }
            SortOrder.DATE_DESC -> notes.sortedByDescending { it.dateStamp }
        }
    }

    fun setPageSize(size: Int) {
        if (size > 0 && size != _pageSize.value) {
            _pageSize.value = size
            // Reset pagination when changing page size
            _currentPage.value = 0
            _uiState.update { it.copy(hasMoreData = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
