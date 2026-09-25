package com.example.patternlab.presentation.screens.characters

import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.model.CharacterStatus
import com.example.patternlab.domain.model.DataError
import com.example.patternlab.domain.repository.CharacterRepository
import com.example.patternlab.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * ✅ The payoff of PAIN #2's fix: the ViewModel is tested with a fake repository.
 * No network, no Retrofit, no Android device. Runs in milliseconds on the JVM.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    private class FakeCharacterRepository : CharacterRepository {
        var result: Result<List<Character>> = Result.success(emptyList())
        override suspend fun getCharacters(page: Int) = result
    }

    private val repository = FakeCharacterRepository()
    private val rick = Character(1, "Rick Sanchez", CharacterStatus.Alive, "Human", "")

    @Before
    fun setUp() {
        // viewModelScope runs on Dispatchers.Main, which doesn't exist on the JVM.
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = CharactersViewModel(GetCharactersUseCase(repository))

    @Test
    fun `success shows characters`() {
        repository.result = Result.success(listOf(rick))

        val viewModel = createViewModel()

        assertEquals(CharactersUiState.Success(listOf(rick)), viewModel.uiState.value)
    }

    @Test
    fun `no internet shows friendly message`() {
        repository.result = Result.failure(DataError.NoInternet)

        val viewModel = createViewModel()

        assertEquals(CharactersUiState.Error("No internet connection"), viewModel.uiState.value)
    }

    @Test
    fun `server error shows status code`() {
        repository.result = Result.failure(DataError.Server(503))

        val viewModel = createViewModel()

        assertEquals(CharactersUiState.Error("Server error (503)"), viewModel.uiState.value)
    }

    @Test
    fun `retry after failure loads characters`() {
        repository.result = Result.failure(DataError.NoInternet)
        val viewModel = createViewModel()

        repository.result = Result.success(listOf(rick))
        viewModel.loadCharacters()

        assertEquals(CharactersUiState.Success(listOf(rick)), viewModel.uiState.value)
    }
}
