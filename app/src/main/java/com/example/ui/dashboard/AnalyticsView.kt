package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HabitRepository
import com.example.data.model.BossChallenge
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.ui.analytics.MilestoneBadgesSection
import com.example.ui.analytics.PillarRadarMatrix
import com.example.ui.analytics.SmartNotificationSchedulerCard
import com.example.ui.components.StreakHeatmap
import com.example.ui.dashboard.StreakFreezeProtocolCard
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun AnalyticsView(
    habits: List<Habit>,
    entries: List<HabitEntry>,
    tasks: List<TaskItem>,
    activeBossChallenge: BossChallenge? = null,
    userProfile: UserProfile? = null,
    onActivateFreeze: () -> Unit = {},
    onToggleComeback: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val completedEntries = remember(entries) { entries.filter { it.isCompleted } }
    val completedTasks = remember(tasks) { tasks.filter { it.isCompleted } }
    val totalDone = completedEntries.size + completedTasks.size

    val dateCountMap = remember(entries, tasks) {
        val map = mutableMapOf<String, Int>()
        entries.filter { it.isCompleted }.forEach { entry ->
            map[entry.date] = (map[entry.date] ?: 0) + 1
        }
        tasks.filter { it.isCompleted }.forEach { task ->
            val dateStr = task.dueDate ?: HabitRepository.getTodayDateString()
            map[dateStr] = (map[dateStr] ?: 0) + 1
        }
        map
    }

    val habitConsistencyRate = remember(habits, entries) {
        if (habits.isEmpty()) 0
        else {
            val last7Days = (0..6).map { HabitRepository.getDateOffset(-it) }
            val expected = habits.size * 7
            val actual = entries.count { it.date in last7Days && it.isCompleted }
            ((actual.toFloat() / expected.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 100)
        }
    }

    val taskCompletionRate = remember(tasks) {
        if (tasks.isEmpty()) 0
        else ((completedTasks.size.toFloat() / tasks.size) * 100).toInt()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "PERFORMANCE ANALYTICS",
            color = ElectricOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Productivity Matrix",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Perfectly equal and well-proportioned KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EqualMetricCard(
                title = "HABIT RATE",
                value = "$habitConsistencyRate%",
                subtitle = "Past 7 days",
                icon = Icons.Default.TrendingUp,
                tint = ElectricOrange,
                modifier = Modifier.weight(1f)
            )

            EqualMetricCard(
                title = "TASKS DONE",
                value = "$taskCompletionRate%",
                subtitle = "${completedTasks.size} / ${tasks.size} done",
                icon = Icons.Default.AssignmentTurnedIn,
                tint = SuccessEmerald,
                modifier = Modifier.weight(1f)
            )

            EqualMetricCard(
                title = "ACTIONS",
                value = "$totalDone",
                subtitle = "Total logged",
                icon = Icons.Default.LocalFireDepartment,
                tint = SunsetAmber,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reusable STREAK HEATMAP matching uploaded screenshot
        StreakHeatmap(
            dateCountMap = dateCountMap,
            totalWeeks = 18,
            title = "$totalDone contributions in the last year"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PILLAR RADAR & BALANCE MATRIX (Idea 3)
        PillarRadarMatrix(
            habits = habits,
            entries = entries,
            tasks = tasks
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Domain Breakdown
        Text(
            text = "PRODUCTION DOMAINS",
            color = ElectricOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val taskCategories = remember(tasks) {
                    tasks.groupBy { it.category }
                }

                if (taskCategories.isEmpty() && habits.isEmpty()) {
                    Text(
                        text = "No production domains yet. Create tasks and habits to see real-time domain velocity.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                } else {
                    // Real task categories
                    taskCategories.entries.take(4).forEachIndexed { idx, (catName, catTasks) ->
                        val completedInCat = catTasks.count { it.isCompleted }
                        val totalInCat = catTasks.size
                        val ratio = if (totalInCat > 0) completedInCat.toFloat() / totalInCat else 0f
                        val catColor = when (idx % 4) {
                            0 -> ElectricOrange
                            1 -> SunsetAmber
                            2 -> CyanBlue
                            else -> CoralRed
                        }
                        val icon = when {
                            catName.contains("code", true) || catName.contains("software", true) || catName.contains("dev", true) -> Icons.Default.Code
                            catName.contains("video", true) || catName.contains("photo", true) || catName.contains("design", true) -> Icons.Default.Movie
                            catName.contains("academic", true) || catName.contains("university", true) || catName.contains("study", true) -> Icons.Default.School
                            else -> Icons.Default.Work
                        }

                        DomainProgressRow(
                            name = catName,
                            icon = icon,
                            countLabel = "$completedInCat / $totalInCat done (${(ratio * 100).toInt()}%)",
                            progress = ratio,
                            color = catColor
                        )
                    }

                    // Daily Lifestyle Habits real progress
                    if (habits.isNotEmpty()) {
                        val completedTodayHabits = habits.count { h ->
                            entries.any { it.habitId == h.id && it.date == HabitRepository.getTodayDateString() && it.isCompleted }
                        }
                        val habitRatio = (completedTodayHabits.toFloat() / habits.size).coerceIn(0f, 1f)
                        DomainProgressRow(
                            name = "Daily Lifestyle Habits",
                            icon = Icons.Default.LocalFireDepartment,
                            countLabel = "$completedTodayHabits / ${habits.size} completed today",
                            progress = habitRatio,
                            color = SuccessEmerald
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // STREAK FREEZE & NEVER MISS TWICE PROTOCOL (Idea 5)
        StreakFreezeProtocolCard(
            userProfile = userProfile,
            onActivateFreeze = onActivateFreeze,
            onToggleComeback = onToggleComeback
        )

        Spacer(modifier = Modifier.height(24.dp))

        // DYNAMIC MILESTONE BADGES (glowing badges with unlock animations)
        MilestoneBadgesSection(
            habitEntries = entries,
            activeBossChallenge = activeBossChallenge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SMART PUSH NOTIFICATION SCHEDULER
        SmartNotificationSchedulerCard(
            habits = habits,
            habitEntries = entries
        )

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun EqualMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(96.dp) // Fixed equal height for all 3 cards
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DomainProgressRow(
    name: String,
    icon: ImageVector,
    countLabel: String,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Text(text = countLabel, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DarkSurfaceCard,
            strokeCap = StrokeCap.Round
        )
    }
}
