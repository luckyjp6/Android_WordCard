package com.example.wordcardsapp.ui.component

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.wordcardsapp.data.local.Vocabulary
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
fun VocabularyList(
    tts: TextToSpeech,
    vocabularyList: List<Vocabulary>,
    onSwipeLeft: (Vocabulary) -> Unit,
    onSwipeRight: (Vocabulary) -> Unit,
    onToggleImportant: (Vocabulary) -> Unit,
    onClickWord: (Int) -> Unit
) {
    LazyColumn {
        items(vocabularyList) { vocab ->
            VocabularyListItem(
                vocab = vocab,
                onSwipeLeft = { onSwipeLeft(vocab) },
                onSwipeRight = { onSwipeRight(vocab) },
                onToggleImportant = { onToggleImportant(vocab) },
                onSpeak = { tts.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null)},
                onClick = { onClickWord(vocab.id) }
            )
        }
    }
}

@Composable
fun VocabularyListItem(
    vocab: Vocabulary,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onToggleImportant: () -> Unit,
    onSpeak: () -> Unit,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val cardColor = MaterialTheme.colorScheme.surfaceVariant
    val starEdgeColor = MaterialTheme.colorScheme.onSurfaceVariant
    val boldTextColor = MaterialTheme.colorScheme.onSurface
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    val thresh = 200f

    Box(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        // drag word
        if (offsetX.value > 0f) {
            Text(
                text = "Learned",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .graphicsLayer {
                        translationX = min(offsetX.value - thresh, 0f)
                        alpha = (offsetX.value / thresh).coerceIn(0f, 1f)
                    }
                    .padding(start = 16.dp)
            )
        } else if (offsetX.value < 0f) {
            Text(
                text = "UnLearned",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .graphicsLayer {
                        translationX = max(offsetX.value + thresh, 0f)
                        alpha = (-offsetX.value / thresh).coerceIn(0f, 1f)
                    }
                    .padding(end = 16.dp)
            )
        }

        // content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = offsetX.value.roundToInt(), y = 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                when {
                                    offsetX.value > thresh -> {
                                        offsetX.animateTo(thresh*1.5f)
                                        onSwipeRight()
                                        offsetX.animateTo(0f)
                                    }
                                    offsetX.value < -thresh -> {
                                        offsetX.animateTo(-thresh*1.5f)
                                        onSwipeLeft()
                                        offsetX.animateTo(0f)
                                    }
                                    else -> {
                                        offsetX.animateTo(0f)
                                    }
                                }
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount)
                            }
                        }
                    )
                }
                .clickable(onClick = onClick)
                .background(if (!vocab.learned) cardColor else cardColor.copy(alpha = 0f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // word & info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vocab.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = boldTextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${vocab.partOfSpeech ?: ""} ${vocab.definition}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }

            // buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleImportant) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Mark important",
                        tint = if (vocab.important) Color.Yellow else MaterialTheme.colorScheme.onBackground,
                    )
                }

                IconButton(onClick = onSpeak) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Pronounce",
                        tint = if (!vocab.learned) starEdgeColor else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}