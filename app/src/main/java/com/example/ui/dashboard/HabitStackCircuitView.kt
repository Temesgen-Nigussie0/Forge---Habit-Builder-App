package com.example.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
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
fun HabitStackCircuitView(
    habitsWithEntries: List<HabitWithEntry>,
    onToggleHabit: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var selectedRoutine by remember { mutableIntStateOf(0) } // 0 = Morning, 1 = Evening

    val morningHabits = habitsWithEntries.take(4)
    val eveningHabits = if (habitsWithEntries.size > 4) habitsWithEntries.drop(4).take(4) else habitsWithEntries.takeLast(2)

    val currentStack = if (selectedRoutine == 0) morningHabits else eveningHabits
    val completedInStack = currentStack.count { it.isCompleted }

    Column(modifier = modifier.fillMaxWidth()) {
        // Routine Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedRoutine == 0) OrangeSubtle else Color.Transparent)
                    .border(
                        1.dp,
                        if (selectedRoutine == 0) ElectricOrange else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedRoutine = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = if (selectedRoutine == 0) ElectricOrange else TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Morning Ignition Stack",
                        color = if (selectedRoutine == 0) ElectricOrange else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedRoutine == 1) OrangeSubtle else Color.Transparent)
                    .border(
                        1.dp,
                        if (selectedRoutine == 1) ElectricOrange else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedRoutine = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = if (selectedRoutine == 1) SunsetAmber else TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Evening Shutdown Stack",
                        color = if (selectedRoutine == 1) SunsetAmber else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Info Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = ElectricOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CIRCUIT MOMENTUM",
                        color = ElectricOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "$completedInStack / ${currentStack.size} NODES POWERED",
                    color = if (completedInStack == currentStack.size) SuccessEmerald else SunsetAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Sequential Connected Circuit Wire
        val infiniteTransition = rememberInfiniteTransition(label = "wire_pulse")
        val pulseOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulse_anim"
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            currentStack.forEachIndexed { index, item ->
                val isCompleted = item.isCompleted
                val isNextInChain = !isCompleted && (index == 0 || currentStack[index - 1].isCompleted)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column: Node circle + vertical circuit wire
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(40.dp)
                    ) {
                        // Glowing Node Circle
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) SuccessEmerald
                                    else if (isNextInChain) ElectricOrange
                                    else DarkSurfaceCard
                                )
                                .border(
                                    2.dp,
                                    if (isCompleted) SuccessEmerald
                                    else if (isNextInChain) NeonOrange
                                    else DarkBorder,
                                    CircleShape
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onToggleHabit(item.habit)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isNextInChain) Color.White else TextTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Vertical Circuit Wire connecting to next node
                        if (index < currentStack.size - 1) {
                            Canvas(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(64.dp)
                            ) {
                                val wireColor = if (isCompleted) ElectricOrange else Color(0x30FFFFFF)
                                drawLine(
                                    color = wireColor,
                                    start = Offset(size.width / 2, 0f),
                                    end = Offset(size.width / 2, size.height),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )

                                // Animated energy spark traveling down
                                if (isNextInChain || isCompleted) {
                                    val sparkY = size.height * pulseOffset
                                    drawCircle(
                                        color = NeonOrange,
                                        radius = 3.5.dp.toPx(),
                                        center = Offset(size.width / 2, sparkY)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right Column: Habit Stack Card
                    val cardBg by animateColorAsState(
                        targetValue = if (isCompleted) OrangeSubtle else if (isNextInChain) DarkSurfaceElevated else DarkSurfaceCard,
                        label = "card_bg"
                    )
                    val cardBorder by animateColorAsState(
                        targetValue = if (isCompleted) SuccessEmerald.copy(alpha = 0.5f) else if (isNextInChain) ElectricOrange else DarkBorder,
                        label = "card_border"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(cardBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onToggleHabit(item.habit)
                            }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.habit.name,
                                        color = if (isCompleted) TextPrimary.copy(alpha = 0.8f) else TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isNextInChain) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ElectricOrange)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "NEXT STEP",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.habit.category} • Target ${item.habit.targetCount}x",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) SuccessEmerald else DarkSurfaceElevated)
                                    .border(1.dp, if (isCompleted) SuccessEmerald else DarkBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (index < currentStack.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
