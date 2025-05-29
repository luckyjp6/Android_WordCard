package com.example.wordcardsapp.data.local

import androidx.room.*

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary WHERE datasetId = :datasetId AND learned = 0 ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomUnlearned(count: Int, datasetId: Int): List<Vocabulary>

    @Query("SELECT * FROM vocabulary WHERE datasetId = :datasetId AND important = 1")
    suspend fun getImportantWords(datasetId: Int): List<Vocabulary>

    @Query("SELECT * FROM vocabulary WHERE datasetId = :datasetId AND learned = 1")
    suspend fun getLearnedWords(datasetId: Int): List<Vocabulary>

    @Query("SELECT COUNT(*) FROM vocabulary WHERE datasetId = :datasetId AND learned = 1")
    suspend fun getNumLearned(datasetId: Int): Int

    @Update
    suspend fun updateSet(vocabularySet: VocabularySet)
    @Update
    suspend fun updateWord(word: Vocabulary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(vocabularySet: VocabularySet)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Vocabulary>)
}