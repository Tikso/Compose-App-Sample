package com.example.patternlab.presentation.screens.characters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.patternlab.domain.model.Character
import com.example.patternlab.domain.model.CharacterStatus
import com.example.patternlab.presentation.components.ErrorContent
import com.example.patternlab.presentation.components.LoadingContent
import com.example.patternlab.presentation.theme.PatternLabTheme

/** STATEFUL wrapper: connects the ViewModel to the stateless content below. */
@Composable
fun CharactersScreen(
    viewModel: CharactersViewModel = viewModel(factory = CharactersViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CharactersContent(
        uiState = uiState,
        onRetry = viewModel::loadCharacters
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharactersContent(
    uiState: CharactersUiState,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Lab 2 · Clean Architecture") }) }
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
private fun CharacterList(
    characters: List<Character>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = characters, key = { it.id }) { character ->
            CharacterRow(character)
        }
    }
}

@Composable
private fun CharacterRow(
    character: Character,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = character.imageUrl,
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
                text = "${character.status.label} · ${character.species}",
                style = MaterialTheme.typography.bodyMedium,
                color = character.status.color
            )
        }
    }
}

// ✅ Exhaustive `when` over an enum: add a new status and the compiler points here.
private val CharacterStatus.color: Color
    get() = when (this) {
        CharacterStatus.Alive -> Color(0xFF2E7D32)
        CharacterStatus.Dead -> Color(0xFFC62828)
        CharacterStatus.Unknown -> Color.Gray
    }

private val CharacterStatus.label: String
    get() = when (this) {
        CharacterStatus.Alive -> "Alive"
        CharacterStatus.Dead -> "Dead"
        CharacterStatus.Unknown -> "Unknown"
    }

// ---------- Previews ----------

// ✅ Fixes PAIN #5: a preview row is one line of 5 meaningful fields.
@Preview(showBackground = true)
@Composable
private fun CharactersSuccessPreview() {
    PatternLabTheme {
        CharactersContent(
            uiState = CharactersUiState.Success(
                listOf(
                    Character(1, "Rick Sanchez", CharacterStatus.Alive, "Human", ""),
                    Character(8, "Adjudicator Rick", CharacterStatus.Dead, "Human", ""),
                    Character(7, "Abradolf Lincler", CharacterStatus.Unknown, "Human", "")
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
        CharactersContent(
            uiState = CharactersUiState.Error("No internet connection"),
            onRetry = {}
        )
    }
}
