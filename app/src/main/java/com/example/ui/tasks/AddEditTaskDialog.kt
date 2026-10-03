package com.example.ui.tasks

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.HabitRepository
import com.example.data.model.TaskItem
import com.example.ui.components.CoolBlackCalendarDialog
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun AddEditTaskDialog(
    taskToEdit: TaskItem?,
    availableWorkspaces: List<String> = emptyList(),
    onAddNewWorkspace: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        notes: String?,
        category: String,
        priority: String,
        scope: String,
        dueDate: String?,
        endDate: String?,
        dailyTimeline: String?,
        estimatedHours: Float,
        clientOrCourse: String?,
        tags: String
    ) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.notes ?: "") }

    // Workspaces
    var currentWorkspaceList by remember(availableWorkspaces) {
        mutableStateOf(
            if (availableWorkspaces.isNotEmpty()) availableWorkspaces
            else listOf("Personal Workspace", "Client Work")
        )
    }
    var selectedWorkspace by remember {
        mutableStateOf(
            taskToEdit?.clientOrCourse ?: currentWorkspaceList.firstOrNull() ?: "Personal Workspace"
        )
    }
    var isWorkspaceDropdownOpen by remember { mutableStateOf(false) }
    var isCreateWorkspaceDialogOpen by remember { mutableStateOf(false) }
    var newWorkspaceNameInput by remember { mutableStateOf("") }

    // Priority
    var selectedPriority by remember { mutableStateOf(taskToEdit?.priority ?: "High") }
    val priorities = listOf("Urgent", "High", "Medium", "Low")

    // Date Mode: "RANGE" (Due Date to End Date) vs "FIXED" (Fixed Ending Date Only)
    var isDateRangeMode by remember {
        mutableStateOf(taskToEdit?.endDate != null && taskToEdit.endDate != taskToEdit.dueDate)
    }
    var dueDate by remember { mutableStateOf(taskToEdit?.dueDate ?: HabitRepository.getTodayDateString()) }
    var endDate by remember { mutableStateOf(taskToEdit?.endDate ?: HabitRepository.getDateOffset(7)) }

    // Calendar Pickers
    var activeCalendarTarget by remember { mutableStateOf<String?>(null) } // "DUE" or "END" or null

    // Daily Timeline
    val dailyTimeline = taskToEdit?.dailyTimeline ?: "10:00 - 12:00 (2h)"

    // Category
    var selectedCategory by remember { mutableStateOf(taskToEdit?.category ?: "Software Dev") }
    val categories = listOf("Software Dev", "Design & UI", "Video & Photo", "Academics", "Client Work", "Personal")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, DarkBorderHighlight, RoundedCornerShape(26.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (taskToEdit == null) "Create Task" else "Edit Task",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
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

                Spacer(modifier = Modifier.height(18.dp))

                // Title
                Text(
                    text = "Title",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("e.g. Design app design system", color = TextTertiary, fontSize = 14.sp) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Description",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .testTag("task_description_input"),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            "Enter task objectives, deliverables, or specifications...",
                            color = TextTertiary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Workspace Section: prominent + button, no mock data!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workspace",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // + New Workspace Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(OrangeSubtle)
                            .clickable { isCreateWorkspaceDialogOpen = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("add_workspace_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Workspace",
                            tint = ElectricOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Workspace",
                            color = ElectricOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .clickable { isWorkspaceDropdownOpen = true }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedWorkspace,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isWorkspaceDropdownOpen,
                        onDismissRequest = { isWorkspaceDropdownOpen = false },
                        modifier = Modifier.background(DarkSurfaceCard)
                    ) {
                        currentWorkspaceList.forEach { ws ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            ws,
                                            color = if (ws == selectedWorkspace) ElectricOrange else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = if (ws == selectedWorkspace) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (ws == selectedWorkspace) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = ElectricOrange,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedWorkspace = ws
                                    isWorkspaceDropdownOpen = false
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = ElectricOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "+ Create New Workspace",
                                        color = ElectricOrange,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            onClick = {
                                isWorkspaceDropdownOpen = false
                                isCreateWorkspaceDialogOpen = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Priority Section with Heading Name & Real System Options
                Text(
                    text = "Task Priority",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.forEach { p ->
                        val isSelected = selectedPriority == p
                        val pColor = when (p) {
                            "Urgent" -> CoralRed
                            "High" -> ElectricOrange
                            "Medium" -> SunsetAmber
                            else -> CyanBlue
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) pColor.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) pColor else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPriority = p }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p,
                                color = if (isSelected) pColor else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Date Configuration Section: Date Range vs Fixed Ending Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Dates",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Toggle: Fixed Date vs Range
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isDateRangeMode) ElectricOrange else Color.Transparent)
                                .clickable { isDateRangeMode = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Fixed Date",
                                color = if (!isDateRangeMode) Color.White else TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = if (!isDateRangeMode) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDateRangeMode) ElectricOrange else Color.Transparent)
                                .clickable { isDateRangeMode = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Date Range",
                                color = if (isDateRangeMode) Color.White else TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = if (isDateRangeMode) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (isDateRangeMode) {
                    // Due Date + End Date side-by-side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Due Date Box
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Due Date (Start)", color = TextTertiary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                    .clickable { activeCalendarTarget = "DUE" }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(dueDate, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Pick Due Date",
                                        tint = ElectricOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // End Date Box
                        Column(modifier = Modifier.weight(1f)) {
                            Text("End Date", color = TextTertiary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                    .clickable { activeCalendarTarget = "END" }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(endDate, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Pick End Date",
                                        tint = ElectricOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Single Fixed Ending Date Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                            .clickable { activeCalendarTarget = "FIXED" }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Fixed Ending Date", color = TextTertiary, fontSize = 10.sp)
                                Text(endDate, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Pick Ending Date",
                                tint = ElectricOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (taskToEdit != null && onDelete != null) {
                        IconButton(
                            onClick = { onDelete(taskToEdit.id) },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CoralRed.copy(alpha = 0.15f))
                                .border(1.dp, CoralRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Task",
                                tint = CoralRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val computedEndDate = if (isDateRangeMode) endDate else dueDate
                            onSave(
                                title.ifBlank { "Untitled Task" },
                                description.ifBlank { null },
                                selectedCategory,
                                selectedPriority,
                                if (isDateRangeMode) "Long-Term" else "Short-Term",
                                dueDate,
                                computedEndDate,
                                dailyTimeline,
                                2.0f,
                                selectedWorkspace,
                                "Task, Active"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_task_submit_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (taskToEdit == null) "Create Task" else "Save Changes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Cool Black Calendar Dialog
    if (activeCalendarTarget != null) {
        val target = activeCalendarTarget!!
        val curr = when (target) {
            "DUE" -> dueDate
            "END" -> endDate
            else -> endDate
        }
        CoolBlackCalendarDialog(
            initialDate = curr,
            title = when (target) {
                "DUE" -> "Select Due Date"
                "END" -> "Select End Date"
                else -> "Select Ending Date"
            },
            onDateSelected = { pickedDate ->
                when (target) {
                    "DUE" -> {
                        dueDate = pickedDate
                        if (!isDateRangeMode) endDate = pickedDate
                    }
                    "END" -> endDate = pickedDate
                    "FIXED" -> {
                        endDate = pickedDate
                        dueDate = pickedDate
                    }
                }
            },
            onDismiss = { activeCalendarTarget = null }
        )
    }



    // Create New Workspace Dialog
    if (isCreateWorkspaceDialogOpen) {
        Dialog(onDismissRequest = { isCreateWorkspaceDialogOpen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkBorderHighlight, RoundedCornerShape(22.dp)),
                color = DarkSurfaceCard
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Create New Workspace",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Separate tasks by client, project, or creative domain:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = newWorkspaceNameInput,
                        onValueChange = { newWorkspaceNameInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Design Agency, Semester 2", color = TextTertiary, fontSize = 13.sp) },
                        shape = RoundedCornerShape(14.dp),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isCreateWorkspaceDialogOpen = false },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceElevated,
                                contentColor = TextSecondary
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val trimmed = newWorkspaceNameInput.trim()
                                if (trimmed.isNotBlank()) {
                                    val updated = currentWorkspaceList.toMutableList()
                                    if (!updated.contains(trimmed)) updated.add(trimmed)
                                    currentWorkspaceList = updated
                                    selectedWorkspace = trimmed
                                    onAddNewWorkspace(trimmed)
                                    newWorkspaceNameInput = ""
                                    isCreateWorkspaceDialogOpen = false
                                }
                            },
                            modifier = Modifier.weight(1.3f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Create", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
