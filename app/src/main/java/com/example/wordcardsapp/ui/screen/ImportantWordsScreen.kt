package com.example.wordcardsapp.ui.screen

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.wordcardsapp.data.local.Vocabulary
import com.example.wordcardsapp.ui.component.VocabularyPager
import com.example.wordcardsapp.viewmodel.VocabularyViewModel

@Composable
fun ImportantWordsScreen(
    viewModel: VocabularyViewModel,
    tts: TextToSpeech
) {
    var words by remember { mutableStateOf<List<Vocabulary>>(emptyList()) }

    LaunchedEffect(Unit) {
        words = viewModel.getImportantWords()
    }
//    viewModel.importantWords.collectAsState()

    VocabularyPager(
        tts = tts,
        vocabularyList  = words,
        onSwipeUp = { word ->
            viewModel.updateWord(word)
        },
        onSwipeDown = { word ->
            viewModel.updateWord(word)
        },
        onToggleImportant  = { word->
            viewModel.updateWord(word)
        }
    )
}