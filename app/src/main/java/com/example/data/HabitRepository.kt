package com.example.data

import com.example.data.dao.BossChallengeDao
import com.example.data.dao.HabitDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.BossChallenge
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import com.example.data.model.HabitWithEntries
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitRepository(
    private val habitDao: HabitDao,
    private val userProfileDao: UserProfileDao,
    private val taskDao: TaskDao,
    private val bossChallengeDao: BossChallengeDao
) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val allEntries: Flow<List<HabitEntry>> = habitDao.getAllEntries()
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val activeBossChallenge: Flow<BossChallenge?> = bossChallengeDao.getActiveChallenge()

    fun getEntriesForDate(date: String): Flow<List<HabitEntry>> =
        habitDao.getEntriesForDate(date)

    fun getEntriesForHabit(habitId: Long): Flow<List<HabitEntry>> =
        habitDao.getEntriesForHabit(habitId)

    fun getCompletedDatesForHabit(habitId: Long): Flow<List<String>> =
        habitDao.getCompletedDatesForHabit(habitId)

    fun getHabitsOrderByCurrentStreak(): Flow<List<Habit>> =
        habitDao.getHabitsOrderByCurrentStreak()

    fun getHabitsOrderByBestStreak(): Flow<List<Habit>> =
        habitDao.getHabitsOrderByBestStreak()

    fun getHabitWithEntries(habitId: Long): Flow<HabitWithEntries?> =
        habitDao.getHabitWithEntries(habitId)

    fun getAllHabitsWithEntries(): Flow<List<HabitWithEntries>> =
        habitDao.getAllHabitsWithEntries()

    fun getDailyCompletionCount(date: String): Flow<Int> =
        habitDao.getDailyCompletionCount(date)

    suspend fun recalculateAndSaveStreak(habitId: Long) {
        val completedDates = habitDao.getCompletedDatesForHabitSync(habitId)
        val streakResult = StreakCalculator.calculate(completedDates)
        habitDao.updateHabitStreak(
            habitId = habitId,
            currentStreak = streakResult.currentStreak,
            bestStreak = streakResult.bestStreak,
            lastCompletedDate = streakResult.lastCompletedDate
        )
    }

    suspend fun insertHabit(habit: Habit): Long =
        habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) =
        habitDao.updateHabit(habit)

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
        habitDao.deleteAllEntriesForHabit(habitId)
    }

    suspend fun toggleHabitCompletion(habitId: Long, date: String, targetCount: Int = 1): Boolean {
        val todayStr = getTodayDateString()
        if (date > todayStr) {
            return false // Future day locked
        }

        val existing = habitDao.getEntry(habitId, date)
        if (existing == null) {
            habitDao.insertEntry(
                HabitEntry(
                    habitId = habitId,
                    date = date,
                    currentCount = 1,
                    targetCount = targetCount,
                    isCompleted = 1 >= targetCount
                )
            )
        } else if (existing.isCompleted) {
            habitDao.deleteEntry(habitId, date)
        } else {
            val newCount = existing.currentCount + 1
            habitDao.insertEntry(
                existing.copy(
                    currentCount = newCount,
                    isCompleted = newCount >= targetCount,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
        recalculateAndSaveStreak(habitId)
        return true
    }

    suspend fun incrementHabitCounter(habitId: Long, date: String, targetCount: Int = 1): Boolean {
        val todayStr = getTodayDateString()
        if (date > todayStr) return false

        val existing = habitDao.getEntry(habitId, date)
        if (existing == null) {
            habitDao.insertEntry(
                HabitEntry(
                    habitId = habitId,
                    date = date,
                    currentCount = 1,
                    targetCount = targetCount,
                    isCompleted = 1 >= targetCount
                )
            )
        } else {
            val newCount = existing.currentCount + 1
            habitDao.insertEntry(
                existing.copy(
                    currentCount = newCount,
                    isCompleted = newCount >= targetCount,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
        recalculateAndSaveStreak(habitId)
        return true
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateProfilePhotos(bannerUri: String?, avatarUri: String?) {
        val current = userProfileDao.getUserProfileSync() ?: UserProfile()
        userProfileDao.insertOrUpdateProfile(
            current.copy(
                bannerUri = bannerUri ?: current.bannerUri,
                avatarUri = avatarUri ?: current.avatarUri
            )
        )
    }

    suspend fun updateProfileDetails(name: String, headline: String, bio: String, occupations: String? = null) {
        val current = userProfileDao.getUserProfileSync() ?: UserProfile()
        userProfileDao.insertOrUpdateProfile(
            current.copy(
                userName = name,
                headline = headline,
                bio = bio,
                occupations = occupations ?: current.occupations
            )
        )
    }

    suspend fun addWorkspace(workspaceName: String) {
        val current = userProfileDao.getUserProfileSync() ?: UserProfile()
        val currentList = current.workspaces.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableList()
        val trimmed = workspaceName.trim()
        if (trimmed.isNotBlank() && !currentList.contains(trimmed)) {
            currentList.add(trimmed)
            userProfileDao.insertOrUpdateProfile(
                current.copy(workspaces = currentList.joinToString(", "))
            )
        }
    }

    suspend fun completeOnboarding(profile: UserProfile, selectedHabitNames: List<String>) {
        userProfileDao.insertOrUpdateProfile(profile.copy(isOnboardingCompleted = true))

        val starterList = selectedHabitNames.ifEmpty {
            listOf("Drink 2L Water", "Read 15 Pages", "10 Min Meditation")
        }

        val habitsToInsert = starterList.map { name ->
            mapPresetToHabit(name)
        }
        habitDao.insertHabits(habitsToInsert)
    }

    suspend fun resetOnboarding() {
        userProfileDao.setOnboardingCompleted(false)
    }

    // Task methods
    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

    suspend fun toggleTaskCompleted(taskId: Long, currentCompleted: Boolean) {
        val newStatus = !currentCompleted
        taskDao.setTaskCompleted(
            id = taskId,
            completed = newStatus,
            completedAt = if (newStatus) System.currentTimeMillis() else null
        )
        taskDao.updateTaskKanbanStatus(
            id = taskId,
            kanbanStatus = if (newStatus) "SHIPPED" else "TODO",
            completed = newStatus,
            completedAt = if (newStatus) System.currentTimeMillis() else null
        )
    }

    suspend fun updateTaskKanbanStatus(taskId: Long, newStatus: String) {
        val isShipped = newStatus == "SHIPPED"
        taskDao.updateTaskKanbanStatus(
            id = taskId,
            kanbanStatus = newStatus,
            completed = isShipped,
            completedAt = if (isShipped) System.currentTimeMillis() else null
        )
    }

    suspend fun deleteTask(taskId: Long) = taskDao.deleteTask(taskId)

    // Streak Freeze & Comeback Protocol methods
    suspend fun activateStreakFreeze(): Boolean {
        val profile = userProfileDao.getUserProfileSync() ?: return false
        if (profile.streakFreezesAvailable > 0) {
            val today = getTodayDateString()
            userProfileDao.updateStreakFreeze(
                available = profile.streakFreezesAvailable - 1,
                used = profile.streakFreezesUsed + 1,
                freezeDate = today
            )
            return true
        }
        return false
    }

    suspend fun setComebackProtocolActive(active: Boolean) {
        userProfileDao.setComebackProtocolActive(active)
    }

    // Boss Mode Challenge methods
    suspend fun startBossChallenge(challenge: BossChallenge): Long =
        bossChallengeDao.insertChallenge(challenge)

    suspend fun updateBossChallenge(challenge: BossChallenge) =
        bossChallengeDao.updateChallenge(challenge)

    suspend fun quitBossChallenge(id: Long, antiQuitPhrase: String) =
        bossChallengeDao.quitChallenge(id, antiQuitPhrase)

    suspend fun seedStarterTasksIfEmpty() {
        // No mock tasks seeded - user creates real tasks and workspaces
    }

    private fun mapPresetToHabit(name: String): Habit {
        return when (name) {
            "Drink 2L Water" -> Habit(
                name = "Drink 2L Water",
                category = "Health",
                iconName = "water",
                colorHex = "#0A84FF",
                targetCount = 8,
                unit = "glasses",
                timeOfDay = "Anytime"
            )
            "Read 15 Pages" -> Habit(
                name = "Read 15 Pages",
                category = "Mind",
                iconName = "book",
                colorHex = "#FF9F0A",
                targetCount = 1,
                unit = "session",
                timeOfDay = "Evening"
            )
            "10 Min Meditation" -> Habit(
                name = "10 Min Meditation",
                category = "Mind",
                iconName = "meditation",
                colorHex = "#BF5AF2",
                targetCount = 1,
                unit = "session",
                timeOfDay = "Morning"
            )
            "30 Min Exercise / Walk" -> Habit(
                name = "30 Min Exercise",
                category = "Health",
                iconName = "workout",
                colorHex = "#FF6A00",
                targetCount = 1,
                unit = "workout",
                timeOfDay = "Morning"
            )
            "Plan Top 3 Priorities" -> Habit(
                name = "Plan Top 3 Priorities",
                category = "Productivity",
                iconName = "target",
                colorHex = "#30D158",
                targetCount = 1,
                unit = "session",
                timeOfDay = "Morning"
            )
            "No Screens 30m Before Bed" -> Habit(
                name = "Screen-Free Winddown",
                category = "Sleep",
                iconName = "sleep",
                colorHex = "#64D2FF",
                targetCount = 1,
                unit = "night",
                timeOfDay = "Evening"
            )
            "Healthy Home Meal" -> Habit(
                name = "Nutritious Whole Meal",
                category = "Health",
                iconName = "meal",
                colorHex = "#30D158",
                targetCount = 1,
                unit = "meal",
                timeOfDay = "Afternoon"
            )
            "5 Min Morning Sunlight" -> Habit(
                name = "5 Min Morning Sunlight",
                category = "Routine",
                iconName = "sunlight",
                colorHex = "#FFD60A",
                targetCount = 1,
                unit = "viewing",
                timeOfDay = "Morning"
            )
            else -> Habit(
                name = name,
                category = "General",
                iconName = "target",
                colorHex = "#FF6A00",
                targetCount = 1,
                unit = "times",
                timeOfDay = "Anytime"
            )
        }
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

        fun getDateOffset(offsetDays: Int): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, offsetDays)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(cal.time)
        }

        fun isDateInFuture(dateStr: String): Boolean {
            return dateStr > getTodayDateString()
        }
    }
}
