package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeActionCard(
    onSwipeRight: () -> Unit, // Complete action
    onSwipeLeft: () -> Unit,  // Reschedule / Snooze action
    modifier: Modifier = Modifier,
    swipeThreshold: Float = 140f,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var hasHapticked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
    ) {
        // Background revealed drawer
        val currentOffset = offsetX.value
        val isRightSwipe = currentOffset > 0

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    if (isRightSwipe) SuccessEmerald.copy(alpha = 0.85f)
                    else SunsetAmber.copy(alpha = 0.85f)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = if (isRightSwipe) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            if (isRightSwipe) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark Done",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Reschedule",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Reschedule",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Foreground content with draggable physics
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            val newX = (offsetX.value + delta).coerceIn(-300f, 300f)
                            offsetX.snapTo(newX)

                            if (Math.abs(newX) >= swipeThreshold && !hasHapticked) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                hasHapticked = true
                            } else if (Math.abs(newX) < swipeThreshold) {
                                hasHapticked = false
                            }
                        }
                    },
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        val current = offsetX.value
                        coroutineScope.launch {
                            if (current >= swipeThreshold) {
                                onSwipeRight()
                            } else if (current <= -swipeThreshold) {
                                onSwipeLeft()
                            }
                            offsetX.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 400f))
                            hasHapticked = false
                        }
                    }
                )
        ) {
            content()
        }
    }
}
