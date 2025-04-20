package com.henry.androidarchitecture.ui.list

import androidx.lifecycle.ViewModel
import com.henry.androidarchitecture.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {
    val notes = noteRepo.getNotesStream()
}
