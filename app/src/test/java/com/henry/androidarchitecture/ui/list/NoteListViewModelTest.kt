import app.cash.turbine.test
import com.henry.androidarchitecture.data.model.Note
import com.henry.androidarchitecture.data.repository.NoteRepository
import com.henry.androidarchitecture.data.repository.ResultState
import com.henry.androidarchitecture.ui.list.NoteListUiState
import com.henry.androidarchitecture.ui.list.NoteListViewModel
import com.henry.androidarchitecture.ui.list.SortOrder
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

val notes = listOf(
    Note(1, "Test 1", "Content 1", "2023-01-01"),
    Note(2, "Test 2", "Content 2", "2023-01-02")
)

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: NoteListViewModel
    private lateinit var repository: NoteRepository
    
    @Before
    fun setup() {
        repository = mockk<NoteRepository>()

        // Set up default mock for getNotesStream
        coEvery { repository.getNotesStream() } returns flow {
            emit(ResultState.Success(notes))
        }

        viewModel = NoteListViewModel(repository)
    }
    
    @Test
    fun `when initialized should load notes`() = runTest {
        // When
        val states = mutableListOf<NoteListUiState>()
        viewModel.uiState.test {
            states.add(awaitItem())
        }
        
        // Then
        assertEquals(notes, states.last().notes)
        assertEquals(false, states.last().isLoading)
    }
    
    @Test
    fun `when search query changes should update filtered notes`() = runTest {
        // Given
        val searchQuery = "Test 1"
        val filteredNotes = listOf(notes[0])

        coEvery { repository.searchNotes(searchQuery) } returns ResultState.Success(filteredNotes)
        
        // When
        viewModel.setSearchQuery(searchQuery)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(searchQuery, state.searchQuery)
            assertEquals(false, state.isLoading)
        }
    }
    
    @Test
    fun `when sort order changes should update sorted notes`() = runTest {
        // Given
        val sortOrder = SortOrder.TITLE_ASC
        
        // When
        viewModel.setSortOrder(sortOrder)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(sortOrder, state.sortOrder)
            assertEquals(false, state.showSortMenu)
            // Verify notes are sorted by title
            assertEquals(
                notes.sortedBy { it.title },
                state.notes
            )
        }
    }
}