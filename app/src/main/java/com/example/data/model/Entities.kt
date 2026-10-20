package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val email: String = "",
    val name: String = "",
    val age: Int = 25,
    val language: String = "العربية",
    val referralSource: String = "ذكاء اصطناعي",
    val ratingPercent: Int = 90,
    val isDarkMode: Boolean = false,
    val ringOnSilent: Boolean = true,
    val isLoggedIn: Boolean = false
)

enum class PlanType {
    DAILY,
    WEEKLY,
    MONTHLY
}

enum class TaskStatus {
    PENDING,
    DONE,        // تم
    PROCRASTINATED, // تسويف
    CANCELLED    // إلغاء
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val planType: PlanType = PlanType.DAILY,
    val status: TaskStatus = TaskStatus.PENDING,
    val timeStart: String? = null, // e.g. "01:00 PM"
    val timeEnd: String? = null,   // e.g. "03:00 PM"
    val orderIndex: Int = 0,
    val dayOfWeek: String? = null, // for weekly recurring
    val monthOfYear: String? = null, // for monthly recurring
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class HabitCategory {
    BREAK,     // التخلص منها
    COMMIT,    // الالتزام بها
    ACHIEVE,   // إنجازها
    DREAM      // تحقيق حلم
}

@Entity(tableName = "habits")
data class HabitItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: HabitCategory = HabitCategory.COMMIT,
    val voiceNotePath: String? = null,
    val currentStreak: Int = 0,
    val monthlyAutomationNote: String? = null, // e.g. "سوفت موعد الذهاب لصالة GYM" or "حققت التزام تام بمواعيد الصلاة"
    val isCompletedThisWeek: Boolean = false,
    val isCompletedThisMonth: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feelings")
data class FeelingEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val audioPath: String? = null,
    val moodTag: String = "متأمل",
    val aiResponse: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "badges")
data class UserBadge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

data class ApixChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedAction: String? = null
)
