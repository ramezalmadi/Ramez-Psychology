package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.remote.GeminiClient
import com.example.service.AlarmScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Onboarding : ScreenDestination()
    object TasksAndPlans : ScreenDestination()
    object Feelings : ScreenDestination()
    object ApixAi : ScreenDestination()
    object Ebook : ScreenDestination()
    object SettingsProfile : ScreenDestination()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()
    private val habitDao = db.habitDao()
    private val feelingDao = db.feelingDao()
    private val badgeDao = db.badgeDao()
    private val userProfileDao = db.userProfileDao()

    val userProfile: StateFlow<UserProfile?> = userProfileDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.TasksAndPlans)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _selectedPlanType = MutableStateFlow(PlanType.DAILY)
    val selectedPlanType: StateFlow<PlanType> = _selectedPlanType.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentTasks: StateFlow<List<TaskItem>> = _selectedPlanType.flatMapLatest { plan ->
        taskDao.getTasksForPlan(plan)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHabits: StateFlow<List<HabitItem>> = habitDao.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeelings: StateFlow<List<FeelingEntry>> = feelingDao.getAllFeelings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBadges: StateFlow<List<UserBadge>> = badgeDao.getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Apix Chat
    private val _apixChatMessages = MutableStateFlow<List<ApixChatMessage>>(
        listOf(
            ApixChatMessage(
                text = "مرحباً بك! أنا أبيكس (Apix)، مرشدك النفسي المستند لكتاب علم النفس السلوكي للدكتور رامز. كيف أساعدك اليوم في تنظيم أهدافك، مشاعرك، أو الإجابة على أي تساؤل في حياتك؟",
                isUser = false
            )
        )
    )
    val apixChatMessages: StateFlow<List<ApixChatMessage>> = _apixChatMessages.asStateFlow()
    val isApixThinking = MutableStateFlow(false)

    // Motivational popup state
    val latestEarnedBadge = MutableStateFlow<UserBadge?>(null)
    val motivationalToast = MutableStateFlow<String?>(null)

    // Voice / Command overlay state
    val isApixVoiceListening = MutableStateFlow(false)
    val apixVoiceFeedback = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            val profile = userProfileDao.getUserProfileOnce()
            if (profile == null || !profile.isLoggedIn) {
                _currentScreen.value = ScreenDestination.Onboarding
            } else {
                _currentScreen.value = ScreenDestination.TasksAndPlans
            }
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun setPlanType(type: PlanType) {
        _selectedPlanType.value = type
    }

    // --- Onboarding / Authentication ---
    fun completeOnboarding(
        email: String,
        name: String,
        age: Int,
        language: String,
        referralSource: String,
        ratingPercent: Int
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                id = 1,
                email = email.ifBlank { "user@example.com" },
                name = name.ifBlank { "صديق النجاح" },
                age = age,
                language = language,
                referralSource = referralSource,
                ratingPercent = ratingPercent,
                isLoggedIn = true,
                isDarkMode = userProfile.value?.isDarkMode ?: false,
                ringOnSilent = userProfile.value?.ringOnSilent ?: true
            )
            userProfileDao.insertOrUpdateProfile(profile)
            _currentScreen.value = ScreenDestination.TasksAndPlans
        }
    }

    fun logout() {
        viewModelScope.launch {
            val current = userProfileDao.getUserProfileOnce()
            if (current != null) {
                userProfileDao.insertOrUpdateProfile(current.copy(isLoggedIn = false))
            }
            _currentScreen.value = ScreenDestination.Onboarding
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = userProfileDao.getUserProfileOnce() ?: UserProfile()
            userProfileDao.insertOrUpdateProfile(current.copy(isDarkMode = enabled))
        }
    }

    fun toggleRingOnSilent(enabled: Boolean) {
        viewModelScope.launch {
            val current = userProfileDao.getUserProfileOnce() ?: UserProfile()
            userProfileDao.insertOrUpdateProfile(current.copy(ringOnSilent = enabled))
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            val current = userProfileDao.getUserProfileOnce() ?: UserProfile()
            userProfileDao.insertOrUpdateProfile(current.copy(language = lang))
        }
    }

    // --- Tasks Management ---
    fun addTask(title: String, timeStart: String? = null, timeEnd: String? = null, notes: String = "") {
        viewModelScope.launch {
            val currentList = currentTasks.value
            val task = TaskItem(
                title = title.trim(),
                planType = _selectedPlanType.value,
                timeStart = timeStart,
                timeEnd = timeEnd,
                orderIndex = currentList.size + 1,
                notes = notes
            )
            taskDao.insertTask(task)
        }
    }

    fun addMultipleTasksFromText(rawText: String) {
        viewModelScope.launch {
            // Parses phrases like: "عندي اليوم مقابلة عمل، موعد مع طبيب، لقاء صحفي، تنظيف سيارة، ذهاب لـ GYM ثم عودة للغداء ثم النوم ثم على الجوال أنمي"
            val delimiters = Regex("[,،\n]|\\s+ثم\\s+|\\s+بعدين\\s+|\\s+وبعدها\\s+")
            val parts = rawText.split(delimiters)
                .map { it.replace("عندي اليوم", "").replace("اليوم", "").replace("عندي", "").trim() }
                .filter { it.isNotBlank() && it.length > 1 }

            val currentSize = currentTasks.value.size
            val items = parts.mapIndexed { index, part ->
                TaskItem(
                    title = part,
                    planType = _selectedPlanType.value,
                    orderIndex = currentSize + index + 1
                )
            }
            taskDao.insertTasks(items)
            motivationalToast.value = "تم تنظيم ${items.size} مهام بتسلسل متناسق وفق خطتك!"
        }
    }

    fun updateTaskStatus(task: TaskItem, newStatus: TaskStatus) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(status = newStatus))

            if (newStatus == TaskStatus.DONE) {
                // Award motivational phrase and check badges
                val quotes = listOf(
                    "رائع! 'العادة هي جسر بين نية الإنسان ومستقبله الحقيقي' - د. رامز",
                    "إنجاز مبهر! خطوة للأمام في بناء الانضباط السلوكي.",
                    "عظيم! لقد تغلبت على المقاومة وصنعت زخماً حقيقياً اليوم.",
                    "بطل! كل 'تم' تقربك من نسختك الأفضل وتثبت قوتك الذهنية."
                )
                motivationalToast.value = quotes.random()

                // Unlock first_task badge
                badgeDao.unlockBadge("first_task")

                // If all tasks are completed
                val remaining = currentTasks.value.count { it.id != task.id && it.status != TaskStatus.DONE }
                if (remaining == 0) {
                    badgeDao.unlockBadge("no_procrastination")
                    val unlocked = allBadges.value.find { it.id == "no_procrastination" }
                    if (unlocked != null) {
                        latestEarnedBadge.value = unlocked.copy(isUnlocked = true)
                    }
                }
            } else if (newStatus == TaskStatus.PROCRASTINATED) {
                motivationalToast.value = "التسويف فرصة للفهم: 'قسم المهمة لخطوة 5 دقائق وابدأ الآن'."
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
        }
    }

    fun clearCurrentPlan() {
        viewModelScope.launch {
            taskDao.clearPlanTasks(_selectedPlanType.value)
        }
    }

    // --- Habits Management ---
    fun addHabit(title: String, category: HabitCategory, voicePath: String? = null) {
        viewModelScope.launch {
            habitDao.insertHabit(
                HabitItem(
                    title = title,
                    category = category,
                    voiceNotePath = voicePath,
                    currentStreak = 1
                )
            )
            motivationalToast.value = "تمت إضافة العادة بنجاح للمتابعة الأسبوعية والشهرية!"
            badgeDao.unlockBadge("habit_master")
        }
    }

    fun toggleHabitWeekCompletion(habit: HabitItem) {
        viewModelScope.launch {
            val updated = habit.copy(
                isCompletedThisWeek = !habit.isCompletedThisWeek,
                currentStreak = if (!habit.isCompletedThisWeek) habit.currentStreak + 1 else maxOf(0, habit.currentStreak - 1)
            )
            habitDao.updateHabit(updated)
        }
    }

    fun deleteHabit(habit: HabitItem) {
        viewModelScope.launch {
            habitDao.deleteHabit(habit)
        }
    }

    // --- Feelings Management ---
    fun addFeeling(text: String, audioPath: String? = null, moodTag: String = "متأمل") {
        viewModelScope.launch {
            val aiResponse = GeminiClient.askApix(
                "المستخدم يعبر عن مشاعره: '$text'. قدم تحليلاً سلوكياً دافئاً ومختصراً في سطرين مستنداً إلى مبادئ علم النفس السلوكي للدكتور رامز (فصل العاطفة عن الأحكام، قبول المشاعر، والخطوة العملية التالية)."
            )
            val entry = FeelingEntry(
                text = text,
                audioPath = audioPath,
                moodTag = moodTag,
                aiResponse = aiResponse
            )
            feelingDao.insertFeeling(entry)
            badgeDao.unlockBadge("emotional_clarity")
            motivationalToast.value = "تم حفظ مشاعرك في السجل مع التحليل السلوكي الآمن"
        }
    }

    fun deleteFeeling(entry: FeelingEntry) {
        viewModelScope.launch {
            feelingDao.deleteFeeling(entry)
        }
    }

    // --- Apix AI Chat ---
    fun sendApixMessage(messageText: String) {
        if (messageText.isBlank()) return
        val userMsg = ApixChatMessage(text = messageText, isUser = true)
        _apixChatMessages.value = _apixChatMessages.value + userMsg
        isApixThinking.value = true

        viewModelScope.launch {
            // First check if it's a voice/text action command
            val actionResult = handleApixActionCommand(messageText)
            val history = _apixChatMessages.value.map { it.text to it.isUser }
            val responseText = if (actionResult != null) {
                actionResult
            } else {
                GeminiClient.askApix(messageText, history)
            }

            _apixChatMessages.value = _apixChatMessages.value + ApixChatMessage(
                text = responseText,
                isUser = false
            )
            isApixThinking.value = false
            badgeDao.unlockBadge("mind_explorer")
        }
    }

    // Process commands like: "عيرلي منبه بعد نصف ساعه", "غيرلي الخطة من يوميه إلى اسبوعية", "خذيني على قسم..."
    fun handleApixActionCommand(command: String): String? {
        val lower = command.lowercase().trim()

        // Navigation commands
        if (lower.contains("خذيني") || lower.contains("انتقلي") || lower.contains("افتحي") || lower.contains("قسم")) {
            when {
                lower.contains("مهام") || lower.contains("خطط") || lower.contains("كتابة المهام") -> {
                    _currentScreen.value = ScreenDestination.TasksAndPlans
                    return "تم توجيهك إلى قسم المهام والخطط! 📋"
                }
                lower.contains("مشاعر") || lower.contains("تعبير") -> {
                    _currentScreen.value = ScreenDestination.Feelings
                    return "تم توجيهك إلى قسم تعبير عن المشاعر! 🌿"
                }
                lower.contains("كتاب") || lower.contains("ramez") || lower.contains("رامز") || lower.contains("علم النفس") -> {
                    _currentScreen.value = ScreenDestination.Ebook
                    return "تم نقلك إلى الكتاب الإلكتروني 'علم النفس السلوكي' للدكتور رامز! 📖"
                }
                lower.contains("ابيكس") || lower.contains("apix") -> {
                    _currentScreen.value = ScreenDestination.ApixAi
                    return "أنا معك هنا في قسم الذكاء الاصطناعي Apix! 🤖"
                }
                lower.contains("اعدادات") || lower.contains("ملف") || lower.contains("شخصي") -> {
                    _currentScreen.value = ScreenDestination.SettingsProfile
                    return "تم نقلك إلى شاشة الملف الشخصي والإعدادات! 👤"
                }
            }
        }

        // Plan change commands
        if (lower.contains("غيرلي الخطة") || lower.contains("حول الخطة") || lower.contains("اجعل الخطة")) {
            when {
                lower.contains("اسبوع") -> {
                    _selectedPlanType.value = PlanType.WEEKLY
                    return "تم تغيير الخطة إلى الخطة الأسبوعية مع إمكانية تكرار المهام للأسبوع كاملاً! 🗓️"
                }
                lower.contains("شهر") -> {
                    _selectedPlanType.value = PlanType.MONTHLY
                    return "تم تغيير الخطة إلى الخطة الشهرية مع الجدولة والأتمتة التلقائية! 📅"
                }
                lower.contains("يوم") -> {
                    _selectedPlanType.value = PlanType.DAILY
                    return "تم تعيين الخطة اليومية لإنجاز مهام اليوم! ☀️"
                }
            }
        }

        // Alarm timer commands
        if (lower.contains("عير") || lower.contains("منبه") || lower.contains("تذكير") || lower.contains("نبهني")) {
            val minutes = when {
                lower.contains("نصف ساعه") || lower.contains("نصف ساعة") -> 30
                lower.contains("ساعه") || lower.contains("ساعة") -> 60
                lower.contains("ربع ساعه") || lower.contains("ربع ساعة") -> 15
                lower.contains("10") || lower.contains("عشر") -> 10
                lower.contains("5") || lower.contains("خمس") -> 5
                else -> 25 // default pomodoro
            }
            val ringOnSilent = userProfile.value?.ringOnSilent ?: true
            AlarmScheduler.scheduleTimer(
                getApplication(),
                hours = 0,
                minutes = minutes,
                seconds = 0,
                taskTitle = "تنبيه ذكي من Apix",
                ringOnSilent = ringOnSilent
            )
            return "تم ضبط المنبه بنجاح بعد $minutes دقيقة! سأقوم بتنبيهك بالرنين والاهتزاز حتى لا يفوتك موعدك. ⏰"
        }

        // Add task command: "ضيفلي على القائمة كذا كذا"
        if (lower.startsWith("ضيف") || lower.startsWith("اضف") || lower.contains("ضيفلي")) {
            val taskName = command.replace("ضيفلي على القائمة", "")
                .replace("ضيفلي", "")
                .replace("اضف", "")
                .replace("ضيف", "")
                .trim()
            if (taskName.isNotBlank()) {
                addTask(taskName)
                return "تمت إضافة المهمة: '$taskName' إلى خطتك بنجاح! 🎯"
            }
        }

        // Delete command: "احذفلي كذا"
        if (lower.startsWith("احذف") || lower.contains("احذفلي")) {
            val taskName = command.replace("احذفلي", "")
                .replace("احذف", "")
                .trim()
            val match = currentTasks.value.find { it.title.contains(taskName, ignoreCase = true) }
            if (match != null) {
                deleteTask(match.id)
                return "تم حذف المهمة: '${match.title}' من خطتك! 🗑️"
            }
        }

        return null
    }

    // Schedule exact custom alarm dialog
    fun setCustomAlarm(hours: Int, minutes: Int, seconds: Int, title: String) {
        val ringOnSilent = userProfile.value?.ringOnSilent ?: true
        AlarmScheduler.scheduleTimer(
            getApplication(),
            hours,
            minutes,
            seconds,
            title,
            ringOnSilent
        )
        motivationalToast.value = "تم تعيير المنبه بنجاح لـ $title ($hours س, $minutes د, $seconds ث)"
    }
}
