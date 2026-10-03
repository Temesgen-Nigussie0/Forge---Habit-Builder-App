package com.example.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun EisenhowerMatrixView(
    tasks: List<TaskItem>,
    onToggleTask: (TaskItem) -> Unit,
    onEditTask: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // 4 Quadrants:
    // Q1: Urgent + High Priority (Urgent & Important)
    val q1Tasks = tasks.filter { (it.priority == "Urgent" || it.priority == "High") && !it.isCompleted }
    // Q2: Medium Priority or Long-Term (Important, Not Urgent - Deep Work)
    val q2Tasks = tasks.filter { it.priority == "Medium" && !it.isCompleted }
    // Q3: Low Priority with tight due date (Urgent, Not High Leverage)
    val q3Tasks = tasks.filter { it.priority == "Low" && !it.isCompleted }
    // Q4: Completed / Backlog
    val q4Tasks = tasks.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quadrant 1: DO NOW
            QuadrantCard(
                title = "DO FIRST (Q1)",
                subtitle = "Urgent & High Impact",
                accentColor = CoralRed,
                tasks = q1Tasks,
                onToggle = onToggleTask,
                onClick = onEditTask,
                modifier = Modifier.weight(1f)
            )

            // Quadrant 2: SCHEDULE
            QuadrantCard(
                title = "DEEP WORK (Q2)",
                subtitle = "Important / Long-Term",
                accentColor = ElectricOrange,
                tasks = q2Tasks,
                onToggle = onToggleTask,
                onClick = onEditTask,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quadrant 3: QUICK WINS / DELEGATE
            QuadrantCard(
                title = "QUICK WINS (Q3)",
                subtitle = "Fast Execution",
                accentColor = SunsetAmber,
                tasks = q3Tasks,
                onToggle = onToggleTask,
                onClick = onEditTask,
                modifier = Modifier.weight(1f)
            )

            // Quadrant 4: SHIPPED & ARCHIVE
            QuadrantCard(
                title = "SHIPPED (Q4)",
                subtitle = "${q4Tasks.size} Tasks Cleared",
                accentColor = SuccessEmerald,
                tasks = q4Tasks,
                onToggle = onToggleTask,
                onClick = onEditTask,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun QuadrantCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    tasks: List<TaskItem>,
    onToggle: (TaskItem) -> Unit,
    onClick: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(230.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${tasks.size}",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Empty",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tasks.take(5).forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceCard)
                                .clickable { onClick(task) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isCompleted) accentColor else Color.Transparent)
                                    .border(1.2.dp, accentColor, CircleShape)
                                    .clickable { onToggle(task) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = task.title,
                                color = if (task.isCompleted) TextTertiary else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanSprintBoardView(
    tasks: List<TaskItem>,
    onToggleTask: (TaskItem) -> Unit,
    onMoveStatus: (taskId: Long, newStatus: String) -> Unit,
    onEditTask: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = listOf(
        Triple("TODO", "Backlog", CyanBlue),
        Triple("IN_PROGRESS", "In Progress", ElectricOrange),
        Triple("REVIEW", "Client Review", SunsetAmber),
        Triple("SHIPPED", "Shipped ✓", SuccessEmerald)
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        columns.forEachIndexed { colIdx, (statusKey, title, color) ->
            val colTasks = tasks.filter {
                if (statusKey == "SHIPPED") it.isCompleted || it.kanbanStatus == "SHIPPED"
                else !it.isCompleted && (it.kanbanStatus == statusKey || (statusKey == "TODO" && it.kanbanStatus.isBlank()))
            }

            Box(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkBorder, RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceCard)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${colTasks.size}",
                                color = color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (colTasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No tasks in $title", color = TextTertiary, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(colTasks, key = { it.id }) { task ->
                                KanbanCardItem(
                                    task = task,
                                    color = color,
                                    canAdvance = colIdx < columns.size - 1,
                                    nextStatus = if (colIdx < columns.size - 1) columns[colIdx + 1].first else null,
                                    onAdvance = { next -> onMoveStatus(task.id, next) },
                                    onClick = { onEditTask(task) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanCardItem(
    task: TaskItem,
    color: Color,
    canAdvance: Boolean,
    nextStatus: String?,
    onAdvance: (String) -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, DarkBorderHighlight, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (canAdvance && nextStatus != null) {
                    IconButton(
                        onClick = { onAdvance(nextStatus) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Advance task",
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (!task.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.notes,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.category,
                    color = TextTertiary,
                    fontSize = 10.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.priority.uppercase(),
                        color = color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
