package com.henry.androidarchitecture.ui.detail

import androidx.lifecycle.ViewModel
import com.henry.androidarchitecture.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(private val noteRepo: NoteRepository) : ViewModel() {
}