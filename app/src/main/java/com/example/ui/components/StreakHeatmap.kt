package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HeatmapDayData(
    val dateString: String, // yyyy-MM-dd
    val count: Int,
    val dayOfWeekIndex: Int, // 0..6 (Mon..Sun)
    val monthName: String, // "Jan", "Feb", etc.
    val isFirstWeekOfMonth: Boolean
)

@Composable
fun StreakHeatmap(
    dateCountMap: Map<String, Int>,
    totalWeeks: Int = 16,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedCell by remember { mutableStateOf<HeatmapDayData?>(null) }

    // Generate grid data: 7 rows (Mon..Sun) across totalWeeks columns
    val weeksData = remember(dateCountMap, totalWeeks) {
        val list = mutableListOf<List<HeatmapDayData>>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val monthFormat = SimpleDateFormat("MMM", Locale.US)

        val cal = Calendar.getInstance()
        // Align to current week's Sunday/Saturday
        cal.firstDayOfWeek = Calendar.MONDAY

        // Go back (totalWeeks - 1) weeks to starting Monday
        val daysBack = (totalWeeks - 1) * 7
        cal.add(Calendar.DAY_OF_YEAR, -daysBack)
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        var lastMonth = ""

        for (w in 0 until totalWeeks) {
            val weekDays = mutableListOf<HeatmapDayData>()
            for (d in 0 until 7) {
                val dateStr = sdf.format(cal.time)
                val count = dateCountMap[dateStr] ?: 0
                val currentMonth = monthFormat.format(cal.time)
                val isNewMonth = currentMonth != lastMonth && d == 0
                if (d == 0 && currentMonth != lastMonth) {
                    lastMonth = currentMonth
                }

                weekDays.add(
                    HeatmapDayData(
                        dateString = dateStr,
                        count = count,
                        dayOfWeekIndex = d,
                        monthName = currentMonth,
                        isFirstWeekOfMonth = isNewMonth
                    )
                )
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            list.add(weekDays)
        }
        list
    }

    val totalContributions = remember(dateCountMap) {
        dateCountMap.values.sum()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
            .testTag("streak_heatmap_card")
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title ?: "$totalContributions completions in the last $totalWeeks weeks",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Grid Area (with Day of Week Labels on left)
            val horizontalScrollState = rememberScrollState(Int.MAX_VALUE) // Scroll to the latest weeks by default

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left Day Labels (Mon, Wed, Fri) aligned with rows
                Column(
                    modifier = Modifier.padding(top = 22.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val dayLabels = listOf("Mon", "", "Wed", "", "Fri", "", "")
                    dayLabels.forEach { label ->
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(28.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (label.isNotEmpty()) {
                                Text(
                                    text = label,
                                    color = TextTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Horizontal scrollable weeks grid with month header
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    weeksData.forEachIndexed { weekIndex, week ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Month label header on top of the first week of month
                            val firstDay = week.firstOrNull()
                            Box(
                                modifier = Modifier
                                    .height(18.dp)
                                    .width(14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (firstDay?.isFirstWeekOfMonth == true || weekIndex == 0) {
                                    Text(
                                        text = firstDay?.monthName ?: "",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // 7 Days column
                            week.forEach { day ->
                                val cellColor = getHeatmapCellColor(day.count)
                                val isSelected = selectedCell?.dateString == day.dateString

                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.5.dp))
                                        .background(cellColor)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.5.dp,
                                            color = if (isSelected) Color.White else DarkBorderHighlight,
                                            shape = RoundedCornerShape(3.5.dp)
                                        )
                                        .clickable { selectedCell = day }
                                )
                            }
                        }
                    }
                }
            }

            // Selected cell inspection pill
            AnimatedVisibility(visible = selectedCell != null) {
                selectedCell?.let { cell ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceCard)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${cell.dateString}: ${cell.count} ${if (cell.count == 1) "habit" else "habits"} completed",
                            color = ElectricOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Legend: "Learn how we count contributions  Less [][][][][] More"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity consistency matrix",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Less", color = TextTertiary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(6.dp))

                    listOf(0, 1, 2, 3, 4).forEach { level ->
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(getHeatmapCellColor(level))
                                .border(0.5.dp, DarkBorderHighlight, RoundedCornerShape(2.5.dp))
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }

                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "More", color = TextTertiary, fontSize = 10.sp)
                }
            }
        }
    }
}

fun getHeatmapCellColor(count: Int): Color {
    return when {
        count <= 0 -> Color(0xFF16161E)
        count == 1 -> Color(0xFF5A2508)
        count == 2 -> Color(0xFFB84500)
        count == 3 -> Color(0xFFFF6A00)
        else -> Color(0xFFFFA233)
    }
}
