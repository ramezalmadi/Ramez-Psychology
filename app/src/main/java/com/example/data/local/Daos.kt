package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE planType = :planType ORDER BY orderIndex ASC, id ASC")
    fun getTasksForPlan(planType: PlanType): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM tasks WHERE planType = :planType")
    suspend fun clearPlanTasks(planType: PlanType)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun getAllHabits(): Flow<List<HabitItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitItem): Long

    @Update
    suspend fun updateHabit(habit: HabitItem)

    @Delete
    suspend fun deleteHabit(habit: HabitItem)
}

@Dao
interface FeelingDao {
    @Query("SELECT * FROM feelings ORDER BY timestamp DESC")
    fun getAllFeelings(): Flow<List<FeelingEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeling(entry: FeelingEntry): Long

    @Delete
    suspend fun deleteFeeling(entry: FeelingEntry)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<UserBadge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<UserBadge>)

    @Update
    suspend fun updateBadge(badge: UserBadge)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)
}
