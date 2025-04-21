package com.henry.androidarchitecture.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.ResultState
import com.henry.androidarchitecture.ui.list.components.NoteItem
import com.henry.androidarchitecture.ui.list.components.NoteSearchBar
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme
import com.henry.androidarchitecture.ui.theme.BackgroundColorState
import com.henry.androidarchitecture.ui.theme.LocalBackgroundColorState

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
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    // Get background color state from composition local
    val backgroundColorState = LocalBackgroundColorState.current
    
    // Get current screen width and orientation
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    
    // Determine if we should use grid layout (2 columns)
    val useGridLayout = screenWidth > 600.dp || isLandscape
    
    // Filter notes by search query
    val filteredNotes = if (searchQuery.isBlank()) {
        notes
    } else {
        notes.filter { note ->
            note.title?.contains(searchQuery, ignoreCase = true) == true ||
            note.content?.contains(searchQuery, ignoreCase = true) == true
        }
    }
    
    // Sort filtered notes
    val sortedNotes = when (sortOrder) {
        SortOrder.TITLE_ASC -> filteredNotes.sortedBy { it.title }
        SortOrder.DATE_ASC -> filteredNotes.sortedBy { it.dateStamp }
        SortOrder.DATE_DESC -> filteredNotes.sortedByDescending { it.dateStamp }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Notes") },
                    actions = {
                        IconButton(onClick = { 
                            // Change background color when color button is clicked
                            backgroundColorState.changeToRandomColor() 
                        }) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Change Color")
                        }
                        
                        Box {
                            IconButton(onClick = { showSortMenu = !showSortMenu }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
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
                
                NoteSearchBar(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    isSearchActive = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    filteredNotes = filteredNotes,
                    onNoteClick = onNoteClick
                )
            }
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
                    text = if (searchQuery.isBlank()) 
                        "No notes yet. Click the + button to add a note."
                    else 
                        "No matching results for \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            if (useGridLayout) {
                // Grid layout for landscape or tablet
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedNotes) { note ->
                        NoteItem(
                            note = note,
                            onNoteClick = { onNoteClick(note.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // Single column for portrait phone
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
    
    // Create a background color state for the preview
    val backgroundColorState = remember { BackgroundColorState() }
    
    AndroidArchitectureTheme(
        backgroundColorState = backgroundColorState
    ) {
        NoteListScreenPreviewContent(sampleNotes)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteListScreenPreviewContent(notes: List<Note>) {
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    // Get current screen width and orientation for preview
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    
    // Determine if we should use grid layout (2 columns)
    val useGridLayout = screenWidth > 600.dp || isLandscape
    
    // Get background color state from composition local
    val backgroundColorState = LocalBackgroundColorState.current
    
    // Filter notes by search query
    val filteredNotes = if (searchQuery.isBlank()) {
        notes
    } else {
        notes.filter { note ->
            note.title?.contains(searchQuery, ignoreCase = true) == true ||
            note.content?.contains(searchQuery, ignoreCase = true) == true
        }
    }
    
    val sortedNotes = when (sortOrder) {
        SortOrder.TITLE_ASC -> filteredNotes.sortedBy { it.title }
        SortOrder.DATE_ASC -> filteredNotes.sortedBy { it.dateStamp }
        SortOrder.DATE_DESC -> filteredNotes.sortedByDescending { it.dateStamp }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Notes") },
                    actions = {
                        IconButton(onClick = { 
                            // Change background color when color button is clicked
                            backgroundColorState.changeToRandomColor() 
                        }) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Change Color")
                        }
                        
                        Box {
                            IconButton(onClick = { showSortMenu = !showSortMenu }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
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
                
                NoteSearchBar(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    isSearchActive = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    filteredNotes = filteredNotes,
                    onNoteClick = { }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        if (useGridLayout) {
            // Grid layout for landscape or tablet
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedNotes) { note ->
                    NoteItem(
                        note = note,
                        onNoteClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Single column for portrait phone
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
}