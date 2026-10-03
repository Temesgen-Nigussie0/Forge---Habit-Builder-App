package com.example.ui.dashboard

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Habit
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitDialog(
    habitToEdit: Habit?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: String,
        iconName: String,
        colorHex: String,
        targetCount: Int,
        unit: String,
        timeOfDay: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(habitToEdit?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(habitToEdit?.category ?: "Health") }
    var selectedIcon by remember { mutableStateOf(habitToEdit?.iconName ?: "workout") }
    var selectedColor by remember { mutableStateOf(habitToEdit?.colorHex ?: "#FF6A00") }
    var targetCount by remember { mutableIntStateOf(habitToEdit?.targetCount ?: 1) }
    var unit by remember { mutableStateOf(habitToEdit?.unit ?: "times") }
    var timeOfDay by remember { mutableStateOf(habitToEdit?.timeOfDay ?: "Morning") }

    val categories = listOf("Health", "Mind", "Productivity", "Routine", "Sleep", "Growth")
    val icons = listOf(
        "workout" to "🏃",
        "water" to "💧",
        "book" to "📖",
        "meditation" to "🧘",
        "target" to "🎯",
        "sunlight" to "☀️",
        "sleep" to "🌙",
        "meal" to "🥗",
        "brain" to "🧠",
        "code" to "💻"
    )
    val colors = listOf(
        "#FF6A00", // Orange
        "#FF9F0A", // Amber
        "#30D158", // Green
        "#0A84FF", // Blue
        "#BF5AF2", // Purple
        "#FF453A"  // Red
    )
    val times = listOf("Morning", "Afternoon", "Evening", "Anytime")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkBorderHighlight, RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (habitToEdit == null) "New Habit" else "Edit Habit",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Name input
                Text(
                    text = "HABIT NAME",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = { Text("e.g. Read 20 minutes", color = TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Category selector
                Text(
                    text = "CATEGORY",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) OrangeSubtle else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricOrange else DarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) ElectricOrange else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Icon picker
                Text(
                    text = "ICON",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    icons.forEach { (key, emoji) ->
                        val isSelected = selectedIcon == key
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) OrangeSubtle else DarkSurfaceElevated)
                                .border(
                                    1.5.dp,
                                    if (isSelected) ElectricOrange else DarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedIcon = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Time of Day
                Text(
                    text = "TIME OF DAY",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    times.forEach { time ->
                        val isSelected = timeOfDay == time
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) OrangeSubtle else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricOrange else DarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { timeOfDay = time }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = time,
                                color = if (isSelected) ElectricOrange else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Daily target counter
                Text(
                    text = "DAILY TARGET",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$targetCount $unit per day",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (targetCount > 1) targetCount-- },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceCard)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = { if (targetCount < 20) targetCount++ },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceCard)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(
                                name.trim(),
                                selectedCategory,
                                selectedIcon,
                                selectedColor,
                                targetCount,
                                unit,
                                timeOfDay
                            )
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_habit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricOrange,
                        disabledContainerColor = DarkSurfaceElevated
                    )
                ) {
                    Text(
                        text = if (habitToEdit == null) "Create Habit" else "Save Changes",
                        color = if (name.isNotBlank()) Color.White else TextTertiary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
