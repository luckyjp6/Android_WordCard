package com.example.wordcardsapp.ui.component

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordcardsapp.data.local.Vocabulary
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun VocabularyPager(
    tts: TextToSpeech,
    vocabularyList: List<Vocabulary>,
    onSwipeUp: (Vocabulary) -> Unit,
    onSwipeDown: (Vocabulary) -> Unit,
    onToggleImportant: (Vocabulary) -> Unit,
    startFromId: Int = 0
) {
    // Empty list
    if (vocabularyList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Vocabularies", color = Color.Gray)
        }
        return
    }

    val maxIndicatorCount = 10
    val coroutineScope = rememberCoroutineScope()
    var localVocabularyList by remember { mutableStateOf<List<Vocabulary>>(vocabularyList) }
    var isFirst by remember { mutableStateOf(true) }
    var isUpdated by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()

    fun update(
        updated: Vocabulary,
        updateFunc: (Vocabulary) -> Unit
    ) {
        isUpdated = true
        updateFunc(updated)
        localVocabularyList = localVocabularyList.map {
            if (it.id == updated.id) updated else it
        }
    }

    LaunchedEffect(vocabularyList) {
        if (isUpdated) {
            isUpdated = false
        } else {
            localVocabularyList = vocabularyList
            if (isFirst) {
                isFirst = false
                val index = localVocabularyList.indexOfFirst { it.id == startFromId }
                if (index >= 0) pagerState.scrollToPage(index)
            }
            else {
                pagerState.animateScrollToPage(0)
            }
        }
    }

    HorizontalPager(
        count = localVocabularyList.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val vocab = localVocabularyList[page]

        VocabularyCard(
            vocab = vocab,
            onSwipeUp = {
                update(vocab.copy(learned = false), onSwipeUp)
            },
            onSwipeDown = {
                update(vocab.copy(learned = true), onSwipeDown)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page + 1)
                }
            },
            onToggleImportant = {
                update(vocab.copy(important = !vocab.important), onToggleImportant)
            },
            onSpeak = { tts.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null) }
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = maxIndicatorCount,
            pageIndexMapping = { page -> page % maxIndicatorCount },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            activeColor = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
            inactiveColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
        )
    }
}

@Composable
fun VocabularyCard(
    vocab: Vocabulary,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onToggleImportant: () -> Unit,
    onSpeak: () -> Unit
) {
    val starFilled = vocab.important
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    val cardColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    val thresh = 300f

    Box {
        // drag word
        if (offsetY.value > 0f) {
            Text(
                text = "Learned",
                color = Color.Gray,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        translationY = max(offsetY.value - thresh, 0f)
                        alpha = (offsetY.value / thresh).coerceIn(0f, 1f)
                    }
                    .padding(30.dp)
            )
        } else if (offsetY.value < 0f) {
            Text(
                text = "Unlearned",
                color = Color.Gray,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        translationY = min(offsetY.value + thresh, 0f)
                        alpha = (-offsetY.value / thresh).coerceIn(0f, 1f)
                    }
                    .padding(30.dp)
            )
        }

        // content
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .fillMaxHeight(1f/3f)
                .padding(16.dp)
                .offset { IntOffset(x = 0, y = offsetY.value.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                when {
                                    offsetY.value > thresh -> {
                                        offsetY.animateTo(thresh*1.5f)
                                        onSwipeDown()
                                        offsetY.animateTo(0f)
                                    }
                                    offsetY.value < -thresh -> {
                                        offsetY.animateTo(-thresh*1.5f)
                                        onSwipeUp()
                                        offsetY.animateTo(0f)
                                    }
                                    else -> {
                                        offsetY.animateTo(0f)
                                    }
                                }
                            }
                        },
                        onVerticalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                offsetY.snapTo(offsetY.value + dragAmount)
                            }
                        }
                    )
                }
                .background(if (vocab.learned) cardColor.copy(alpha = 0.5f) else cardColor, RoundedCornerShape(24.dp))
        ) {
            var fontSize = 48.sp
            if (vocab.word.length > 15) fontSize = 24.sp
            else if (vocab.word.length > 10) fontSize = 32.sp
            else if (vocab.word.length > 8) fontSize = 40.sp

            // Speak
            IconButton(
                onClick = onSpeak,
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Pronounce",
                    modifier = Modifier.size(32.dp)
                )
            }
            // Important star
            IconButton(
                onClick = onToggleImportant,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Mark Important",
                    tint = if (starFilled) Color.Yellow else Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center).padding(20.dp)
            ) {
                Text(
                    text = vocab.word,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = vocab.partOfSpeech ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = textColor
                    )
                    Text(
                        text = vocab.level ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = vocab.definition,
                    fontSize = 24.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = vocab.note ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}

