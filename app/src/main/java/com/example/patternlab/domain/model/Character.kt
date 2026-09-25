package com.example.patternlab.domain.model

/**
 * DOMAIN MODEL: what the app actually cares about, in the app's own words.
 * Pure Kotlin (no Android, no Retrofit, no @Serializable), so it survives any API change.
 *
 * ✅ Fixes PAIN #1 and #5: the UI never sees the 12-field network DTO,
 * and a preview only needs these 5 fields.
 */
data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val imageUrl: String
)

/**
 * ✅ Fixes PAIN #4: a typo like `CharacterStatus.Alvie` no longer compiles,
 * and `when` over the enum is exhaustive.
 */
enum class CharacterStatus { Alive, Dead, Unknown }
