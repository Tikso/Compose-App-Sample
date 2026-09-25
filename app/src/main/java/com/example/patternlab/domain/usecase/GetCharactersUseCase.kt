package com.example.patternlab.domain.usecase

import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.repository.CharacterRepository

/**
 * One use case = one action the user can trigger.
 * It's thin today, but it's the home for business rules later
 * (e.g. filtering, sorting, or combining several repositories).
 */
class GetCharactersUseCase(private val repository: CharacterRepository) {
    suspend operator fun invoke(page: Int = 1): Result<List<Character>> =
        repository.getCharacters(page)
}
