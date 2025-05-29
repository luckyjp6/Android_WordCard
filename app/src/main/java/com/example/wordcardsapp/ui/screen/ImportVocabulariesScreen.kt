package com.example.wordcardsapp.ui.screen

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.wordcardsapp.data.local.Vocabulary
import com.example.wordcardsapp.viewmodel.VocabularyViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ImportVocabulariesScreen(
    viewModel: VocabularyViewModel,
    tts: TextToSpeech
) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var datasetName by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            println(uri)
            selectedUri.value = uri
//            parseAndInsertCSV(uri, context, name, viewModel)
        }
    }
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = datasetName,
            onValueChange = { datasetName = it },
            label = { Text("Input the name of the Vocabulary set") },
            placeholder = { Text("New") }
        )
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = { launcher.launch("text/csv")}) {
                Text("Import vocabulary (.csv)")
            }
            Spacer(Modifier.width(8.dp))
            Text(text = selectedUri.toString())
        }
        Spacer(Modifier.height(16.dp))


        Button(onClick = {
            if (selectedUri == null) {
                Toast.makeText(context, "Please select the file to import.", Toast.LENGTH_LONG).show()
            } else if (datasetName.isNotEmpty()) {
                Toast.makeText(context, "Please enter the name of the vocabulary set.", Toast.LENGTH_LONG).show()
            } else {
                parseAndInsertCSV(selectedUri!!, context, datasetName, viewModel)
            }
        }) {
            Text("Import")
        }
    }
}

fun parseAndInsertCSV(
    uri: Uri,
    context: Context,
    datasetName: String,
    viewModel: VocabularyViewModel
) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val reader = BufferedReader(InputStreamReader(inputStream))

    val vocabularies = mutableListOf<Vocabulary>()

    reader.forEachLine { line ->
        val tokens = line.split(",")
        if (tokens.size >= 3) {
            val word =tokens[0].trim()
            val partOfSpeech = tokens[1].trim()
            val definition = tokens[2].trim()
            val note = tokens[3].trim()
            val level = tokens[4].trim()

            vocabularies.add(
                Vocabulary(
                    id = 0,
                    dataset = datasetName,
                    word = word,
                    partOfSpeech = partOfSpeech,
                    definition = definition,
                    note = note,
                    level = level
                )
            )
        }
    }
    viewModel.viewModelScope.launch {
        viewModel.insertAll(vocabularies)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    selectedLanguage: String,
    languages: List<String>,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedLanguage,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Language") }
        )
    }
}