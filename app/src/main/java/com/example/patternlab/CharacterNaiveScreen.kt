package com.example.patternlab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.patternlab.data.CharacterDto
import com.example.patternlab.data.LocationDto

import com.example.patternlab.screen.CharactersNaiveViewModel
import com.example.patternlab.screen.CharactersUiState
import com.example.patternlab.ui.theme.PatternLabTheme

/**
 * STATEFUL wrapper: knows about the ViewModel, collects its state.
 * Its only job is to connect the ViewModel to the pure UI below.
 */
@Composable
fun CharactersNaiveScreen(
    viewModel: CharactersNaiveViewModel = viewModel()
) {
    // Lifecycle-aware: stops collecting when the app is in the background.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CharactersNaiveContent(
        uiState = uiState,
        onRetry = viewModel::loadCharacters // "value in, callback out"
    )
}

/**
 * STATELESS content: plain values in, callbacks out. No ViewModel reference,
 * so it's easy to preview and test (state hoisting).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharactersNaiveContent(
    uiState: CharactersUiState,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Lab 1 · Naive") }) }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (uiState) {
            CharactersUiState.Loading -> LoadingContent(contentModifier)
            is CharactersUiState.Success -> CharacterList(uiState.characters, contentModifier)
            is CharactersUiState.Error -> ErrorContent(uiState.message, onRetry, contentModifier)
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun CharacterList(
    characters: List<CharacterDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        // key = stable id, so Compose can track items correctly when the list changes
        items(items = characters, key = { it.id }) { character ->
            CharacterRow(character)
        }
    }
}

@Composable
private fun CharacterRow(
    character: CharacterDto,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = character.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${character.status} · ${character.species}",
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor(character.status)
            )
        }
    }
}

// 🚩 PAIN #4: the UI compares raw server strings ("Alive"). A typo compiles fine and
// silently breaks. Later we'll map this to a proper enum in the domain layer.
private fun statusColor(status: String): Color = when (status) {
    "Alive" -> Color(0xFF2E7D32)
    "Dead" -> Color(0xFFC62828)
    else -> Color.Gray
}

// ---------- Preview ----------

// 🚩 PAIN #5: to preview one row we must fill a 12-field network DTO.
private fun fakeCharacter(id: Int, name: String, status: String) = CharacterDto(
    id = id,
    name = name,
    status = status,
    species = "Human",
    type = "",
    gender = "Male",
    origin = LocationDto(name = "Earth (C-137)", url = ""),
    location = LocationDto(name = "Citadel of Ricks", url = ""),
    image = "",
    episode = emptyList(),
    url = "",
    created = ""
)

@Preview(showBackground = true)
@Composable
private fun CharactersSuccessPreview() {
    PatternLabTheme {
        CharactersNaiveContent(
            uiState = CharactersUiState.Success(
                listOf(
                    fakeCharacter(1, "Rick Sanchez", "Alive"),
                    fakeCharacter(8, "Adjudicator Rick", "Dead"),
                    fakeCharacter(7, "Abradolf Lincler", "unknown")
                )
            ),
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharactersErrorPreview() {
    PatternLabTheme {
        CharactersNaiveContent(
            uiState = CharactersUiState.Error("No internet connection"),
            onRetry = {}
        )
    }
}