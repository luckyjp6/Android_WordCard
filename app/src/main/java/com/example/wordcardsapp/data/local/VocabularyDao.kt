package com.example.wordcardsapp.data.local

import androidx.room.*

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary WHERE learned = 0 ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomUnlearned(count: Int): List<Vocabulary>

    @Query("SELECT * FROM vocabulary WHERE important = 1")
    suspend fun getImportantWords(): List<Vocabulary>

    @Query("SELECT * FROM vocabulary WHERE learned = 1")
    suspend fun getLearnedWords(): List<Vocabulary>

    @Query("SELECT COUNT(*) FROM vocabulary WHERE learned = 1")
    suspend fun getNumLearned(): Int

    @Update
    suspend fun updateWord(word: Vocabulary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Vocabulary>)
}