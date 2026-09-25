package com.example.patternlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.patternlab.presentation.screens.characters.CharactersScreen
import com.example.patternlab.presentation.theme.PatternLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatternLabTheme {
                CharactersScreen()
            }
        }
    }
}
