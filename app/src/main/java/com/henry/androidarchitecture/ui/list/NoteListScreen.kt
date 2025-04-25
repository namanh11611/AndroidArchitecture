package com.henry.androidarchitecture.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.ui.list.components.NoteItem
import com.henry.androidarchitecture.ui.list.components.NoteSearchBar
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme
import com.henry.androidarchitecture.ui.theme.BackgroundColorState
import com.henry.androidarchitecture.ui.theme.LocalBackgroundColorState
import com.henry.androidarchitecture.util.AppUtils

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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Get background color state from composition local
    val backgroundColorState = LocalBackgroundColorState.current
    
    // Determine if we should use grid layout using AppUtils
    val useGridLayout = AppUtils.shouldUseGridLayout(LocalConfiguration.current)
    
    // Update grid layout preference in ViewModel
    LaunchedEffect(useGridLayout) {
        viewModel.setGridLayout(useGridLayout)
    }
    
    // Use database search results when search is active, otherwise use all notes
    val displayNotes = if (uiState.searchQuery.isBlank()) {
        uiState.notes
    } else {
        uiState.filteredNotes
    }
    
    // Effect to trigger search when query changes
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery.isNotBlank()) {
            viewModel.searchNotes(uiState.searchQuery)
        }
    }
    
    // Sort filtered notes
    val sortedNotes = when (uiState.sortOrder) {
        SortOrder.TITLE_ASC -> displayNotes.sortedBy { it.title }
        SortOrder.DATE_ASC -> displayNotes.sortedBy { it.dateStamp }
        SortOrder.DATE_DESC -> displayNotes.sortedByDescending { it.dateStamp }
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
                            IconButton(onClick = { viewModel.toggleSortMenu() }) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                            }
                            
                            DropdownMenu(
                                expanded = uiState.showSortMenu,
                                onDismissRequest = { viewModel.toggleSortMenu() }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sort by Title") },
                                    onClick = { viewModel.setSortOrder(SortOrder.TITLE_ASC) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort by Date (Newest)") },
                                    onClick = { viewModel.setSortOrder(SortOrder.DATE_DESC) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort by Date (Oldest)") },
                                    onClick = { viewModel.setSortOrder(SortOrder.DATE_ASC) }
                                )
                            }
                        }
                    }
                )
                
                NoteSearchBar(
                    searchQuery = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    isSearchActive = uiState.isSearchActive,
                    onActiveChange = { viewModel.setSearchActive(it) },
                    filteredNotes = displayNotes,
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
                    text = if (uiState.searchQuery.isBlank()) 
                        "No notes yet. Click the + button to add a note."
                    else 
                        "No matching results for \"${uiState.searchQuery}\"",
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
                    
                    // Add loading indicator at the bottom if more data is being loaded
                    if (uiState.hasMoreData || uiState.isLoading) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isLoading) {
                                    androidx.compose.material3.CircularProgressIndicator()
                                } else {
                                    // Load more when this item becomes visible
                                    LaunchedEffect(key1 = true) {
                                        viewModel.loadMoreNotes()
                                    }
                                }
                            }
                        }
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
                    
                    // Add loading indicator at the bottom if more data is being loaded
                    if (uiState.hasMoreData || uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isLoading) {
                                    androidx.compose.material3.CircularProgressIndicator()
                                } else {
                                    // Load more when this item becomes visible
                                    LaunchedEffect(key1 = true) {
                                        viewModel.loadMoreNotes()
                                    }
                                }
                            }
                        }
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
    
    // Determine if we should use grid layout using AppUtils
    val useGridLayout = AppUtils.shouldUseGridLayout(LocalConfiguration.current)
    
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