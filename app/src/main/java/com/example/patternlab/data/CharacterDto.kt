package com.example.patternlab.data

import kotlinx.serialization.Serializable

/**
 * DTOs = Data Transfer Objects.
 * They mirror the server's JSON 1:1 and nothing else. Field names must match the JSON keys.
 *
 * GET https://rickandmortyapi.com/api/character returns:
 * { "info": { count, pages, next, prev }, "results": [ { id, name, status, ... } ] }
 */
@Serializable
data class CharacterResponseDto(
    val info: InfoDto,
    val results: List<CharacterDto>
)

@Serializable
data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null, // null on the last page
    val prev: String? = null   // null on the first page
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,   // "Alive" | "Dead" | "unknown"  (a raw String from the server)
    val species: String,
    val type: String,     // often an empty string ""
    val gender: String,   // "Female" | "Male" | "Genderless" | "unknown"
    val origin: LocationDto,
    val location: LocationDto,
    val image: String,
    val episode: List<String>, // list of episode URLs
    val url: String,
    val created: String
)

@Serializable
data class LocationDto(
    val name: String,
    val url: String // "" when the location is "unknown"
)