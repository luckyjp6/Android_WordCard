package com.example.wordcardsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class Vocabulary (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val partOfSpeech: String?,
    val definition: String,
    val level: String?,
    val note: String?,
    val learned: Boolean = false,
    val important: Boolean = false
)