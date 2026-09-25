package com.example.patternlab.data.remote

import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.model.CharacterStatus

/**
 * DTO -> domain mapping lives in the data layer: the only place that knows both shapes.
 * The raw server strings are compared exactly once, here.
 */
fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status.toCharacterStatus(),
    species = species,
    imageUrl = image
)

internal fun String.toCharacterStatus(): CharacterStatus = when (lowercase()) {
    "alive" -> CharacterStatus.Alive
    "dead" -> CharacterStatus.Dead
    else -> CharacterStatus.Unknown
}
