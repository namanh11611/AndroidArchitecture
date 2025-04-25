package com.henry.androidarchitecture.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.henry.androidarchitecture.R
import com.henry.androidarchitecture.navigation.NoteDestinationsArgs.NOTE_ID_ARG
import com.henry.androidarchitecture.navigation.NoteDestinationsArgs.TITLE_ARG
import com.henry.androidarchitecture.navigation.NoteDestinationsArgs.USER_MESSAGE_ARG
import com.henry.androidarchitecture.ui.detail.NoteDetailScreen
import com.henry.androidarchitecture.ui.editor.NoteEditorScreen
import com.henry.androidarchitecture.ui.list.NoteListScreen

@Composable
fun NoteNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NoteDestinations.NOTE_LIST_ROUTE,
    navActions: NoteNavigationActions = remember(navController) {
        NoteNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            NoteDestinations.NOTE_LIST_ROUTE,
            arguments = listOf(
                navArgument(USER_MESSAGE_ARG) { type = NavType.IntType; defaultValue = 0 }
            )
        ) { entry ->
            val userMessage = entry.arguments?.getInt(USER_MESSAGE_ARG) ?: 0
            NoteListScreen(
                onAddNoteClick = { navActions.navigateToNoteEditor(R.string.add_note, null) },
                onNoteClick = { noteId -> navActions.navigateToNoteDetail(noteId) }
            )
        }
        
        composable(
            NoteDestinations.NOTE_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(NOTE_ID_ARG) { type = NavType.IntType }
            )
        ) { entry ->
            val noteId = entry.arguments?.getInt(NOTE_ID_ARG) ?: 0
            NoteDetailScreen(
                noteId = noteId,
                onBackClick = { navController.popBackStack() },
                onNoteSaved = { navActions.navigateToNoteList(EDIT_RESULT_OK) },
                onNoteDeleted = { navActions.navigateToNoteList(DELETE_RESULT_OK) }
            )
        }
        
        composable(
            NoteDestinations.NOTE_EDITOR_ROUTE,
            arguments = listOf(
                navArgument(TITLE_ARG) { type = NavType.IntType },
                navArgument(NOTE_ID_ARG) { type = NavType.IntType; defaultValue = 0 }
            )
        ) { entry ->
            val noteId = entry.arguments?.getInt(NOTE_ID_ARG) ?: 0
            NoteEditorScreen(
                noteId = noteId,
                onBackClick = { navController.popBackStack() },
                onNoteSaved = {
                    navActions.navigateToNoteList(
                        if (noteId == 0) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK
                    )
                }
            )
        }
    }
}

const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
