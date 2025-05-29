package com.example.wordcardsapp.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
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

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "Setting"
    )

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

    suspend fun insertAll(words: List<Vocabulary>) {
        return dao.insertAll(words)
    }

    // Setting
//    suspend fun changeLanguage(language: String, context: Context) {
//        context.dataStore.edit { preferences ->
//            preferences[]
//        }
//    }
//    https://developer.android.com/codelabs/basic-android-kotlin-training-preferences-datastore?hl=zh-tw#4
}