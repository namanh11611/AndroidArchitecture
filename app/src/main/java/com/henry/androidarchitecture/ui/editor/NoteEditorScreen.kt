package com.henry.androidarchitecture.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Int = 0,
    viewModel: NoteEditorViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNoteSaved: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(noteId > 0) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var formatBold by remember { mutableStateOf(false) }
    var formatItalic by remember { mutableStateOf(false) }
    var formatBulletList by remember { mutableStateOf(false) }
    var formatNumberedList by remember { mutableStateOf(false) }
    
    // Load note if editing existing note
    LaunchedEffect(key1 = noteId) {
        if (noteId > 0) {
            isLoading = true
            viewModel.loadNote(noteId) { note ->
                title = note.title ?: ""
                content = note.content ?: ""
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId > 0) "Edit Note" else "New Note") },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (title.isNotEmpty() || content.isNotEmpty()) {
                            showDiscardDialog = true
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (noteId > 0) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                        }
                    }
                    IconButton(onClick = { saveNote(noteId, title, content, viewModel, scope, snackbarHostState, onNoteSaved) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Note")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Formatting toolbar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { formatBold = !formatBold },
                            modifier = Modifier.background(
                                if (formatBold) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.FormatBold, contentDescription = "Bold")
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        IconButton(
                            onClick = { formatItalic = !formatItalic },
                            modifier = Modifier.background(
                                if (formatItalic) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        Divider(modifier = Modifier
                            .height(24.dp)
                            .width(1.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(
                            onClick = { 
                                formatBulletList = !formatBulletList
                                if (formatBulletList) formatNumberedList = false
                            },
                            modifier = Modifier.background(
                                if (formatBulletList) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List")
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        IconButton(
                            onClick = { 
                                formatNumberedList = !formatNumberedList
                                if (formatNumberedList) formatBulletList = false
                            },
                            modifier = Modifier.background(
                                if (formatNumberedList) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = "Numbered List")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(300.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    )
                )
            }
        }
    }
    
    // Discard changes dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onBackClick()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep editing")
                }
            }
        )
    }
    
    // Delete note dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete note?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(noteId)
                    scope.launch {
                        snackbarHostState.showSnackbar("Note deleted")
                    }
                    showDeleteDialog = false
                    onBackClick()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun saveNote(
    noteId: Int,
    title: String,
    content: String,
    viewModel: NoteEditorViewModel,
    scope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onNoteSaved: () -> Unit
) {
    if (title.isBlank()) {
        scope.launch {
            snackbarHostState.showSnackbar("Please enter a title")
        }
        return
    }
    
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val note = Note(
        id = noteId,
        title = title,
        content = content,
        dateStamp = formattedDate
    )
    
    viewModel.saveNote(note)
    scope.launch {
        snackbarHostState.showSnackbar("Note saved")
    }
    onNoteSaved()
}

@Preview(showBackground = true)
@Composable
fun NoteEditorScreenPreview() {
    AndroidArchitectureTheme {
        NoteEditorPreviewContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteEditorPreviewContent() {
    var title by remember { mutableStateOf("Meeting Notes") }
    var content by remember { mutableStateOf("Discuss project timeline and resource allocation. Follow up with team about progress.") }
    
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
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Note")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Check, contentDescription = "Save Note")
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Formatting toolbar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold")
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    Divider(modifier = Modifier
                        .height(24.dp)
                        .width(1.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List")
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = "Numbered List")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(300.dp)
            )
        }
    }
}