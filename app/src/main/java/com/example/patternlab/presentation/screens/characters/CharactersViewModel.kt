package com.example.patternlab.presentation.screens.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.patternlab.PatternLabApplication
import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.model.DataError
import com.example.patternlab.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CharactersUiState {
    data object Loading : CharactersUiState

    // ✅ Domain model, not the network DTO.
    data class Success(val characters: List<Character>) : CharactersUiState

    data class Error(val message: String) : CharactersUiState
}

/**
 * ✅ Fixes PAIN #2: the dependency comes in through the constructor.
 * In a unit test, pass a GetCharactersUseCase backed by a fake repository. No network needed.
 */
class CharactersViewModel(
    private val getCharacters: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    // ✅ No try/catch and no Retrofit types: just "ask, then render the answer".
    fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = CharactersUiState.Loading
            _uiState.value = getCharacters().fold(
                onSuccess = { CharactersUiState.Success(it) },
                onFailure = { CharactersUiState.Error(it.toUserMessage()) }
            )
        }
    }

    companion object {
        // The framework creates ViewModels, so we tell it how to build ours from the container.
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PatternLabApplication
                CharactersViewModel(app.container.getCharactersUseCase)
            }
        }
    }
}

// Turning a domain error into user-facing text is a presentation concern.
private fun Throwable.toUserMessage(): String = when (this) {
    is DataError.NoInternet -> "No internet connection"
    is DataError.Server -> "Server error ($code)"
    is DataError.Unknown -> "Something went wrong: ${cause.message}"
    else -> "Something went wrong: $message"
}
