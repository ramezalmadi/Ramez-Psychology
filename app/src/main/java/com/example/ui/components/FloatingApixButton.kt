package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.ScreenDestination
import com.example.ui.theme.ApixGlow
import com.example.ui.theme.BluePrimaryLight

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloatingApixButton(
    onNavigateToApix: () -> Unit,
    onExecuteVoiceCommand: (String) -> String?,
    modifier: Modifier = Modifier
) {
    var showQuickDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var voiceInputText by remember { mutableStateOf("") }
    var executionResult by remember { mutableStateOf<String?>(null) }

    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .padding(16.dp)
            .scale(pulseScale)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ApixGlow,
                        BluePrimaryLight,
                        Color(0xFF0F172A)
                    )
                )
            )
            .combinedClickable(
                onClick = { showQuickDialog = true },
                onLongClick = {
                    showVoiceDialog = true
                    voiceInputText = ""
                    executionResult = null
                }
            )
            .padding(14.dp)
            .testTag("floating_apix_button"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Apix AI",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "Apix",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Quick Tap Dialog
    if (showQuickDialog) {
        AlertDialog(
            onDismissRequest = { showQuickDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "مساعدك الذكي Apix",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "أنا أبيكس! يمكنك سؤالي عن أي شيء في علم النفس السلوكي، تنظيم حياتك، أو التحدث معي صوتياً.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💡 نصيحة: اضغط مطولاً على دائرة Apix لتتحول إلى ميكروفون وتسمع أوامرك الصوتية مباشرة!",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showQuickDialog = false
                        onNavigateToApix()
                    }
                ) {
                    Text("فتح قسم Apix بالكامل")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showQuickDialog = false
                        showVoiceDialog = true
                    }
                ) {
                    Text("تحدث صوتياً")
                }
            }
        )
    }

    // Voice Long-Press Dialog
    if (showVoiceDialog) {
        Dialog(onDismissRequest = { showVoiceDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "استماع",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Apix يستمع إليك الآن...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "قل مثلاً:\n• عيرلي منبه بعد نصف ساعه\n• غيرلي الخطة من يومية إلى أسبوعية\n• ضيفلي على القائمة مقابلة عمل\n• خذيني على قسم تعبير المشاعر",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )

                    OutlinedTextField(
                        value = voiceInputText,
                        onValueChange = { voiceInputText = it },
                        placeholder = { Text("أو اكتب أمرك هنا وسينفذه Apix فوراً...") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (voiceInputText.isNotBlank()) {
                                        val res = onExecuteVoiceCommand(voiceInputText)
                                        executionResult = res ?: "تم استلام الأمر ومعالجته بواسطة Apix!"
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "إرسال")
                            }
                        }
                    )

                    // Quick voice command presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                voiceInputText = "عيرلي منبه بعد نصف ساعه"
                                val res = onExecuteVoiceCommand(voiceInputText)
                                executionResult = res
                            },
                            label = { Text("منبه 30د", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                voiceInputText = "غيرلي الخطة إلى أسبوعية"
                                val res = onExecuteVoiceCommand(voiceInputText)
                                executionResult = res
                            },
                            label = { Text("خطة أسبوعية", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                voiceInputText = "خذيني على قسم Ramez psychology"
                                val res = onExecuteVoiceCommand(voiceInputText)
                                executionResult = res
                            },
                            label = { Text("كتاب رامز", fontSize = 11.sp) }
                        )
                    }

                    executionResult?.let { result ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = result,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showVoiceDialog = false }) {
                            Text("إغلاق")
                        }
                    }
                }
            }
        }
    }
}
