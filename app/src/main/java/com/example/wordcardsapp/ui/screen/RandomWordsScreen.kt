package com.example.wordcardsapp.ui.screen

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.wordcardsapp.ui.component.VocabularyPager
import com.example.wordcardsapp.viewmodel.VocabularyViewModel

@Composable
fun RandomWordScreen(
    viewModel: VocabularyViewModel,
    tts: TextToSpeech
) {
    val words by viewModel.words.collectAsState()
    if (words.isEmpty()) {
        viewModel.loadRandomWords()
    }

    VocabularyPager(
        tts = tts,
        vocabularyList = words,
        onSwipeUp = { word ->
            viewModel.updateWord(word.copy(learned = false))
        },
        onSwipeDown = { word ->
            viewModel.updateWord(word.copy(learned = true))
        },
        onToggleImportant  = { word->
            viewModel.updateWord(word.copy(important = !word.important))
        }
    )
}