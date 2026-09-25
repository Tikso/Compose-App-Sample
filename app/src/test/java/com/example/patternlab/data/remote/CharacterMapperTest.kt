package com.example.patternlab.data.remote

import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.model.CharacterStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterMapperTest {

    private fun dto(status: String) = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = status,
        species = "Human",
        type = "",
        gender = "Male",
        origin = LocationDto(name = "Earth (C-137)", url = ""),
        location = LocationDto(name = "Citadel of Ricks", url = ""),
        image = "https://example.com/1.jpeg",
        episode = emptyList(),
        url = "",
        created = ""
    )

    @Test
    fun `maps dto fields to domain model`() {
        assertEquals(
            Character(1, "Rick Sanchez", CharacterStatus.Alive, "Human", "https://example.com/1.jpeg"),
            dto("Alive").toDomain()
        )
    }

    @Test
    fun `maps every server status string`() {
        assertEquals(CharacterStatus.Alive, dto("Alive").toDomain().status)
        assertEquals(CharacterStatus.Dead, dto("Dead").toDomain().status)
        assertEquals(CharacterStatus.Unknown, dto("unknown").toDomain().status)
        assertEquals(CharacterStatus.Unknown, dto("something new").toDomain().status)
    }
}
