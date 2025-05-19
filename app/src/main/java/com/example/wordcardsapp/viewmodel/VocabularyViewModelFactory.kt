package com.example.wordcardsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordcardsapp.data.local.VocabularyDao

class VocabularyViewModelFactory(private val dao: VocabularyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VocabularyViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}