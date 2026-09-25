package com.example.patternlab.domain.repository

import com.example.patternlab.domain.model.Character

/**
 * The CONTRACT. The domain says *what* it needs; the data layer decides *how*
 * (network today, network + Room cache later).
 *
 * On failure, the Result holds a [com.example.patternlab.domain.model.DataError].
 */
interface CharacterRepository {
    suspend fun getCharacters(page: Int): Result<List<Character>>
}
