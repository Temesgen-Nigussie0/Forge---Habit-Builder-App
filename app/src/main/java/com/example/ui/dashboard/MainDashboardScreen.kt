package com.example.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.UserProfile
import com.example.ui.boss.BossModeScreen
import com.example.ui.components.ParticleCelebration
import com.example.ui.components.SwipeActionCard
import com.example.ui.dashboard.HabitStackCircuitView
import com.example.ui.dashboard.StreakFreezeProtocolCard
import com.example.ui.tasks.AddEditTaskDialog
import com.example.ui.tasks.TasksView
import com.example.ui.theme.BossCrimson
import com.example.ui.theme.BossFlame
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MainDashboardScreen(
    viewModel: HabitDashboardViewModel,
    onRetakeOnboarding: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val habits by viewModel.habitsForSelectedDate.collectAsState()
    val weekDays by viewModel.weekDays.collectAsState()
    val allHabits by viewModel.allHabits.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val activeChallenge by viewModel.activeBossChallenge.collectAsState()
    val isDateLocked by viewModel.isDateLocked.collectAsState()

    val completedCount = remember(habits) { habits.count { it.isCompleted } }
    val totalCount = habits.size
    val progressRatio = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        containerColor = ObsidianBlack,
        bottomBar = {
            ForgeModernDockNavigationBar(
                selectedTab = uiState.currentTab,
                onSelectTab = { viewModel.selectTab(it) }
            )
        },
        floatingActionButton = {
            if (uiState.currentTab == 0) {
                FloatingActionButton(
                    onClick = { viewModel.openAddHabit() },
                    containerColor = ElectricOrange,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("fab_add_habit")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Habit",
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (uiState.currentTab == 1) {
                FloatingActionButton(
                    onClick = { viewModel.openAddTask() },
                    containerColor = ElectricOrange,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("fab_add_task")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Future Date Alert Banner
            AnimatedVisibility(
                visible = uiState.futureDateAlert != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                uiState.futureDateAlert?.let { alertText ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, ElectricOrange.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = ElectricOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = alertText,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.dismissFutureAlert() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main_tabs_transition"
            ) { tab ->
                when (tab) {
                    0 -> TodayHabitsView(
                        userProfileName = userProfile?.userName ?: "Alex",
                        habits = habits,
                        weekDays = weekDays,
                        isDateLocked = isDateLocked,
                        completedCount = completedCount,
                        totalCount = totalCount,
                        progressRatio = progressRatio,
                        selectedFilter = uiState.selectedFilter,
                        onSelectFilter = { viewModel.selectFilter(it) },
                        onSelectDate = { viewModel.selectDate(it) },
                        onToggleHabit = { viewModel.toggleCompletion(it) },
                        onIncrementHabit = { viewModel.incrementHabitCounter(it) },
                        onLockedClick = { viewModel.onLockedDateClicked() },
                        onHabitClick = { viewModel.showHabitDetail(it) },
                        userProfile = userProfile,
                        onActivateFreeze = { viewModel.activateStreakFreeze() },
                        onToggleComeback = { viewModel.toggleComebackProtocol(it) },
                        todayViewMode = uiState.todayViewMode,
                        onSelectTodayViewMode = { viewModel.setTodayViewMode(it) }
                    )
                    1 -> TasksView(
                        tasks = allTasks,
                        onToggleTask = { viewModel.toggleTaskCompleted(it) },
                        onEditTask = { viewModel.openEditTask(it) },
                        onAddNewTask = { viewModel.openAddTask() },
                        workspaces = userProfile?.workspaces?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
                            ?: listOf("Personal Workspace", "Client Work"),
                        onAddNewWorkspace = { ws -> viewModel.addWorkspace(ws) },
                        viewMode = uiState.taskViewMode,
                        onSelectViewMode = { viewModel.setTaskViewMode(it) },
                        onMoveKanbanStatus = { id, status -> viewModel.updateTaskKanbanStatus(id, status) }
                    )
                    2 -> BossModeScreen(
                        activeChallenge = activeChallenge,
                        allHabits = allHabits,
                        habitsWithEntries = habits,
                        onStartChallenge = { title, habitIds, duration, intensity, punishment ->
                            viewModel.startBossChallenge(title, habitIds, duration, intensity, punishment)
                        },
                        onToggleHabit = { viewModel.toggleCompletion(it) },
                        onQuitChallenge = { id, phrase ->
                            viewModel.quitBossChallenge(id, phrase)
                        }
                    )
                    3 -> AnalyticsView(
                        habits = allHabits,
                        entries = allEntries,
                        tasks = allTasks,
                        activeBossChallenge = activeChallenge,
                        userProfile = userProfile,
                        onActivateFreeze = { viewModel.activateStreakFreeze() },
                        onToggleComeback = { viewModel.toggleComebackProtocol(it) }
                    )
                    4 -> ProfileSettingsView(
                        profile = userProfile,
                        habitsCount = allHabits.size,
                        tasksCount = allTasks.size,
                        completedTasksCount = allTasks.count { it.isCompleted },
                        onRetakeOnboarding = onRetakeOnboarding,
                        onUpdatePhotos = { banner, avatar ->
                            viewModel.updateProfilePhotos(banner, avatar)
                        },
                        onUpdateDetails = { name, headline, bio, occ ->
                            viewModel.updateProfile(name, headline, bio, occ)
                        },
                        onActivateFreeze = { viewModel.activateStreakFreeze() },
                        onToggleComeback = { viewModel.toggleComebackProtocol(it) },
                        allHabits = allHabits,
                        onQuickAddHabit = { habit -> viewModel.quickAddHabit(habit) }
                    )
                }
            }
        }
    }

    // Particle Celebration Micro-Interactions Overlay (Idea 9)
    if (uiState.particleTrigger > 0L) {
        ParticleCelebration(
            trigger = true,
            style = uiState.particleStyle,
            modifier = Modifier.fillMaxSize()
        )
    }
}

    // Add / Edit Habit Dialog
    if (uiState.isAddHabitOpen) {
        AddEditHabitDialog(
            habitToEdit = uiState.editingHabit,
            onDismiss = { viewModel.closeAddHabit() },
            onSave = { name, cat, icon, color, target, unit, time ->
                viewModel.saveHabit(name, cat, icon, color, target, unit, time)
            }
        )
    }

    // Habit Detail Dialog
    uiState.habitDetail?.let { habit ->
        HabitDetailDialog(
            habit = habit,
            entries = allEntries,
            onDismiss = { viewModel.showHabitDetail(null) },
            onEdit = {
                viewModel.showHabitDetail(null)
                viewModel.openEditHabit(it)
            },
            onDelete = { habitId ->
                viewModel.deleteHabit(habitId)
            }
        )
    }

    // Add / Edit Task Dialog (with cool black calendar, daily timeline, and real workspaces)
    if (uiState.isAddTaskOpen) {
        val userWorkspaces = userProfile?.workspaces
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: listOf("Personal Workspace", "Client Work")

        AddEditTaskDialog(
            taskToEdit = uiState.editingTask,
            availableWorkspaces = userWorkspaces,
            onAddNewWorkspace = { wsName -> viewModel.addWorkspace(wsName) },
            onDismiss = { viewModel.closeAddTask() },
            onSave = { title, notes, cat, priority, scope, due, end, timeline, est, client, tags ->
                viewModel.saveTask(title, notes, cat, priority, scope, due, end, timeline, est, client, tags)
            },
            onDelete = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )
    }
}

@Composable
private fun TodayHabitsView(
    userProfileName: String,
    habits: List<HabitWithEntry>,
    weekDays: List<DayItem>,
    isDateLocked: Boolean,
    completedCount: Int,
    totalCount: Int,
    progressRatio: Float,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    onSelectDate: (String) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onIncrementHabit: (Habit) -> Unit,
    onLockedClick: () -> Unit,
    onHabitClick: (Habit) -> Unit,
    userProfile: UserProfile? = null,
    onActivateFreeze: () -> Unit = {},
    onToggleComeback: (Boolean) -> Unit = {},
    todayViewMode: String = "HABITS",
    onSelectTodayViewMode: (String) -> Unit = {}
) {
    val filters = listOf("All", "Morning", "Afternoon", "Evening")
    var isViewModeMenuOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Vector style button beside left to "Hello, [name]"
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.2.dp, ElectricOrange.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                    .clickable { isViewModeMenuOpen = true }
                                    .testTag("today_view_mode_vector_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (todayViewMode == "CIRCUIT") Icons.Default.ElectricBolt else Icons.Default.GridView,
                                    contentDescription = "Switch View Mode",
                                    tint = ElectricOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = isViewModeMenuOpen,
                                onDismissRequest = { isViewModeMenuOpen = false },
                                modifier = Modifier
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, ElectricOrange.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.GridView,
                                                contentDescription = null,
                                                tint = if (todayViewMode == "HABITS") ElectricOrange else TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Habit Grid",
                                                    color = if (todayViewMode == "HABITS") ElectricOrange else TextPrimary,
                                                    fontWeight = if (todayViewMode == "HABITS") FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "Standard habit cards & checklists",
                                                    color = TextTertiary,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onSelectTodayViewMode("HABITS")
                                        isViewModeMenuOpen = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.ElectricBolt,
                                                contentDescription = null,
                                                tint = if (todayViewMode == "CIRCUIT") ElectricOrange else TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Routine Circuit",
                                                    color = if (todayViewMode == "CIRCUIT") ElectricOrange else TextPrimary,
                                                    fontWeight = if (todayViewMode == "CIRCUIT") FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "Sequential circadian loop stack",
                                                    color = TextTertiary,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onSelectTodayViewMode("CIRCUIT")
                                        isViewModeMenuOpen = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "TODAY'S RHYTHM",
                                color = ElectricOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hello, $userProfileName",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Streak Pill Badge
                    val maxStreak = remember(habits) { habits.maxOfOrNull { it.streak } ?: 0 }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(OrangeSubtle)
                            .border(1.dp, ElectricOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = SunsetAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (maxStreak > 0) "$maxStreak Days" else "Daily Streak",
                                color = SunsetAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Calendar Strip (Compact, not stretched!)
        item {
            CalendarStrip(
                days = weekDays,
                onSelectDate = onSelectDate
            )
        }

        // Daily Progress Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkBorderHighlight, RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when {
                                    isDateLocked -> "Future Date (Preview Only)"
                                    totalCount == 0 -> "No habits configured"
                                    completedCount == totalCount -> "All habits completed! 🎉"
                                    else -> "$completedCount of $totalCount completed"
                                },
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when {
                                    isDateLocked -> "Habits are locked until this day arrives"
                                    progressRatio >= 1f -> "Peak momentum maintained today"
                                    else -> "${(progressRatio * 100).toInt()}% of daily target achieved"
                                },
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDateLocked -> DarkSurfaceCard
                                        progressRatio >= 1f -> SuccessEmerald.copy(alpha = 0.15f)
                                        else -> OrangeSubtle
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isDateLocked -> Icons.Default.Lock
                                    progressRatio >= 1f -> Icons.Default.CheckCircle
                                    else -> Icons.Default.LocalFireDepartment
                                },
                                contentDescription = null,
                                tint = when {
                                    isDateLocked -> TextTertiary
                                    progressRatio >= 1f -> SuccessEmerald
                                    else -> ElectricOrange
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { if (isDateLocked) 0f else progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progressRatio >= 1f) SuccessEmerald else ElectricOrange,
                        trackColor = DarkSurface,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        if (todayViewMode == "CIRCUIT") {
            // Habit Stacking Sequential Circuit Wire View (Idea 6)
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    HabitStackCircuitView(
                        habitsWithEntries = habits,
                        onToggleHabit = onToggleHabit
                    )
                }
            }
        } else {
            // Time Filters Row
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricOrange else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricOrange else DarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectFilter(filter) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("filter_$filter")
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Habit Cards List (Active habits first, completed habits sent to bottom with divider!)
            val activeHabits = habits.filter { !it.isCompleted }
            val completedHabits = habits.filter { it.isCompleted }

            if (habits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 32.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🌱", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No habits for this filter",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap the + button to create a new habit.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                // 1. Active (uncompleted) habits first
                items(activeHabits, key = { it.habit.id }) { item ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        SwipeActionCard(
                            onSwipeRight = { onToggleHabit(item.habit) },
                            onSwipeLeft = { onHabitClick(item.habit) }
                        ) {
                            HabitCard(
                                item = item,
                                isDateLocked = isDateLocked,
                                onToggle = { onToggleHabit(item.habit) },
                                onIncrement = { onIncrementHabit(item.habit) },
                                onLockedClick = onLockedClick,
                                onClick = { onHabitClick(item.habit) }
                            )
                        }
                    }
                }

                // 2. Completed section divider
                if (completedHabits.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(DarkBorder)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, ElectricOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ElectricOrange,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "COMPLETED (${completedHabits.size})",
                                    color = ElectricOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(DarkBorder)
                            )
                        }
                    }

                    // 3. Completed habits at the bottom!
                    items(completedHabits, key = { it.habit.id }) { item ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            SwipeActionCard(
                                onSwipeRight = { onToggleHabit(item.habit) },
                                onSwipeLeft = { onHabitClick(item.habit) }
                            ) {
                                HabitCard(
                                    item = item,
                                    isDateLocked = isDateLocked,
                                    onToggle = { onToggleHabit(item.habit) },
                                    onIncrement = { onIncrementHabit(item.habit) },
                                    onLockedClick = onLockedClick,
                                    onClick = { onHabitClick(item.habit) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

/**
 * Modern Dock Navigation Bar matching Image 1
 * Features 5 items: Today, Tasks, Center Elevated BOSS MODE Flame, Analytics, Profile.
 * Cool dark palette (no brown tones!).
 */
@Composable
private fun ForgeModernDockNavigationBar(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Curved Floating Dock matching Image 1
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(Color(0xFF0F0F14)) // Cool titanium/graphite dark (No brown!)
                .border(1.dp, Color(0xFF22222E), RoundedCornerShape(34.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Today
                DockNavItem(
                    title = "Today",
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == 0,
                    onClick = { onSelectTab(0) }
                )

                // 2. Tasks
                DockNavItem(
                    title = "Tasks",
                    icon = Icons.Default.Assignment,
                    isSelected = selectedTab == 1,
                    onClick = { onSelectTab(1) }
                )

                // 3. Center Elevated BOSS MODE Flame Button!
                Box(
                    modifier = Modifier
                        .offset(y = (-4).dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(BossCrimson, BossFlame)
                            )
                        )
                        .border(
                            2.dp,
                            if (selectedTab == 2) Color.White else Color(0x60FFA0A0),
                            CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectTab(2) }
                        )
                        .testTag("nav_tab_boss_mode"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Boss Mode",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // 4. Analytics
                DockNavItem(
                    title = "Analytics",
                    icon = Icons.Default.BarChart,
                    isSelected = selectedTab == 3,
                    onClick = { onSelectTab(3) }
                )

                // 5. Profile
                DockNavItem(
                    title = "Profile",
                    icon = Icons.Default.Person,
                    isSelected = selectedTab == 4,
                    onClick = { onSelectTab(4) }
                )
            }
        }
    }
}

@Composable
private fun DockNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("dock_tab_$title")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) ElectricOrange else TextTertiary,
            modifier = Modifier.size(22.dp)
        )

        // Label underneath selected section like Image 1!
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = ElectricOrange,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
