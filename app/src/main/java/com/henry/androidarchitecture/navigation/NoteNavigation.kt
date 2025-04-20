package com.henry.androidarchitecture.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.henry.androidarchitecture.navigation.NoteDestinationsArgs.NOTE_ID_ARG
import com.henry.androidarchitecture.navigation.NoteDestinationsArgs.TITLE_ARG
import com.henry.androidarchitecture.navigation.NoteDestinationsArgs.USER_MESSAGE_ARG
import com.henry.androidarchitecture.navigation.NoteScreens.NOTE_EDITOR_SCREEN
import com.henry.androidarchitecture.navigation.NoteScreens.NOTE_DETAIL_SCREEN
import com.henry.androidarchitecture.navigation.NoteScreens.NOTE_LIST_SCREEN

private object NoteScreens {
    const val NOTE_LIST_SCREEN = "noteList"
    const val NOTE_DETAIL_SCREEN = "noteDetail"
    const val NOTE_EDITOR_SCREEN = "noteEditor"
}

object NoteDestinationsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val NOTE_ID_ARG = "noteId"
    const val TITLE_ARG = "title"
}

object NoteDestinations {
    const val NOTE_LIST_ROUTE = "$NOTE_LIST_SCREEN?$USER_MESSAGE_ARG={$USER_MESSAGE_ARG}"
    const val NOTE_DETAIL_ROUTE = "$NOTE_DETAIL_SCREEN/{$NOTE_ID_ARG}"
    const val NOTE_EDITOR_ROUTE = "$NOTE_EDITOR_SCREEN/{$TITLE_ARG}?$NOTE_ID_ARG={$NOTE_ID_ARG}"
}

class NoteNavigationActions(private val navController: NavHostController) {

    fun navigateToNoteList(userMessage: Int = 0) {
        val navigatesFromDrawer = userMessage == 0
        navController.navigate(
            NOTE_LIST_SCREEN.let {
                if (userMessage != 0) "$it?$USER_MESSAGE_ARG=$userMessage" else it
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = !navigatesFromDrawer
                saveState = navigatesFromDrawer
            }
            launchSingleTop = true
            restoreState = navigatesFromDrawer
        }
    }

    fun navigateToNoteDetail(noteId: Int) {
        navController.navigate("$NOTE_DETAIL_SCREEN/$noteId")
    }

    fun navigateToNoteEditor(title: Int, noteId: Int?) {
        navController.navigate(
            "$NOTE_EDITOR_SCREEN/$title".let {
                if (noteId != null) "$it?$NOTE_ID_ARG=$noteId" else it
            }
        )
    }
}
