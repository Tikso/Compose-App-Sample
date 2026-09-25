package com.example.patternlab.di

import com.example.patternlab.data.remote.RetrofitProvider
import com.example.patternlab.data.repository.CharacterRepositoryImpl
import com.example.patternlab.domain.repository.CharacterRepository
import com.example.patternlab.domain.usecase.GetCharactersUseCase

/**
 * MANUAL DEPENDENCY INJECTION: the one place that knows how to build the object graph.
 * Classes receive what they need through their constructors and never build it themselves.
 *
 * This is the job Hilt automates later: @Module/@Provides replace these properties.
 */
class AppContainer {
    // Typed as the INTERFACE: nothing outside this file knows the Impl exists.
    private val characterRepository: CharacterRepository by lazy {
        CharacterRepositoryImpl(RetrofitProvider.api)
    }

    val getCharactersUseCase: GetCharactersUseCase
        get() = GetCharactersUseCase(characterRepository)
}
