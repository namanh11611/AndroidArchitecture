package com.henry.androidarchitecture.ui.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    filteredNotes: List<Note>,
    onNoteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = searchQuery,
        onQueryChange = onQueryChange,
        onSearch = { onActiveChange(false) },
        active = isSearchActive,
        onActiveChange = onActiveChange,
        placeholder = { Text("Search notes...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        // Show search results in the dropdown
        if (searchQuery.isNotEmpty()) {
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching notes found")
                }
            } else {
                Text(
                    text = "Search Results",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredNotes.take(5)) { note ->
                        SearchResultItem(
                            note = note,
                            searchQuery = searchQuery,
                            onClick = {
                                onNoteClick(note.id)
                                onActiveChange(false)
                            }
                        )
                    }

                    if (filteredNotes.size > 5) {
                        item {
                            Text(
                                text = "See all ${filteredNotes.size} results",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onActiveChange(false)
                                    }
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            // Show recent searches or suggestions here if needed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Try searching for titles, content, or keywords")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NoteSearchBarPreview() {
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
        )
    )
    
    AndroidArchitectureTheme {
        NoteSearchBar(
            searchQuery = "meeting",
            onQueryChange = {},
            isSearchActive = true,
            onActiveChange = {},
            filteredNotes = sampleNotes,
            onNoteClick = {}
        )
    }
} 