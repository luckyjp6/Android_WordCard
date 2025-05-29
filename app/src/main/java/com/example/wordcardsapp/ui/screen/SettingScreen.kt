package com.example.wordcardsapp.ui.screen

import android.content.Context
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.wordcardsapp.viewmodel.VocabularyViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingScreen(
    viewModel: VocabularyViewModel,
    tts: TextToSpeech,
    onImportVocabularies: () -> Unit
) {
    val context = LocalContext.current
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Button(onClick = { downloadCsvTemplate(context) }) {
            Text("Download vocabulary template")
        }
        Button(onClick = onImportVocabularies) {
            Text("Import vocabulary")
        }
    }
}

fun downloadCsvTemplate(context: Context) {
    val templateContent = "word,partOfSpeech,definition,note,level\n" + "example,noun,範例,備註,初級\n"
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "vocabulary_template.csv")

    try {
        FileOutputStream(file).use {it.write(templateContent.toByteArray())}
        Toast.makeText(context, "Template saved! (${file.absolutePath})", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Fail to download vocabulary template", Toast.LENGTH_LONG).show()
    }
}