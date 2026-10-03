package com.example.ui.dashboard

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CalendarStrip(
    days: List<DayItem>,
    onSelectDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            DayItemView(
                day = day,
                onClick = { onSelectDate(day.dateString) }
            )
        }
    }
}

@Composable
private fun DayItemView(
    day: DayItem,
    onClick: () -> Unit
) {
    val isSelected = day.isSelected
    val isToday = day.isToday

    val borderColor by animateColorAsState(
        targetValue = when {
            isToday -> ElectricOrange
            isSelected -> ElectricOrange
            else -> DarkBorder
        },
        label = "day_border"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isToday -> ElectricOrange
            isSelected -> OrangeSubtle
            else -> DarkSurfaceElevated
        },
        label = "day_bg"
    )

    // Compact, proportional pill matching iOS design (not stretched!)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(44.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isToday || isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Day of week letter (M, T, W...)
        Text(
            text = day.dayOfWeek,
            color = when {
                isToday -> Color.White
                isSelected -> ElectricOrange
                else -> TextSecondary
            },
            fontSize = 11.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Number with clean lock or clean text (No overlapping lines!)
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (day.isFuture) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Future date locked",
                    tint = if (isToday) Color.White.copy(alpha = 0.8f) else TextTertiary,
                    modifier = Modifier.size(11.dp)
                )
            } else {
                Text(
                    text = "${day.dayOfMonth}",
                    color = when {
                        isToday -> Color.White
                        isSelected -> ElectricOrange
                        else -> TextPrimary
                    },
                    fontSize = 14.sp,
                    fontWeight = if (isToday || isSelected) FontWeight.ExtraBold else FontWeight.Medium
                )
            }
        }

        // Clean subtle indicator dot for completion status (cleanly separated, NO overlapping lines!)
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(
                    when {
                        day.completionRatio >= 1f -> if (isToday) Color.White else SuccessEmerald
                        day.completionRatio > 0f -> if (isToday) Color.White.copy(alpha = 0.7f) else ElectricOrange
                        else -> Color.Transparent
                    }
                )
        )
    }
}
