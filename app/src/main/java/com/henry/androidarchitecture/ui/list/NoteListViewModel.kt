package com.henry.androidarchitecture.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteRepository
import com.henry.androidarchitecture.data.repository.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {
    
    // All notes from repository
    private val allNotes = noteRepo.getNotesStream()
    
    // Search results
    private val _searchResults = MutableStateFlow<ResultState<List<Note>>>(ResultState.Success(emptyList()))
    val searchResults: StateFlow<ResultState<List<Note>>> = _searchResults
    
    // Current search query
    private val _currentSearchQuery = MutableStateFlow<String?>(null)
    val currentSearchQuery: StateFlow<String?> = _currentSearchQuery
    
    // Pagination state
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage
    
    private val _pageSize = MutableStateFlow(20)
    val pageSize: StateFlow<Int> = _pageSize
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData
    
    // Paginated notes
    val notes = allNotes.map { resultState ->
        when (resultState) {
            is ResultState.Success -> {
                val allNotesList = resultState.data
                val endIndex = minOf((_currentPage.value + 1) * _pageSize.value, allNotesList.size)
                _hasMoreData.value = endIndex < allNotesList.size
                
                ResultState.Success(allNotesList.take(endIndex))
            }
            is ResultState.Error -> resultState
            is ResultState.Loading -> resultState
        }
    }
    
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder
    
    private val _deleteNoteState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val deleteNoteState: StateFlow<ResultState<Unit>> = _deleteNoteState
    
    private val _refreshState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val refreshState: StateFlow<ResultState<Unit>> = _refreshState
    
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }
    
    fun searchNotes(query: String) {
        if (query.isBlank()) {
            _currentSearchQuery.value = null
            _searchResults.value = ResultState.Success(emptyList())
            return
        }
        
        _currentSearchQuery.value = query
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = noteRepo.searchNotes(query)
                _searchResults.value = result
            } catch (e: Exception) {
                _searchResults.value = ResultState.Error(e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshNotes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                noteRepo.refreshNotes()
                _refreshState.value = ResultState.Success(Unit)
                // Reset pagination when refreshing
                _currentPage.value = 0
                _hasMoreData.value = true
            } catch (e: Exception) {
                _refreshState.value = ResultState.Error(e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadMoreNotes() {
        if (_isLoading.value || !_hasMoreData.value) return
        
        viewModelScope.launch {
            _isLoading.value = true
            // Simulate network delay for smoother UX
            kotlinx.coroutines.delay(300)
            _currentPage.value = _currentPage.value + 1
            _isLoading.value = false
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
    
    fun sortNotes(notes: List<Note>): List<Note> {
        return when (_sortOrder.value) {
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
            _hasMoreData.value = true
        }
    }
}
