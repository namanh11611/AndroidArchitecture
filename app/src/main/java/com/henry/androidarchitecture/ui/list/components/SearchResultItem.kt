package com.henry.androidarchitecture.ui.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme

@Composable
fun SearchResultItem(
    note: Note,
    searchQuery: String,
    onClick: () -> Unit
) {
    val title = note.title ?: "Untitled"
    val content = note.content ?: ""
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                // Find the context around the matching search query in content
                val previewText = getContentPreview(content, searchQuery)
                
                Text(
                    text = previewText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.dateStamp ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// Helper function to get content preview with context around the matching query
fun getContentPreview(content: String, query: String): String {
    if (query.isBlank() || !content.contains(query, ignoreCase = true)) {
        return content.take(100)
    }
    
    val startIndex = content.indexOf(query, ignoreCase = true)
    val contextStart = maxOf(0, startIndex - 20)
    val contextEnd = minOf(content.length, startIndex + query.length + 40)
    
    var previewText = content.substring(contextStart, contextEnd)
    
    // Add ellipsis if needed
    if (contextStart > 0) {
        previewText = "..." + previewText
    }
    if (contextEnd < content.length) {
        previewText = previewText + "..."
    }
    
    return previewText
}

@Preview(showBackground = true)
@Composable
fun SearchResultItemPreview() {
    val sampleNote = Note(
        id = 1,
        title = "Meeting Notes",
        content = "Discuss project timeline and resource allocation. Follow up with team about progress.",
        dateStamp = "2023-06-12"
    )
    
    AndroidArchitectureTheme {
        SearchResultItem(
            note = sampleNote,
            searchQuery = "timeline",
            onClick = { }
        )
    }
}
