package com.example.wordcardsapp.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordcardsapp.R
import com.example.wordcardsapp.viewmodel.VocabularyViewModel

@Composable
fun HomeScreen(
    viewModel: VocabularyViewModel,
    onSettingScreen: () -> Unit,
    onNavigateRandomWordsScreen: () -> Unit,
    onNavigateImportantWordsScreen: () -> Unit,
    onNavigateLearnedWordsScreen: () -> Unit
) {
    val numLearned = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        numLearned.intValue = viewModel.getNumLearned()
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
                onClick = onNavigateRandomWordsScreen,
                imageVector = ImageVector.vectorResource(R.drawable.casino)
            )

            HomeButton(
                name = "Starred Words",
                onClick = onNavigateImportantWordsScreen,
                icon = Icons.Default.Star
            )

            HomeButton(
                name = "Learned Words (${numLearned.intValue})",
                onClick = onNavigateLearnedWordsScreen,
                icon = Icons.Default.Check
            )
            HomeButton(
                name = "Quiz",
                onClick = onNavigateLearnedWordsScreen,
                icon = Icons.Default.PlayArrow
            )
        }
    }
}

@Composable
fun HomeButton(
    name: String,
    onClick: () -> Unit,
    imageVector: ImageVector? = null,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null)
        } else if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = name,
                modifier = Modifier
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 18.sp
        )
    }
    Spacer(Modifier.height(32.dp))
}