package com.example.wordcardsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordcardsapp.data.local.Vocabulary
import com.example.wordcardsapp.data.local.VocabularyDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VocabularyViewModel(private val dao: VocabularyDao) : ViewModel() {
    private val _words = MutableStateFlow<List<Vocabulary>>(emptyList())
    val words: StateFlow<List<Vocabulary>> = _words

    fun loadRandomWords(count: Int = 10) {
        viewModelScope.launch {
            _words.value = dao.getRandomUnlearned(count)
        }
    }

    fun updateWord(word: Vocabulary, reload: Boolean = true) {
        viewModelScope.launch {
            dao.updateWord(word)

            if (reload) {
                _words.update { currentList ->
                    currentList.map {
                        if (it.id == word.id) word
                        else it
                    }
                }
            }
        }
    }

    suspend fun getImportantWords(): List<Vocabulary> {
        return dao.getImportantWords()
    }
    suspend fun getLearnedWords(): List<Vocabulary> {
        return dao.getLearnedWords()
    }
    suspend fun getNumLearned(): Int {
        return dao.getNumLearned()
    }
}