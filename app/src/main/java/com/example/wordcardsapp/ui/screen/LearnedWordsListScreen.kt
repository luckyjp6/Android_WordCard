package com.example.wordcardsapp.ui.screen

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.wordcardsapp.data.local.Vocabulary
import com.example.wordcardsapp.ui.component.VocabularyList
import com.example.wordcardsapp.viewmodel.VocabularyViewModel

@Composable
fun LearnedWordsListScreen(
    viewModel: VocabularyViewModel,
    tts: TextToSpeech,
    onClick: (Int) -> Unit
) {
    var words by remember { mutableStateOf<List<Vocabulary>>(emptyList()) }
    LaunchedEffect(Unit) {
        words = viewModel.getLearnedWords()
    }

    VocabularyList(
        tts = tts,
        vocabularyList = words,
        onSwipeLeft = { word ->
            val updated = word.copy(learned = false)
            viewModel.updateWord(updated)
            words = words.map {
                if (it.id == word.id) updated else it
            }
        },
        onSwipeRight = { word ->
            val updated = word.copy(learned = true)
            viewModel.updateWord(updated)
            words = words.map {
                if (it.id == word.id) updated else it
            }
        },
        onToggleImportant = { word ->
            val updated = word.copy(important = !word.important)
            viewModel.updateWord(updated)
            words = words.map {
                if (it.id == word.id) updated else it
            }
        },
        onClickWord = onClick
    )
}