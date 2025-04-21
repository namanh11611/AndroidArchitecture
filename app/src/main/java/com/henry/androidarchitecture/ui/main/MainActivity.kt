package com.henry.androidarchitecture.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.henry.androidarchitecture.navigation.NoteNavGraph
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme
import com.henry.androidarchitecture.ui.theme.BackgroundColorState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Create and remember a BackgroundColorState instance
            val backgroundColorState = remember { BackgroundColorState() }
            
            AndroidArchitectureTheme(
                backgroundColorState = backgroundColorState
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoteNavGraph()
                }
            }
        }
    }
}
