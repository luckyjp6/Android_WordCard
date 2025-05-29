package com.example.wordcardsapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.wordcardsapp.ui.theme.WordCardsAppTheme
import com.example.wordcardsapp.data.local.AppDatabase
import com.example.wordcardsapp.viewmodel.VocabularyViewModel
import com.example.wordcardsapp.viewmodel.VocabularyViewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wordcardsapp.ui.screen.*
import com.example.wordcardsapp.ui.screen.RandomWordScreen
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getInstance(applicationContext).vocabularyDao()
        val factory = VocabularyViewModelFactory(dao)
        val viewModel: VocabularyViewModel by viewModels { factory }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US // UK xd
            }
        }

        enableEdgeToEdge()
        setContent {
            WordCardsAppTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                val title = when (currentRoute) {
                    "home" -> "Let's Learn!"
                    "setting" -> "Setting"
                    "random_list" -> "Random Words"
                    "important_list" -> "Starred Words"
                    "learned_list" -> "Learned Words"
                    "vocab_detail/{vocabId}" -> "Learned Words Detail"
                    "import" -> "Import Vocabularies"
                    else -> "Learned Words Detail"
                }
                val showBack = (currentRoute != "home")

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if (showBack) {
                                    IconButton( onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Return"
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (currentRoute == "random_list") {
                                    IconButton( onClick = {
                                        viewModel.loadRandomWords()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "New Words"
                                        )
                                    }
                                }
                                if (currentRoute == "home") {
                                    IconButton( onClick = { navController.navigate("setting") } ) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    }
                                }
                            }
                        )
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel,
                                onSettingScreen = { navController.navigate("setting")},
                                onNavigateRandomWordsScreen = { navController.navigate("random_list") },
                                onNavigateImportantWordsScreen = { navController.navigate("important_list") },
                                onNavigateLearnedWordsScreen = { navController.navigate("learned_list")}
                            )
                        }
                        composable("setting") {
                            SettingScreen(
                                viewModel,
                                tts,
                                onImportVocabularies = { navController.navigate("import") }
                            )
                        }
                        composable("import") {
                            ImportVocabulariesScreen(viewModel, tts)
                        }
                        composable("random_list") {
                            RandomWordScreen(viewModel, tts)
                        }
                        composable("important_list") {
                            ImportantWordsScreen(viewModel, tts)
                        }
                        composable("learned_list") {
                            LearnedWordsListScreen(viewModel, tts,
                                onClick = { id -> navController.navigate("vocab_detail/${id}")}
                            )
                        }
                        composable("vocab_detail/{vocabId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("vocabId")?.toIntOrNull()
                            id?.let {
                                LearnedWordsCardScreen(
                                    viewModel = viewModel,
                                    tts = tts,
                                    id = id
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
