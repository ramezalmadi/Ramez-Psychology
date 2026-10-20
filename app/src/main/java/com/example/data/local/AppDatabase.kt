package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        TaskItem::class,
        HabitItem::class,
        FeelingEntry::class,
        UserBadge::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun feelingDao(): FeelingDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ramez_psychology_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.badgeDao().insertBadges(getDefaultBadges())
                            // Preload some default psychology habits
                            database.habitDao().insertHabit(
                                HabitItem(
                                    title = "الالتزام التام بمواعيد الصلاة",
                                    category = HabitCategory.COMMIT,
                                    currentStreak = 12,
                                    monthlyAutomationNote = "حققت هذا الشهر التزام تام بمواعيد الصلاة"
                                )
                            )
                            database.habitDao().insertHabit(
                                HabitItem(
                                    title = "الذهاب إلى صالة GYM الرياضية",
                                    category = HabitCategory.COMMIT,
                                    currentStreak = 4,
                                    monthlyAutomationNote = "سوفت هذا الشهر موعد الذهاب لصالة الرياضية ثم تم تداركه"
                                )
                            )
                            database.habitDao().insertHabit(
                                HabitItem(
                                    title = "التخلص من إدمان تصفح الجوال قبل النوم",
                                    category = HabitCategory.BREAK,
                                    currentStreak = 3
                                )
                            )
                            database.habitDao().insertHabit(
                                HabitItem(
                                    title = "قراءة كتاب علم النفس السلوكي للدكتور رامز",
                                    category = HabitCategory.ACHIEVE,
                                    currentStreak = 7
                                )
                            )
                            database.habitDao().insertHabit(
                                HabitItem(
                                    title = "إطلاق مشروعي الخاص وتحقيق الاستقلال المالي",
                                    category = HabitCategory.DREAM,
                                    currentStreak = 1
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun getDefaultBadges(): List<UserBadge> {
            return listOf(
                UserBadge(
                    id = "first_task",
                    title = "وسام البداية القوية",
                    description = "أكملت أول مهمة لك بنجاح!",
                    iconName = "Star"
                ),
                UserBadge(
                    id = "no_procrastination",
                    title = "قاهر التسويف",
                    description = "أنجزت جميع مهامك اليومية دون تسويف",
                    iconName = "CheckCircle"
                ),
                UserBadge(
                    id = "habit_master",
                    title = "سيد العادات الإيجابية",
                    description = "حافظت على عادة إيجابية لأكثر من 7 أيام متتالية",
                    iconName = "EmojiEvents"
                ),
                UserBadge(
                    id = "mind_explorer",
                    title = "مستكشف علم النفس السلوكي",
                    description = "قرأت حالات من كتاب د. رامز وتفاعلت مع Apix",
                    iconName = "Psychology"
                ),
                UserBadge(
                    id = "emotional_clarity",
                    title = "وسام الوعي الذاتي",
                    description = "عبرت عن مشاعرك بصدق ووثقت حالتك النفسية",
                    iconName = "Favorite"
                ),
                UserBadge(
                    id = "dream_achiever",
                    title = "صانع الأحلام",
                    description = "أحرزت تقدماً ملموساً في عادة تحقيق الحلم",
                    iconName = "MilitaryTech"
                )
            )
        }
    }
}
