package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitRepository
import com.example.data.model.BossChallenge
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HabitWithEntry(
    val habit: Habit,
    val entry: HabitEntry?,
    val isCompleted: Boolean,
    val currentCount: Int,
    val targetCount: Int,
    val streak: Int
)

data class DayItem(
    val dateString: String, // yyyy-MM-dd
    val dayOfWeek: String, // M, T, W, etc.
    val dayOfMonth: Int, // 1..31
    val isToday: Boolean,
    val isSelected: Boolean,
    val isFuture: Boolean,
    val completionRatio: Float = 0f
)

data class DashboardUiState(
    val selectedDate: String = HabitRepository.getTodayDateString(),
    val selectedFilter: String = "All", // "All", "Morning", "Afternoon", "Evening"
    val currentTab: Int = 0, // 0 = Today, 1 = Tasks, 2 = Boss Mode, 3 = Analytics, 4 = Profile
    val isAddHabitOpen: Boolean = false,
    val editingHabit: Habit? = null,
    val habitDetail: Habit? = null,
    val isAddTaskOpen: Boolean = false,
    val editingTask: TaskItem? = null,
    val futureDateAlert: String? = null,
    val taskViewMode: String = "BENTO", // "BENTO", "EISENHOWER", "KANBAN"
    val todayViewMode: String = "HABITS", // "HABITS", "CIRCUIT"
    val particleTrigger: Long = 0L,
    val particleStyle: com.example.ui.components.ParticleStyle = com.example.ui.components.ParticleStyle.GOLDEN_SPARKS
)

class HabitDashboardViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isProfileLoaded: StateFlow<Boolean> = repository.userProfile
        .map { true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allHabits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEntries: StateFlow<List<HabitEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBossChallenge: StateFlow<BossChallenge?> = repository.activeBossChallenge
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isDateLocked: StateFlow<Boolean> = combine(_uiState) { state ->
        HabitRepository.isDateInFuture(state[0].selectedDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Combined habits with their entry status for the currently selected date
    val habitsForSelectedDate: StateFlow<List<HabitWithEntry>> = combine(
        allHabits,
        allEntries,
        _uiState
    ) { habits, entries, state ->
        val dateEntries = entries.filter { it.date == state.selectedDate }.associateBy { it.habitId }

        habits.map { habit ->
            val entry = dateEntries[habit.id]
            val currentCount = entry?.currentCount ?: 0
            val target = habit.targetCount
            val isCompleted = entry?.isCompleted ?: (currentCount >= target)
            val streak = computeStreakForHabit(habit.id, entries)

            HabitWithEntry(
                habit = habit,
                entry = entry,
                isCompleted = isCompleted,
                currentCount = currentCount,
                targetCount = target,
                streak = streak
            )
        }.filter { item ->
            if (state.selectedFilter == "All") true
            else item.habit.timeOfDay.equals(state.selectedFilter, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Generate 7-day strip centered on current week
    val weekDays: StateFlow<List<DayItem>> = combine(
        allEntries,
        allHabits,
        _uiState
    ) { entries, habits, state ->
        val list = mutableListOf<DayItem>()
        val cal = Calendar.getInstance()
        val todayStr = HabitRepository.getTodayDateString()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayNameFormat = SimpleDateFormat("EEE", Locale.US)

        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0 until 7) {
            val dateStr = sdf.format(cal.time)
            val dayName = dayNameFormat.format(cal.time).take(1)
            val dayNum = cal.get(Calendar.DAY_OF_MONTH)
            val isFuture = dateStr > todayStr

            val totalForDate = habits.size
            val completedForDate = if (totalForDate > 0) {
                val doneCount = entries.count { it.date == dateStr && it.isCompleted }
                doneCount.toFloat() / totalForDate
            } else 0f

            list.add(
                DayItem(
                    dateString = dateStr,
                    dayOfWeek = dayName,
                    dayOfMonth = dayNum,
                    isToday = dateStr == todayStr,
                    isSelected = dateStr == state.selectedDate,
                    isFuture = isFuture,
                    completionRatio = completedForDate.coerceIn(0f, 1f)
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedStarterTasksIfEmpty()
        }
    }

    fun selectDate(date: String) {
        val today = HabitRepository.getTodayDateString()
        val isFuture = date > today
        _uiState.update {
            it.copy(
                selectedDate = date,
                futureDateAlert = if (isFuture) "Viewing future date ($date). Habits are locked until this day arrives." else null
            )
        }
    }

    fun dismissFutureAlert() {
        _uiState.update { it.copy(futureDateAlert = null) }
    }

    fun selectFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun openAddHabit() {
        _uiState.update { it.copy(isAddHabitOpen = true, editingHabit = null) }
    }

    fun openEditHabit(habit: Habit) {
        _uiState.update { it.copy(isAddHabitOpen = true, editingHabit = habit) }
    }

    fun closeAddHabit() {
        _uiState.update { it.copy(isAddHabitOpen = false, editingHabit = null) }
    }

    fun showHabitDetail(habit: Habit?) {
        _uiState.update { it.copy(habitDetail = habit) }
    }

    fun toggleCompletion(habit: Habit) {
        viewModelScope.launch {
            val success = repository.toggleHabitCompletion(habit.id, _uiState.value.selectedDate, habit.targetCount)
            if (!success) {
                _uiState.update {
                    it.copy(futureDateAlert = "Future days are locked! You can only check habits for today or past days.")
                }
            } else {
                triggerCelebration(com.example.ui.components.ParticleStyle.GOLDEN_SPARKS)
            }
        }
    }

    fun incrementHabitCounter(habit: Habit) {
        viewModelScope.launch {
            val success = repository.incrementHabitCounter(habit.id, _uiState.value.selectedDate, habit.targetCount)
            if (!success) {
                _uiState.update {
                    it.copy(futureDateAlert = "Future days are locked! You can only check habits for today or past days.")
                }
            } else {
                if (habit.name.contains("water", ignoreCase = true) || habit.category.contains("water", ignoreCase = true)) {
                    triggerCelebration(com.example.ui.components.ParticleStyle.WATER_BUBBLES)
                } else {
                    triggerCelebration(com.example.ui.components.ParticleStyle.GOLDEN_SPARKS)
                }
            }
        }
    }

    fun onLockedDateClicked() {
        _uiState.update {
            it.copy(futureDateAlert = "This day is in the future. Habits are locked until that day arrives!")
        }
    }

    fun saveHabit(
        name: String,
        category: String,
        iconName: String,
        colorHex: String,
        targetCount: Int,
        unit: String,
        timeOfDay: String
    ) {
        viewModelScope.launch {
            val existing = _uiState.value.editingHabit
            if (existing != null) {
                repository.updateHabit(
                    existing.copy(
                        name = name,
                        category = category,
                        iconName = iconName,
                        colorHex = colorHex,
                        targetCount = targetCount,
                        unit = unit,
                        timeOfDay = timeOfDay
                    )
                )
            } else {
                repository.insertHabit(
                    Habit(
                        name = name,
                        category = category,
                        iconName = iconName,
                        colorHex = colorHex,
                        targetCount = targetCount,
                        unit = unit,
                        timeOfDay = timeOfDay
                    )
                )
            }
            closeAddHabit()
        }
    }

    fun quickAddHabit(habit: Habit) {
        viewModelScope.launch {
            val exists = allHabits.value.any { it.name.equals(habit.name, ignoreCase = true) }
            if (!exists) {
                repository.insertHabit(habit)
                triggerCelebration(com.example.ui.components.ParticleStyle.GOLDEN_SPARKS)
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            showHabitDetail(null)
        }
    }

    // Task actions
    fun openAddTask() {
        _uiState.update { it.copy(isAddTaskOpen = true, editingTask = null) }
    }

    fun openEditTask(task: TaskItem) {
        _uiState.update { it.copy(isAddTaskOpen = true, editingTask = task) }
    }

    fun closeAddTask() {
        _uiState.update { it.copy(isAddTaskOpen = false, editingTask = null) }
    }

    fun toggleTaskCompleted(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task.id, task.isCompleted)
            if (!task.isCompleted) {
                triggerCelebration(com.example.ui.components.ParticleStyle.FIREWORKS)
            }
        }
    }

    fun saveTask(
        title: String,
        notes: String?,
        category: String,
        priority: String,
        scope: String,
        dueDate: String?,
        endDate: String? = null,
        dailyTimeline: String? = null,
        estimatedHours: Float,
        clientOrCourse: String?,
        tags: String
    ) {
        viewModelScope.launch {
            val existing = _uiState.value.editingTask
            if (existing != null) {
                repository.updateTask(
                    existing.copy(
                        title = title,
                        notes = notes,
                        category = category,
                        priority = priority,
                        scope = scope,
                        dueDate = dueDate,
                        endDate = endDate,
                        dailyTimeline = dailyTimeline,
                        estimatedHours = estimatedHours,
                        clientOrCourse = clientOrCourse,
                        tags = tags
                    )
                )
            } else {
                repository.insertTask(
                    TaskItem(
                        title = title,
                        notes = notes,
                        category = category,
                        priority = priority,
                        scope = scope,
                        dueDate = dueDate,
                        endDate = endDate,
                        dailyTimeline = dailyTimeline,
                        estimatedHours = estimatedHours,
                        clientOrCourse = clientOrCourse,
                        tags = tags
                    )
                )
            }
            closeAddTask()
        }
    }

    fun addWorkspace(name: String) {
        viewModelScope.launch {
            repository.addWorkspace(name)
        }
    }

    fun updateProfile(name: String, headline: String, bio: String, occupations: String? = null) {
        viewModelScope.launch {
            repository.updateProfileDetails(name, headline, bio, occupations)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            closeAddTask()
        }
    }

    // Boss Mode actions
    fun startBossChallenge(
        title: String,
        selectedHabitIds: List<Long>,
        durationDays: Int,
        intensity: String,
        punishment: String
    ) {
        viewModelScope.launch {
            val startDate = HabitRepository.getTodayDateString()
            val endDate = HabitRepository.getDateOffset(durationDays)
            val challenge = BossChallenge(
                title = title,
                targetHabitIds = selectedHabitIds.joinToString(","),
                durationDays = durationDays,
                intensity = intensity,
                punishment = punishment,
                startDate = startDate,
                endDate = endDate,
                currentDayNumber = 1,
                completedDays = 0,
                missedDays = 0,
                isActive = true
            )
            repository.startBossChallenge(challenge)
        }
    }

    fun quitBossChallenge(id: Long, antiQuitPhrase: String) {
        viewModelScope.launch {
            repository.quitBossChallenge(id, antiQuitPhrase)
        }
    }

    // Profile updates
    fun updateProfilePhotos(bannerUri: String?, avatarUri: String?) {
        viewModelScope.launch {
            repository.updateProfilePhotos(bannerUri, avatarUri)
        }
    }

    fun updateProfileDetails(name: String, headline: String, bio: String) {
        viewModelScope.launch {
            repository.updateProfileDetails(name, headline, bio)
        }
    }

    fun retakeOnboarding() {
        viewModelScope.launch {
            repository.resetOnboarding()
        }
    }

    fun setTaskViewMode(mode: String) {
        _uiState.update { it.copy(taskViewMode = mode) }
    }

    fun setTodayViewMode(mode: String) {
        _uiState.update { it.copy(todayViewMode = mode) }
    }

    fun updateTaskKanbanStatus(taskId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateTaskKanbanStatus(taskId, newStatus)
            if (newStatus == "SHIPPED") {
                triggerCelebration(com.example.ui.components.ParticleStyle.FIREWORKS)
            }
        }
    }

    fun activateStreakFreeze() {
        viewModelScope.launch {
            repository.activateStreakFreeze()
        }
    }

    fun toggleComebackProtocol(active: Boolean) {
        viewModelScope.launch {
            repository.setComebackProtocolActive(active)
        }
    }

    fun triggerCelebration(style: com.example.ui.components.ParticleStyle) {
        _uiState.update { it.copy(particleTrigger = System.currentTimeMillis(), particleStyle = style) }
    }

    fun sendTestBurningNotification(context: android.content.Context, taskTitle: String = "Urgent: Complete Project Deliverable") {
        com.example.notifications.ForgeNotificationHelper.sendBurningTaskAlert(
            context = context,
            taskTitle = taskTitle,
            priority = "Urgent",
            timeRemainingText = "Burning down in 2h"
        )
    }

    fun sendTestHabitGuardNotification(context: android.content.Context, uncompletedCount: Int = 3) {
        com.example.notifications.ForgeNotificationHelper.sendHabitGuardAlert(
            context = context,
            uncompletedCount = uncompletedCount,
            streakDays = 14
        )
    }

    private fun computeStreakForHabit(habitId: Long, entries: List<HabitEntry>): Int {
        val habitEntries = entries.filter { it.habitId == habitId && it.isCompleted }.associateBy { it.date }
        if (habitEntries.isEmpty()) return 0

        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var streak = 0

        val todayStr = sdf.format(cal.time)
        val doneToday = habitEntries.containsKey(todayStr)

        if (doneToday) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)
            if (!habitEntries.containsKey(yesterdayStr)) {
                return 0
            }
        }

        while (true) {
            val checkDate = sdf.format(cal.time)
            if (habitEntries.containsKey(checkDate)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
