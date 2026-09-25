package com.example.patternlab.data.repository

import com.example.patternlab.data.remote.RickAndMortyApi
import com.example.patternlab.data.remote.toDomain
import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.model.DataError
import com.example.patternlab.domain.repository.CharacterRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/**
 * ✅ Fixes PAIN #3: networking + error translation now live in ONE place.
 * Any ViewModel that needs characters reuses this instead of copy-pasting try/catch.
 *
 * The API is passed in (constructor injection), not grabbed from a singleton.
 */
class CharacterRepositoryImpl(
    private val api: RickAndMortyApi
) : CharacterRepository {

    override suspend fun getCharacters(page: Int): Result<List<Character>> =
        try {
            Result.success(api.getCharacters(page).results.map { it.toDomain() })
        } catch (e: CancellationException) {
            // Not runCatching {}: it would swallow cancellation too.
            throw e
        } catch (e: HttpException) {
            Result.failure(DataError.Server(e.code()))
        } catch (e: IOException) {
            Result.failure(DataError.NoInternet)
        } catch (e: Exception) {
            Result.failure(DataError.Unknown(e))
        }
}
