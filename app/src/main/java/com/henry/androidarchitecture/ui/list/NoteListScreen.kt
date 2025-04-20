package com.henry.androidarchitecture.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.henry.androidarchitecture.data.model.Note
import androidx.compose.ui.tooling.preview.Preview
import com.henry.androidarchitecture.data.repository.ResultState
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme

enum class SortOrder {
    TITLE_ASC,
    DATE_ASC,
    DATE_DESC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NoteListViewModel = hiltViewModel(),
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val notesState = viewModel.notes.collectAsState(initial = ResultState.Loading)
    var notes = emptyList<Note>()
    if (notesState.value is ResultState.Success) {
        notes = (notesState.value as ResultState.Success<List<Note>>).data
    }
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    val sortedNotes = when (sortOrder) {
        SortOrder.TITLE_ASC -> notes.sortedBy { it.title }
        SortOrder.DATE_ASC -> notes.sortedBy { it.dateStamp }
        SortOrder.DATE_DESC -> notes.sortedByDescending { it.dateStamp }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = !showSortMenu }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Title") },
                                onClick = { 
                                    sortOrder = SortOrder.TITLE_ASC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Date (Newest)") },
                                onClick = { 
                                    sortOrder = SortOrder.DATE_DESC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Date (Oldest)") },
                                onClick = { 
                                    sortOrder = SortOrder.DATE_ASC
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        if (sortedNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notes yet. Click the + button to add a note.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(sortedNotes) { note ->
                    NoteItem(
                        note = note,
                        onNoteClick = { onNoteClick(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onNoteClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = note.title ?: "Untitled",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = note.dateStamp ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteListScreenPreview() {
    // Sample notes for preview
    val sampleNotes = listOf(
        Note(
            id = 1,
            title = "Shopping List",
            content = "Milk, Eggs, Bread, Butter",
            dateStamp = "2023-06-15"
        ),
        Note(
            id = 2,
            title = "Meeting Notes",
            content = "Discuss project timeline and resource allocation. Follow up with team about progress.",
            dateStamp = "2023-06-12"
        ),
        Note(
            id = 3,
            title = "Ideas for Vacation",
            content = "Beach resort, mountain cabin, or city tour? Research best options for July.",
            dateStamp = "2023-06-10"
        )
    )
    
    AndroidArchitectureTheme {
        NoteListScreenPreviewContent(sampleNotes)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteListScreenPreviewContent(notes: List<Note>) {
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    val sortedNotes = when (sortOrder) {
        SortOrder.TITLE_ASC -> notes.sortedBy { it.title }
        SortOrder.DATE_ASC -> notes.sortedBy { it.dateStamp }
        SortOrder.DATE_DESC -> notes.sortedByDescending { it.dateStamp }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = !showSortMenu }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Title") },
                                onClick = { 
                                    sortOrder = SortOrder.TITLE_ASC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Date (Newest)") },
                                onClick = { 
                                    sortOrder = SortOrder.DATE_DESC
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Date (Oldest)") },
                                onClick = { 
                                    sortOrder = SortOrder.DATE_ASC
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(sortedNotes) { note ->
                NoteItem(
                    note = note,
                    onNoteClick = { }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    val sampleNote = Note(
        id = 1,
        title = "Meeting Notes",
        content = "Discuss project timeline and resource allocation. Follow up with team about progress.",
        dateStamp = "2023-06-12"
    )
    
    AndroidArchitectureTheme {
        NoteItem(
            note = sampleNote,
            onNoteClick = { }
        )
    }
}