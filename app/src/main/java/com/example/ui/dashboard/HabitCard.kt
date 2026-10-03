package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeGlow
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HabitCard(
    item: HabitWithEntry,
    isDateLocked: Boolean = false,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onIncrement: (() -> Unit)? = null,
    onLockedClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val habit = item.habit
    val isCompleted = item.isCompleted
    val isWaterHabit = habit.iconName.equals("water", ignoreCase = true) ||
            habit.name.contains("water", ignoreCase = true) ||
            habit.name.contains("drink", ignoreCase = true)

    var isWaterExpanded by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "card_scale"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "check_scale"
    )

    val checkBgColor by animateColorAsState(
        targetValue = if (isCompleted) Color.White else Color.Transparent,
        label = "check_bg"
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (isCompleted) ElectricOrange else DarkSurfaceElevated,
        label = "card_bg"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            isDateLocked -> DarkBorder
            isCompleted -> ElectricOrange
            else -> DarkBorder
        },
        label = "card_border"
    )

    val accentColor = remember(habit.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(habit.colorHex))
        } catch (e: Exception) {
            ElectricOrange
        }
    }

    com.example.ui.components.SwipeActionCard(
        onSwipeRight = {
            if (isDateLocked) {
                onLockedClick?.invoke()
            } else {
                onToggle()
            }
        },
        onSwipeLeft = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clip(RoundedCornerShape(20.dp))
                .background(cardBgColor)
                .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
                .padding(16.dp)
                .testTag("habit_card_${habit.id}")
        ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Pill
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isCompleted) Color.White.copy(alpha = 0.22f) else accentColor.copy(alpha = 0.16f))
                        .border(
                            1.dp,
                            if (isCompleted) Color.White.copy(alpha = 0.35f) else accentColor.copy(alpha = 0.35f),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getIconEmoji(habit.iconName),
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Habit Details
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habit.name,
                            color = if (isCompleted) Color.White else TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCompleted) Color.White.copy(alpha = 0.22f) else DarkBorder)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = habit.category,
                                color = if (isCompleted) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Time of day tag
                        if (habit.timeOfDay != "Anytime") {
                            Text(
                                text = "• ${habit.timeOfDay}",
                                color = if (isCompleted) Color.White.copy(alpha = 0.85f) else TextTertiary,
                                fontSize = 12.sp
                            )
                        }

                        // Streak count
                        if (item.streak > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = if (isCompleted) Color.White else SunsetAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${item.streak}d",
                                    color = if (isCompleted) Color.White else SunsetAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Water habit expandable trigger button
                        if (isWaterHabit) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isCompleted) Color.White.copy(alpha = 0.22f) else CyanBlue.copy(alpha = 0.15f))
                                    .clickable { isWaterExpanded = !isWaterExpanded }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isWaterExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = if (isCompleted) Color.White else CyanBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Hydro",
                                        color = if (isCompleted) Color.White else CyanBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Checkmark or Locked Button
                if (isDateLocked) {
                    // Future date is locked
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceCard)
                            .border(1.dp, DarkBorder, CircleShape)
                            .clickable(onClick = { onLockedClick?.invoke() })
                            .testTag("habit_lock_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked date",
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .scale(checkScale)
                            .clip(CircleShape)
                            .background(checkBgColor)
                            .border(
                                1.8.dp,
                                if (isCompleted) Color.White else DarkBorderHighlight,
                                CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onToggle
                            )
                            .testTag("habit_check_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = ElectricOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        } else if (habit.targetCount > 1) {
                            Text(
                                text = "${item.currentCount}/${habit.targetCount}",
                                color = ElectricOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Cool Animated Water Drawer Widget
            if (isWaterHabit) {
                AnimatedVisibility(visible = isWaterExpanded) {
                    val target = habit.targetCount.coerceAtLeast(1)
                    val current = item.currentCount
                    val ratio = (current.toFloat() / target).coerceIn(0f, 1f)
                    val animatedFill by animateFloatAsState(
                        targetValue = ratio,
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                        label = "water_fill"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceCard)
                            .border(1.dp, CyanBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = CyanBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hydration Wave",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "${current * 250}ml / ${target * 250}ml",
                                color = CyanBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Animated Water Level Bar
                        LinearProgressIndicator(
                            progress = { animatedFill },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyanBlue,
                            trackColor = DarkSurfaceElevated,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Droplet indicators row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (glassIdx in 1..target.coerceAtMost(8)) {
                                val isFilled = glassIdx <= current
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isFilled) CyanBlue else DarkSurfaceElevated)
                                        .border(1.dp, if (isFilled) CyanBlue else DarkBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Opacity,
                                        contentDescription = null,
                                        tint = if (isFilled) Color.White else TextTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick +1 Glass (250ml) Button
                        if (!isDateLocked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(CyanBlue, Color(0xFF00B4D8))
                                            )
                                        )
                                        .clickable { onIncrement?.invoke() ?: onToggle() }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "+1 Glass (250ml)",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

fun getIconEmoji(iconName: String): String {
    return when (iconName.lowercase()) {
        "water" -> "💧"
        "book" -> "📖"
        "meditation" -> "🧘"
        "workout", "fitness", "exercise" -> "🏃"
        "target", "focus" -> "🎯"
        "sleep", "night" -> "🌙"
        "meal", "nutrition" -> "🥗"
        "sunlight", "sun" -> "☀️"
        "brain" -> "🧠"
        "code", "laptop" -> "💻"
        "money", "finance" -> "💰"
        "heart" -> "❤️"
        else -> "⚡"
    }
}
