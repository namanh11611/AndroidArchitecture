package com.henry.androidarchitecture.ui.list

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
class NoteListViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {
    
    val notes = noteRepo.getNotesStream()
    
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder
    
    private val _deleteNoteState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val deleteNoteState: StateFlow<ResultState<Unit>> = _deleteNoteState
    
    private val _refreshState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val refreshState: StateFlow<ResultState<Unit>> = _refreshState
    
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }
    
    fun refreshNotes() {
        viewModelScope.launch {
            try {
                noteRepo.refreshNotes()
                _refreshState.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _refreshState.value = ResultState.Error(e.message)
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
    
    fun sortNotes(notes: List<Note>): List<Note> {
        return when (_sortOrder.value) {
            SortOrder.TITLE_ASC -> notes.sortedBy { it.title }
            SortOrder.DATE_ASC -> notes.sortedBy { it.dateStamp }
            SortOrder.DATE_DESC -> notes.sortedByDescending { it.dateStamp }
        }
    }
}
