package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.AlarmTimerDialog
import com.example.ui.theme.CancelRed
import com.example.ui.theme.GoldMedal
import com.example.ui.theme.ProcrastinateOrange
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlansTasksScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val selectedPlan by viewModel.selectedPlanType.collectAsState()
    val tasks by viewModel.currentTasks.collectAsState()
    val habits by viewModel.allHabits.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAlarmDialogForTask by remember { mutableStateOf<TaskItem?>(null) }
    var showHabitsModal by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    // Voice dictation state
    var isDictatingByVoice by remember { mutableStateOf(false) }
    var quickInputText by remember { mutableStateOf("") }

    // Computed real-time statistics
    val totalTasks = tasks.size
    val doneTasks = tasks.count { it.status == TaskStatus.DONE }
    val procrastinatedTasks = tasks.count { it.status == TaskStatus.PROCRASTINATED }
    val cancelledTasks = tasks.count { it.status == TaskStatus.CANCELLED }
    val progressPercent = if (totalTasks > 0) (doneTasks.toFloat() / totalTasks.toFloat()) else 0f

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("plans_tasks_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "RAMEZ PSYCHOLOGY",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "تنظيم الخطط والعادات والمواعيد",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showHabitsModal = true }) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "العادات والأتمتة",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "التقرير الدوري",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTaskDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("إضافة مهمة") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_task_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plan Selector Tabs
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PlanTabItem(
                            title = "خطة يومية",
                            isSelected = selectedPlan == PlanType.DAILY,
                            onClick = { viewModel.setPlanType(PlanType.DAILY) },
                            modifier = Modifier.weight(1f)
                        )
                        PlanTabItem(
                            title = "خطة أسبوعية",
                            isSelected = selectedPlan == PlanType.WEEKLY,
                            onClick = { viewModel.setPlanType(PlanType.WEEKLY) },
                            modifier = Modifier.weight(1f)
                        )
                        PlanTabItem(
                            title = "خطة شهرية",
                            isSelected = selectedPlan == PlanType.MONTHLY,
                            onClick = { viewModel.setPlanType(PlanType.MONTHLY) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Quick Voice/Text Dictation Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تسجيل صوتي أو كتابي للمهام المرتبة:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FilledIconButton(
                                onClick = {
                                    isDictatingByVoice = !isDictatingByVoice
                                    if (isDictatingByVoice) {
                                        // Sample voice command
                                        quickInputText = "مقابلة عمل، موعد مع طبيب، لقاء صحفي، تنظيف سيارة، ذهاب لـ GYM ثم عودة للغداء ثم النوم ثم على الجوال أنمي"
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (isDictatingByVoice) SuccessGreen else MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "تسجيل صوتي",
                                    tint = if (isDictatingByVoice) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = quickInputText,
                            onValueChange = { quickInputText = it },
                            placeholder = {
                                Text("تحدث أو اكتب أعمالك: مثلاً مقابلة عمل، موعد طبيب، تنظيف سيارة، ذهاب للرياضة ثم غداء...")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedPlan == PlanType.WEEKLY) "تتكرر للمدة أسبوعياً" else if (selectedPlan == PlanType.MONTHLY) "تتكرر شهرياً مع أتمتة" else "تُنظم تلقائياً بترتيب زمني",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    if (quickInputText.isNotBlank()) {
                                        viewModel.addMultipleTasksFromText(quickInputText)
                                        quickInputText = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("تنظيم وجدولة المهام", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Real-Time Summary & Exam Score Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ملخص الخطة اللحظي (نظام التقييم والامتحان):",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(progressPercent * 100).toInt()}% مكتمل",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SuccessGreen
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatChip(label = "تم", count = doneTasks, color = SuccessGreen)
                            StatChip(label = "تسويف", count = procrastinatedTasks, color = ProcrastinateOrange)
                            StatChip(label = "إلغاء", count = cancelledTasks, color = CancelRed)
                            StatChip(label = "المتبقي", count = tasks.count { it.status == TaskStatus.PENDING }, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Task List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قائمة المهام (${tasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (tasks.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCurrentPlan() }) {
                            Text("تعديل الخطة / مسح الكل", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAddCheck,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "لا توجد مهام مسجلة في هذه الخطة حالياً",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "استخدم الميكروفون بالأعلى لتملي أعمالك كـ (مقابلة عمل، زيارة طبيب...) وسيرتبها التطبيق بالترتيب!",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(tasks, key = { _, item -> item.id }) { index, task ->
                    TaskExamItemCard(
                        index = index + 1,
                        task = task,
                        onStatusChanged = { newStatus ->
                            viewModel.updateTaskStatus(task, newStatus)
                        },
                        onAlarmClick = {
                            showAlarmDialogForTask = task
                        },
                        onDeleteClick = {
                            viewModel.deleteTask(task.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        var newTitle by remember { mutableStateOf("") }
        var startTime by remember { mutableStateOf("01:00 PM") }
        var endTime by remember { mutableStateOf("03:00 PM") }
        var setAlarmDirectly by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("إضافة مهمة جديدة وحصر وقتها") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("عنوان المهمة (مثال: مقابلة عمل)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_task_title_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("من") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("إلى") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تعيير منبه عند انتهاء الوقت (3:00 PM)")
                        Switch(checked = setAlarmDirectly, onCheckedChange = { setAlarmDirectly = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addTask(newTitle, startTime, endTime)
                            if (setAlarmDirectly) {
                                viewModel.setCustomAlarm(1, 30, 0, newTitle)
                            }
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("إضافة وجدولة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Alarm Dialog for Specific Task
    showAlarmDialogForTask?.let { task ->
        AlarmTimerDialog(
            initialTitle = task.title,
            initialRingOnSilent = userProfile?.ringOnSilent ?: true,
            onDismiss = { showAlarmDialogForTask = null },
            onConfirm = { hours, minutes, seconds, title, ringOnSilent ->
                viewModel.toggleRingOnSilent(ringOnSilent)
                viewModel.setCustomAlarm(hours, minutes, seconds, title)
                showAlarmDialogForTask = null
            }
        )
    }

    // Habits & Automation Modal Dialog
    if (showHabitsModal) {
        HabitsAutomationDialog(
            habits = habits,
            onDismiss = { showHabitsModal = false },
            onAddHabit = { title, cat -> viewModel.addHabit(title, cat) },
            onToggleHabit = { habit -> viewModel.toggleHabitWeekCompletion(habit) },
            onDeleteHabit = { habit -> viewModel.deleteHabit(habit) }
        )
    }

    // Report Dialog
    if (showReportDialog) {
        PeriodicReportDialog(
            planType = selectedPlan,
            total = totalTasks,
            done = doneTasks,
            procrastinated = procrastinatedTasks,
            cancelled = cancelledTasks,
            habits = habits,
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
fun TaskExamItemCard(
    index: Int,
    task: TaskItem,
    onStatusChanged: (TaskStatus) -> Unit,
    onAlarmClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (task.status) {
                TaskStatus.DONE -> SuccessGreen.copy(alpha = 0.08f)
                TaskStatus.PROCRASTINATED -> ProcrastinateOrange.copy(alpha = 0.08f)
                TaskStatus.CANCELLED -> CancelRed.copy(alpha = 0.08f)
                TaskStatus.PENDING -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!task.timeStart.isNullOrBlank() || !task.timeEnd.isNullOrBlank()) {
                            Text(
                                text = "⏰ الموعد: ${task.timeStart ?: "الآن"} إلى ${task.timeEnd ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onAlarmClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "تعيير منبه",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف المهمة",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Exam Style Option Circles: [تم] [تسويف] [إلغاء]
            Text(
                text = "اختر ما قمت به (كنظام الامتحان):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExamChoiceCircle(
                    label = "تم",
                    isSelected = task.status == TaskStatus.DONE,
                    activeColor = SuccessGreen,
                    icon = Icons.Default.CheckCircle,
                    onClick = { onStatusChanged(TaskStatus.DONE) }
                )

                ExamChoiceCircle(
                    label = "تسويف",
                    isSelected = task.status == TaskStatus.PROCRASTINATED,
                    activeColor = ProcrastinateOrange,
                    icon = Icons.Default.HourglassTop,
                    onClick = { onStatusChanged(TaskStatus.PROCRASTINATED) }
                )

                ExamChoiceCircle(
                    label = "إلغاء",
                    isSelected = task.status == TaskStatus.CANCELLED,
                    activeColor = CancelRed,
                    icon = Icons.Default.Cancel,
                    onClick = { onStatusChanged(TaskStatus.CANCELLED) }
                )
            }
        }
    }
}

@Composable
fun ExamChoiceCircle(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, activeColor) else null,
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else Color.Transparent)
                    .border(1.5.dp, if (isSelected) activeColor else MaterialTheme.colorScheme.outline, CircleShape)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PlanTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatChip(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HabitsAutomationDialog(
    habits: List<HabitItem>,
    onDismiss: () -> Unit,
    onAddHabit: (String, HabitCategory) -> Unit,
    onToggleHabit: (HabitItem) -> Unit,
    onDeleteHabit: (HabitItem) -> Unit
) {
    var habitTitle by remember { mutableStateOf("") }
    var habitCategory by remember { mutableStateOf(HabitCategory.COMMIT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إدارة العادات السلوكية والأتمتة الشهرية") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تسجيل العادات المراد التخلص منها أو الالتزام بها أو إنجازها أو تحقيق حلم:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = habitTitle,
                    onValueChange = { habitTitle = it },
                    placeholder = { Text("اسم العادة (مثال: الذهاب للرياضة، الصلاة...)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = habitCategory == HabitCategory.COMMIT,
                        onClick = { habitCategory = HabitCategory.COMMIT },
                        label = { Text("التزام", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = habitCategory == HabitCategory.BREAK,
                        onClick = { habitCategory = HabitCategory.BREAK },
                        label = { Text("تخلص", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = habitCategory == HabitCategory.ACHIEVE,
                        onClick = { habitCategory = HabitCategory.ACHIEVE },
                        label = { Text("إنجاز", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = habitCategory == HabitCategory.DREAM,
                        onClick = { habitCategory = HabitCategory.DREAM },
                        label = { Text("حلم", fontSize = 11.sp) }
                    )
                }

                Button(
                    onClick = {
                        if (habitTitle.isNotBlank()) {
                            onAddHabit(habitTitle, habitCategory)
                            habitTitle = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ العادة في جدول المتابعة")
                }

                Divider()

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(habits.size) { i ->
                        val h = habits[i]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = h.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = "التتابع: ${h.currentStreak} أيام متتالية | ${h.category.name}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    h.monthlyAutomationNote?.let { note ->
                                        Text(
                                            text = "🤖 أتمتة شهرية: $note",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                                IconButton(onClick = { onDeleteHabit(h) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
fun PeriodicReportDialog(
    planType: PlanType,
    total: Int,
    done: Int,
    procrastinated: Int,
    cancelled: Int,
    habits: List<HabitItem>,
    onDismiss: () -> Unit
) {
    val rate = if (total > 0) ((done.toFloat() / total) * 100).toInt() else 100

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("التقرير الدوري والأتمتة السلوكية")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val planTitle = when (planType) {
                    PlanType.DAILY -> "اليوم"
                    PlanType.WEEKLY -> "الأسبوعي"
                    PlanType.MONTHLY -> "الشهري"
                }
                Text(
                    text = "تقرير $planTitle:",
                    fontWeight = FontWeight.Bold
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("• نسبة الإنجاز العام: $rate%", fontWeight = FontWeight.Bold)
                        Text("• مهام تم إنجازها: $done")
                        Text("• مهام تم تسويفها: $procrastinated (راجع استراتيجية الخطوة الصغيرة)")
                        Text("• مهام تم إلغاؤها: $cancelled")
                    }
                }

                Text(
                    text = "أتمتة الشهر وتحليل العادات السلوكية:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = "• سوفت هذا الشهر موعد الذهاب لصالة GYM الرياضية 3 مرات ثم تم التدارك.\n• حققت هذا الشهر إلتزاماً تاماً بمواعيد الصلاة بنسبة 96%.\n• التخلص من عادة تصفح الهاتف قبل النوم يظهر تحسناً بمقدار 5 أيام متتالية.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("حسناً")
            }
        }
    )
}
