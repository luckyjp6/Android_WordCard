package com.example.wordcardsapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary set")
data class VocabularySet (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    
    val name: String,
    val tts: String
)

@Entity(
    tableName = "vocabulary",
    foreignKeys = [
        ForeignKey(
            entity = VocabularySet::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("datasetId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Vocabulary (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo("datasetId")
    val datasetId: Int,

    val word: String,
    val partOfSpeech: String?,
    val definition: String,
    val level: String?,
    val note: String?,
    val learned: Boolean = false,
    val important: Boolean = false
)