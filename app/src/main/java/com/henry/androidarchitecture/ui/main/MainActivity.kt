package com.henry.androidarchitecture.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.henry.androidarchitecture.navigation.NoteNavGraph
import com.henry.androidarchitecture.ui.theme.AndroidArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidArchitectureTheme {
                NoteNavGraph()
            }
        }
    }
}
