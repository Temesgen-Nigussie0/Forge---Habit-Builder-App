package com.example.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: TaskItem,
    onToggleCompleted: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.isCompleted

    val priorityColor = when (task.priority) {
        "Urgent" -> CoralRed
        "High" -> ElectricOrange
        "Medium" -> SunsetAmber
        else -> CyanBlue
    }

    val cardBorderColor by animateColorAsState(
        targetValue = if (isCompleted) DarkBorder else priorityColor.copy(alpha = 0.45f),
        label = "task_border"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "task_check_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("task_item_${task.id}")
    ) {
        Column {
            // Header Row: Category Badge + Priority Pill + Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge with icon
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceCard)
                            .border(0.8.dp, DarkBorderHighlight, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = getCategoryIcon(task.category),
                                contentDescription = null,
                                tint = priorityColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = task.category,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Priority Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(priorityColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = task.priority.uppercase(),
                            color = priorityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Scope Tag (Short/Long term)
                    if (task.scope != "Short-Term") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = task.scope,
                                color = CyanBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Checkbox
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .background(if (isCompleted) SuccessEmerald else Color.Transparent)
                        .border(
                            1.5.dp,
                            if (isCompleted) SuccessEmerald else DarkBorderHighlight,
                            CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleCompleted
                        )
                        .testTag("task_check_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Title
            Text(
                text = task.title,
                color = if (isCompleted) TextSecondary else TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                lineHeight = 22.sp
            )

            // Optional Client / Course subtitle
            if (!task.clientOrCourse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (task.category.contains("University")) Icons.Default.School else Icons.Default.Business,
                        contentDescription = null,
                        tint = ElectricOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = task.clientOrCourse,
                        color = ElectricOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Optional notes
            if (!task.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = task.notes,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2
                )
            }

            // Tags row + Due date + Hours
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tags
                if (task.tags.isNotBlank()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        task.tags.split(",").take(3).forEach { tag ->
                            val clean = tag.trim()
                            if (clean.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkBorder)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "#$clean",
                                        color = TextTertiary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Metadata: Due date / Est time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (task.estimatedHours > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${task.estimatedHours}h",
                                color = TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (!task.dueDate.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = if (task.priority == "Urgent") CoralRed else TextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = task.dueDate,
                                color = if (task.priority == "Urgent") CoralRed else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when {
        category.contains("Software", ignoreCase = true) || category.contains("Code", ignoreCase = true) -> Icons.Default.Code
        category.contains("Video", ignoreCase = true) || category.contains("Photo", ignoreCase = true) -> Icons.Default.Movie
        category.contains("University", ignoreCase = true) || category.contains("Academics", ignoreCase = true) -> Icons.Default.School
        category.contains("Client", ignoreCase = true) -> Icons.Default.Business
        else -> Icons.Default.Person
    }
}
