package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AlarmTimerDialog(
    initialTitle: String = "مهمتي",
    initialRingOnSilent: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (hours: Int, minutes: Int, seconds: Int, title: String, ringOnSilent: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(30) }
    var seconds by remember { mutableStateOf(0) }
    var ringOnSilent by remember { mutableStateOf(initialRingOnSilent) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("alarm_timer_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "المنبه",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "تعيير منبه وتذكير المهمة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم المهمة / الموعد") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "حدد المدة: ساعة : دقيقة : ثانية",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Pickers row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerColumn(
                        label = "ساعة",
                        value = hours,
                        onIncrement = { if (hours < 23) hours++ },
                        onDecrement = { if (hours > 0) hours-- }
                    )
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    TimePickerColumn(
                        label = "دقيقة",
                        value = minutes,
                        onIncrement = { if (minutes < 59) minutes++ else minutes = 0 },
                        onDecrement = { if (minutes > 0) minutes-- else minutes = 59 }
                    )
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    TimePickerColumn(
                        label = "ثانية",
                        value = seconds,
                        onIncrement = { if (seconds < 59) seconds += 10 else seconds = 0 },
                        onDecrement = { if (seconds >= 10) seconds -= 10 else seconds = 0 }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Silent mode override option
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إذا كان هاتفك في وضع صامت:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (ringOnSilent) "سيتم إصدار صوت رنين واهتزاز قوي" else "اهتزاز وتنبيه صامت فقط دون رنين",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = ringOnSilent,
                            onCheckedChange = { ringOnSilent = it },
                            thumbContent = {
                                Icon(
                                    imageVector = if (ringOnSilent) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            onConfirm(hours, minutes, seconds, title, ringOnSilent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تعيير المنبه")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimePickerColumn(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FilledTonalIconButton(onClick = onIncrement, modifier = Modifier.size(36.dp)) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = "%02d".format(value),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        FilledTonalIconButton(onClick = onDecrement, modifier = Modifier.size(36.dp)) {
            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
