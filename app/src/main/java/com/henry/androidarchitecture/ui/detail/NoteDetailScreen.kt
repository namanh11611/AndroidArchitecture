package com.henry.androidarchitecture.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int,
    viewModel: NoteDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNoteSaved: () -> Unit,
    onNoteDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Load note details
    LaunchedEffect(key1 = noteId) {
        viewModel.loadNote(noteId)
    }
    
    // Handle saved state
    LaunchedEffect(key1 = uiState.isSaved) {
        if (uiState.isSaved) {
            scope.launch {
                snackbarHostState.showSnackbar("Note saved successfully")
            }
            onNoteSaved()
        }
    }
    
    // Handle deleted state
    LaunchedEffect(key1 = uiState.isDeleted) {
        if (uiState.isDeleted) {
            scope.launch {
                snackbarHostState.showSnackbar("Note deleted successfully")
            }
            onNoteDeleted()
        }
    }
    
    // Handle errors
    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isExistingNote) "Edit Note" else "Add Note") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isExistingNote) {
                        IconButton(onClick = { viewModel.showDeleteConfirmation(true) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                viewModel.saveNote(
                    Note(
                        id = uiState.noteId,
                        title = uiState.title,
                        content = uiState.content,
                        dateStamp = formattedDate
                    )
                )
            }) {
                Icon(Icons.Default.Save, contentDescription = "Save Note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            // Show error state only if we don't have a successful note load
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error loading note: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadNote(noteId) }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            // Success or Empty state - show editor
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    minLines = 5
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirmation(false) },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(uiState.noteId)
                        viewModel.showDeleteConfirmation(false)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteConfirmation(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailScreenPreview() {
    val sampleNote = Note(
        id = 1,
        title = "Meeting Notes",
        content = "Discuss project timeline and resource allocation. Follow up with team about progress.",
        dateStamp = "2023-06-12"
    )
    
    AndroidArchitectureTheme {
        NoteDetailPreviewContent(sampleNote)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDetailPreviewContent(note: Note) {
    var title by remember { mutableStateOf(note.title ?: "") }
    var content by remember { mutableStateOf(note.content ?: "") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Save, contentDescription = "Save Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 5
            )
        }
    }
}