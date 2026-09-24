package com.example.patternlab.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patternlab.data.CharacterDto
import com.example.patternlab.data.RetrofitProvider
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface CharactersUiState {
    data object Loading : CharactersUiState

    // 🚩 PAIN #1: the UI state holds the raw network DTO (CharacterDto).
    data class Success(val characters: List<CharacterDto>) : CharactersUiState

    data class Error(val message: String) : CharactersUiState
}
class CharactersNaiveViewModel  : ViewModel() {

    // 🚩 PAIN #2: hard-wired dependency. The ViewModel reaches out and grabs the real
    // network singleton itself, so you can't swap in a fake for a unit test.
    private val api = RetrofitProvider.api

    // Private mutable state, public read-only state (same "whiteboard" idea as remember/mutableStateOf).
    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = CharactersUiState.Loading

            // 🚩 PAIN #3: networking + error handling live inside the ViewModel.
            // Every other ViewModel that needs characters would copy-paste this block.
            _uiState.value = try {
                val response = api.getCharacters(page = 1)
                CharactersUiState.Success(response.results)
            } catch (e: HttpException) {
                // Server answered, but with 4xx/5xx
                CharactersUiState.Error("Server error (${e.code()})")
            } catch (e: IOException) {
                // No connection, timeout, DNS failure...
                CharactersUiState.Error("No internet connection")
            } catch (e: CancellationException) {
                // Never swallow cancellation, or the coroutine can't be cancelled properly.
                throw e
            } catch (e: Exception) {
                // e.g. JSON parsing problems (SerializationException)
                CharactersUiState.Error("Something went wrong: ${e.message}")
            }
        }
    }
}