package com.example.wordcardsapp.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordcardsapp.viewmodel.VocabularyViewModel

@Composable
fun HomeScreen(
    viewModel: VocabularyViewModel,
    onNavigateRandomWordsScreen: () -> Unit,
    onNavigateImportantWordsScreen: () -> Unit,
    onNavigateLearnedWordsScreen: () -> Unit
) {
    val num_learned = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        num_learned.value = viewModel.getNumLearned()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            HomeButton(
                name = "Random Words",
                icon = Icons.Default.PlayArrow,
                onClick = onNavigateRandomWordsScreen
            )

            HomeButton(
                name = "Starred Words",
                icon = Icons.Default.Star,
                onClick = onNavigateImportantWordsScreen
            )

            HomeButton(
                name = "Learned Words (${num_learned.value})",
                icon = Icons.Default.Check,
                onClick = onNavigateLearnedWordsScreen
            )
        }
    }
}

@Composable
fun HomeButton(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 18.sp
        )
    }
    Spacer(Modifier.height(32.dp))
}