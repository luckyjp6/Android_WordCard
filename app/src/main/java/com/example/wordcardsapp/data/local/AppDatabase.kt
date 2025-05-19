package com.example.wordcardsapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Vocabulary::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    name = "wordcards-db"
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            CoroutineScope(Dispatchers.IO).launch {
                                val inputStream = context.assets.open("vocab.csv")
                                val lines = inputStream.bufferedReader().readLines().drop(1)

                                val words = lines.map { line ->
                                    val parts = line.split(",")
                                    Vocabulary(
                                        word = parts[0],
                                        partOfSpeech = parts[1],
                                        definition = parts[2],
                                        note = parts[3],
                                        level = parts[4]
                                    )
                                }

                                getInstance(context).vocabularyDao().insertAll(words)
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
    }
}