package com.henry.androidarchitecture.util

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppUtils {
    const val DATABASE_NAME = "compose_base"
    const val DATABASE_VERSION = 1

    fun shouldUseGridLayout(
        configuration: Configuration,
        tabletWidthThreshold: Dp = 600.dp
    ): Boolean {
        val screenWidth = configuration.screenWidthDp.dp
        val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
        
        // Use grid layout for tablets (width > threshold) or any device in landscape
        return screenWidth > tabletWidthThreshold || isLandscape
    }
}