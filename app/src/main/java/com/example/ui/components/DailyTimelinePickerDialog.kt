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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyTimelinePickerDialog(
    currentTimeline: String,
    onTimelineSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var startHour by remember { mutableIntStateOf(10) }
    var startMin by remember { mutableIntStateOf(0) }
    var endHour by remember { mutableIntStateOf(12) }
    var endMin by remember { mutableIntStateOf(0) }
    var selectedPreset by remember { mutableStateOf(currentTimeline) }

    val presets = listOf(
        "08:00 - 10:00 (2h)",
        "09:00 - 12:00 (3h)",
        "10:00 - 12:00 (2h)",
        "13:00 - 15:00 (2h)",
        "14:00 - 17:00 (3h)",
        "16:00 - 18:00 (2h)",
        "19:00 - 21:00 (2h)",
        "20:00 - 22:30 (2.5h)",
        "All Day Focus"
    )

    fun formatTimeline(): String {
        val sH = startHour.toString().padStart(2, '0')
        val sM = startMin.toString().padStart(2, '0')
        val eH = endHour.toString().padStart(2, '0')
        val eM = endMin.toString().padStart(2, '0')
        val durationMins = (endHour * 60 + endMin) - (startHour * 60 + startMin)
        val hours = durationMins / 60
        val mins = durationMins % 60
        val durationStr = if (hours > 0 && mins > 0) "${hours}h ${mins}m"
        else if (hours > 0) "${hours}h"
        else "${mins}m"

        return "$sH:$sM - $eH:$eM ($durationStr)"
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = ElectricOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Timeline Schedule",
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

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select scheduled time window for this daily task:",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Presets Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = selectedPreset == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) OrangeSubtle else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricOrange else DarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedPreset = preset
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = preset,
                                color = if (isSelected) ElectricOrange else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Hours Adjuster
                Text(
                    text = "OR CUSTOM HOURS",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Start Time Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Start Time", color = TextTertiary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${startHour.toString().padStart(2, '0')}:${startMin.toString().padStart(2, '0')}",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurface)
                                    .clickable { startHour = (startHour + 23) % 24; selectedPreset = formatTimeline() }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) { Text("-1h", color = TextSecondary, fontSize = 11.sp) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurface)
                                    .clickable { startHour = (startHour + 1) % 24; selectedPreset = formatTimeline() }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) { Text("+1h", color = ElectricOrange, fontSize = 11.sp) }
                        }
                    }

                    // End Time Box
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("End Time", color = TextTertiary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${endHour.toString().padStart(2, '0')}:${endMin.toString().padStart(2, '0')}",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurface)
                                    .clickable { endHour = (endHour + 23) % 24; selectedPreset = formatTimeline() }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) { Text("-1h", color = TextSecondary, fontSize = 11.sp) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurface)
                                    .clickable { endHour = (endHour + 1) % 24; selectedPreset = formatTimeline() }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) { Text("+1h", color = ElectricOrange, fontSize = 11.sp) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
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
                            onTimelineSelected(selectedPreset)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("confirm_daily_timeline_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Set Timeline", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
