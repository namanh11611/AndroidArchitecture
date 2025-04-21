package com.henry.androidarchitecture.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Define the set of available background colors
object BackgroundColors {
    val colors = listOf(
        Color(0xFF0D060F), // Dark purple
        Color(0xFF1E2824), // Dark green
        Color(0xFF5D3C18), // Brown
        Color(0xFF766B65), // Gray
        Color(0xFF230000)  // Dark red
    )
}

// Class to manage the background color state
class BackgroundColorState(initialColor: Color = BackgroundColors.colors.first()) {
    val current = mutableStateOf(initialColor)
    
    fun changeToRandomColor() {
        val currentColor = current.value
        // Get a new random color different from the current one
        val availableColors = BackgroundColors.colors.filter { it != currentColor }
        current.value = availableColors.random()
    }
}

// Composition local to provide the background color state across the app
val LocalBackgroundColorState = compositionLocalOf { 
    BackgroundColorState() 
}
