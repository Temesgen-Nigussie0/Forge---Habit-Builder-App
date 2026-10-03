package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.HabitRepository
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoolBlackCalendarDialog(
    initialDate: String? = null,
    title: String = "Select Date",
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val displaySdf = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.US) }

    val initialCalendar = remember(initialDate) {
        val cal = Calendar.getInstance()
        if (!initialDate.isNullOrBlank()) {
            try {
                cal.time = sdf.parse(initialDate) ?: Calendar.getInstance().time
            } catch (_: Exception) { }
        }
        cal
    }

    var selectedYear by remember { mutableIntStateOf(initialCalendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(initialCalendar.get(Calendar.MONTH)) } // 0..11
    var selectedDay by remember { mutableIntStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }

    val todayCal = remember { Calendar.getInstance() }
    val isTodayMonth = todayCal.get(Calendar.YEAR) == selectedYear && todayCal.get(Calendar.MONTH) == selectedMonth
    val todayDayNumber = todayCal.get(Calendar.DAY_OF_MONTH)

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Calculate calendar grid days
    val calInstance = Calendar.getInstance().apply {
        set(Calendar.YEAR, selectedYear)
        set(Calendar.MONTH, selectedMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    // Sunday = 1, Monday = 2 ... convert to Monday-first (0..6)
    val firstDayOfWeek = (calInstance.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val maxDays = calInstance.getActualMaximum(Calendar.DAY_OF_MONTH)

    val currentSelectedFormatted = remember(selectedYear, selectedMonth, selectedDay) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay.coerceIn(1, maxDays))
        }
        displaySdf.format(cal.time)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.5.dp, DarkBorderHighlight, RoundedCornerShape(26.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title.uppercase(),
                            color = ElectricOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentSelectedFormatted,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month / Year Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (selectedMonth == 0) {
                                selectedMonth = 11
                                selectedYear -= 1
                            } else {
                                selectedMonth -= 1
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${monthNames[selectedMonth]} $selectedYear",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            if (selectedMonth == 11) {
                                selectedMonth = 0
                                selectedYear += 1
                            } else {
                                selectedMonth += 1
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day of Week Headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            color = TextTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Month Grid (7 columns)
                val totalCells = ((firstDayOfWeek + maxDays + 6) / 7) * 7
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until totalCells / 7) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - firstDayOfWeek + 1

                                if (dayNum in 1..maxDays) {
                                    val isSelected = dayNum == selectedDay
                                    val isToday = isTodayMonth && dayNum == todayDayNumber

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) ElectricOrange
                                                else if (isToday) OrangeSubtle
                                                else Color.Transparent
                                            )
                                            .border(
                                                width = if (isSelected) 0.dp else if (isToday) 1.dp else 0.dp,
                                                color = if (isToday) ElectricOrange else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                                selectedDay = dayNum
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNum",
                                            color = if (isSelected) Color.White else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Shortcut Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Today" to 0,
                        "Tomorrow" to 1,
                        "+3 Days" to 3,
                        "+1 Week" to 7,
                        "+2 Weeks" to 14
                    ).forEach { (label, offsetDays) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .clickable {
                                    val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offsetDays) }
                                    selectedYear = c.get(Calendar.YEAR)
                                    selectedMonth = c.get(Calendar.MONTH)
                                    selectedDay = c.get(Calendar.DAY_OF_MONTH)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = ElectricOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = TextSecondary
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val resultCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedYear)
                                set(Calendar.MONTH, selectedMonth)
                                set(Calendar.DAY_OF_MONTH, selectedDay.coerceIn(1, maxDays))
                            }
                            onDateSelected(sdf.format(resultCal.time))
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("confirm_cool_calendar_date"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Set Date", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
