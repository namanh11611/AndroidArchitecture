package com.henry.androidarchitecture.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteRepository
import com.henry.androidarchitecture.data.repository.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _saveNoteState = MutableStateFlow<ResultState<Unit>>(ResultState.Success(Unit))
    val saveNoteState: StateFlow<ResultState<Unit>> = _saveNoteState

    fun loadNote(noteId: Int, onLoaded: (Note) -> Unit) {
        viewModelScope.launch {
            when (val result = noteRepository.getNote(noteId)) {
                is ResultState.Success -> onLoaded(result.data)
                is ResultState.Error -> {
                    // Handle error, could show a message
                }
                else -> {
                    // Handle loading or empty state
                }
            }
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.saveNote(note)
                _saveNoteState.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _saveNoteState.value = ResultState.Error(e.message)
            }
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(noteId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Helper function to insert text format at cursor position
    fun applyFormat(currentText: String, format: NoteFormat, cursorPosition: Int): String {
        return when (format) {
            NoteFormat.BOLD -> insertMarkdown(currentText, "**", "**", cursorPosition)
            NoteFormat.ITALIC -> insertMarkdown(currentText, "*", "*", cursorPosition)
            NoteFormat.BULLET_LIST -> {
                val lines = currentText.split("\n")
                lines.mapIndexed { index, line ->
                    if (index == 0 || cursorPosition <= currentText.indexOf(line)) {
                        "- $line"
                    } else {
                        line
                    }
                }.joinToString("\n")
            }
            NoteFormat.NUMBERED_LIST -> {
                val lines = currentText.split("\n")
                lines.mapIndexed { index, line ->
                    if (index == 0 || cursorPosition <= currentText.indexOf(line)) {
                        "${index + 1}. $line"
                    } else {
                        line
                    }
                }.joinToString("\n")
            }
        }
    }

    private fun insertMarkdown(text: String, prefix: String, suffix: String, cursorPosition: Int): String {
        if (text.isEmpty()) return "$prefix$suffix"
        
        val beforeCursor = text.substring(0, cursorPosition)
        val afterCursor = text.substring(cursorPosition)
        
        return "$beforeCursor$prefix$suffix$afterCursor"
    }
}

enum class NoteFormat {
    BOLD,
    ITALIC,
    BULLET_LIST,
    NUMBERED_LIST
}