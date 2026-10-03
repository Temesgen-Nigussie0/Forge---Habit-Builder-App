package com.example.ui.tasks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.HabitRepository
import com.example.data.model.TaskItem
import com.example.notifications.ForgeNotificationHelper
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasksView(
    tasks: List<TaskItem>,
    onToggleTask: (TaskItem) -> Unit,
    onEditTask: (TaskItem) -> Unit,
    onAddNewTask: () -> Unit,
    workspaces: List<String> = emptyList(),
    onAddNewWorkspace: (String) -> Unit = {},
    viewMode: String = "BENTO",
    onSelectViewMode: (String) -> Unit = {},
    onMoveKanbanStatus: (Long, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedWorkspaceFilter by remember { mutableStateOf("All") }
    var selectedPriorityFilter by remember { mutableStateOf("ALL") }
    var isTaskOptionsMenuOpen by remember { mutableStateOf(false) }
    var isCreateWorkspaceDialogOpen by remember { mutableStateOf(false) }
    var newWorkspaceInput by remember { mutableStateOf("") }

    val activeWorkspaces = remember(workspaces) {
        if (workspaces.isNotEmpty()) workspaces
        else listOf("Personal Workspace", "Client Work", "Creative Studio")
    }

    var selectedMonthIndex by remember {
        mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) // 0..11
    }
    var selectedDayNumber by remember {
        mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }
    var isMonthDropdownOpen by remember { mutableStateOf(false) }

    // Long-pressed task for creation detail inspection
    var inspectingTask by remember { mutableStateOf<TaskItem?>(null) }

    val monthsList = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Calculate days for selected month (1 to 28..31)
    val daysInSelectedMonth = remember(selectedMonthIndex) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, selectedMonthIndex)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val sdf = SimpleDateFormat("EEE", Locale.US)
        val list = mutableListOf<Pair<Int, String>>() // dayNumber, dayName
        for (d in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            list.add(Pair(d, sdf.format(cal.time)))
        }
        list
    }

    // Selected full date string: yyyy-MM-dd
    val selectedFullDateString = remember(selectedMonthIndex, selectedDayNumber) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, selectedMonthIndex)
        cal.set(Calendar.DAY_OF_MONTH, selectedDayNumber.coerceIn(1, 31))
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    // Filter tasks by selected workspace first
    val workspaceTasks = remember(tasks, selectedWorkspaceFilter) {
        if (selectedWorkspaceFilter == "All") tasks
        else tasks.filter { it.clientOrCourse?.equals(selectedWorkspaceFilter, ignoreCase = true) == true }
    }

    // Low Priority and Medium Priority tasks for the 2 bento columns
    val lowPriorityTasks = remember(workspaceTasks) {
        workspaceTasks.filter { it.priority.equals("Low", ignoreCase = true) }
    }
    val mediumPriorityTasks = remember(workspaceTasks) {
        workspaceTasks.filter { it.priority.equals("Medium", ignoreCase = true) }
    }

    // Today's Task List:
    // Sorted strictly: Uncompleted first (Urgent -> High -> Medium -> Low), then Completed tasks on bottom!
    val sortedTasks = remember(workspaceTasks, searchQuery, selectedPriorityFilter) {
        val filtered = if (searchQuery.isBlank()) workspaceTasks
        else workspaceTasks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    (it.clientOrCourse?.contains(searchQuery, ignoreCase = true) == true) ||
                    (it.notes?.contains(searchQuery, ignoreCase = true) == true)
        }

        val priorityFiltered = when (selectedPriorityFilter) {
            "LOW" -> filtered.filter { it.priority.equals("Low", ignoreCase = true) }
            "MEDIUM" -> filtered.filter { it.priority.equals("Medium", ignoreCase = true) }
            "URGENT" -> filtered.filter { it.priority.equals("Urgent", ignoreCase = true) || it.priority.equals("High", ignoreCase = true) }
            else -> filtered
        }

        priorityFiltered.sortedWith(
            compareBy<TaskItem> { it.isCompleted } // false (0) first, true (1) last
                .thenBy {
                    when (it.priority) {
                        "Urgent" -> 1
                        "High" -> 2
                        "Medium" -> 3
                        else -> 4
                    }
                }
        )
    }

    val completedCount = workspaceTasks.count { it.isCompleted }
    val totalCount = workspaceTasks.size.coerceAtLeast(1)
    val completionRatio = (completedCount.toFloat() / totalCount).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // 1. Top Bar: Vector Button in Top Left Corner + Search Bar
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Vector style button in top left corner with categorized options
            Box {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.2.dp, ElectricOrange.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clickable { isTaskOptionsMenuOpen = true }
                        .testTag("tasks_view_options_vector_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Task Options & Categories",
                        tint = ElectricOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = isTaskOptionsMenuOpen,
                    onDismissRequest = { isTaskOptionsMenuOpen = false },
                    modifier = Modifier
                        .background(DarkSurfaceElevated)
                        .border(1.dp, ElectricOrange.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .width(260.dp)
                ) {
                    Text(
                        text = "VIEW LAYOUTS",
                        color = ElectricOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                    listOf(
                        Triple("BENTO", "📅 Timeline Bento", "Timeline strip & priority columns"),
                        Triple("EISENHOWER", "🎯 4-Quadrant Matrix", "Urgent vs Important matrix"),
                        Triple("KANBAN", "📋 Kanban Sprint", "Backlog, In Progress & Shipped")
                    ).forEach { (modeKey, title, subtitle) ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = title,
                                        color = if (viewMode == modeKey) ElectricOrange else TextPrimary,
                                        fontWeight = if (viewMode == modeKey) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                    Text(text = subtitle, color = TextTertiary, fontSize = 10.sp)
                                }
                            },
                            onClick = {
                                onSelectViewMode(modeKey)
                                isTaskOptionsMenuOpen = false
                            }
                        )
                    }

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "PRIORITY COLUMNS & FILTERS",
                        color = ElectricOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                    listOf(
                        "ALL" to "🌟 All Priorities",
                        "MEDIUM" to "🟠 Column 1: Medium (${mediumPriorityTasks.size})",
                        "LOW" to "⚪ Column 2: Low (${lowPriorityTasks.size})",
                        "URGENT" to "🔴 Urgent & High Tasks"
                    ).forEach { (pKey, pLabel) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = pLabel,
                                    color = if (selectedPriorityFilter == pKey) ElectricOrange else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedPriorityFilter == pKey) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedPriorityFilter = pKey
                                isTaskOptionsMenuOpen = false
                            }
                        )
                    }

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "WORKSPACE CATEGORIES",
                        color = ElectricOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                    (listOf("All") + activeWorkspaces).take(6).forEach { wsName ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (wsName == "All") "📂 All Workspaces" else "📁 $wsName",
                                    color = if (selectedWorkspaceFilter == wsName) ElectricOrange else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedWorkspaceFilter == wsName) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedWorkspaceFilter = wsName
                                isTaskOptionsMenuOpen = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("task_search_bar"),
                placeholder = {
                    Text("Search tasks...", color = TextTertiary, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // WORKSPACE FILTER STRIP (+ button first as requested by user!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First item: + Button to create a new workspace
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrangeSubtle)
                    .border(1.2.dp, ElectricOrange, RoundedCornerShape(12.dp))
                    .clickable {
                        newWorkspaceInput = ""
                        isCreateWorkspaceDialogOpen = true
                    }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("tasks_add_workspace_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New Workspace",
                        tint = ElectricOrange,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New Workspace",
                        color = ElectricOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // "All Workspaces" Chip
            val isAllSelected = selectedWorkspaceFilter == "All"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAllSelected) ElectricOrange else DarkSurfaceElevated)
                    .border(1.dp, if (isAllSelected) ElectricOrange else DarkBorder, RoundedCornerShape(12.dp))
                    .clickable { selectedWorkspaceFilter = "All" }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "All (${tasks.size})",
                    color = if (isAllSelected) Color.White else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                )
            }

            // User Workspaces (No mock data, dynamically loaded from user profile!)
            activeWorkspaces.forEach { ws ->
                val isSelected = selectedWorkspaceFilter == ws
                val countInWs = tasks.count { it.clientOrCourse?.equals(ws, ignoreCase = true) == true }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ElectricOrange else DarkSurfaceElevated)
                        .border(1.dp, if (isSelected) ElectricOrange else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedWorkspaceFilter = ws }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (countInWs > 0) "$ws ($countInWs)" else ws,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // VIEW MODE SELECTOR: Timeline Bento vs Eisenhower 4Q vs Kanban Sprint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurfaceElevated)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                "BENTO" to "📅 Timeline",
                "EISENHOWER" to "🎯 4-Quadrant",
                "KANBAN" to "📋 Kanban"
            ).forEach { (modeKey, label) ->
                val isSelected = viewMode == modeKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (isSelected) OrangeSubtle else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) ElectricOrange else Color.Transparent,
                            RoundedCornerShape(11.dp)
                        )
                        .clickable { onSelectViewMode(modeKey) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) ElectricOrange else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content switching based on viewMode
        when (viewMode) {
            "EISENHOWER" -> {
                EisenhowerMatrixView(
                    tasks = tasks,
                    onToggleTask = onToggleTask,
                    onEditTask = onEditTask
                )
            }
            "KANBAN" -> {
                KanbanSprintBoardView(
                    tasks = tasks,
                    onToggleTask = onToggleTask,
                    onMoveStatus = onMoveKanbanStatus,
                    onEditTask = onEditTask
                )
            }
            else -> {
                // BENTO TIMELINE (Default layout)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // "Your Timeline" Section with Month Dropdown
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Timeline",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // Month Dropdown Trigger
                                Box {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(DarkSurfaceElevated)
                                            .clickable { isMonthDropdownOpen = true }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = monthsList[selectedMonthIndex],
                                            color = ElectricOrange,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ExpandMore,
                                            contentDescription = "Select Month",
                                            tint = ElectricOrange,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = isMonthDropdownOpen,
                                        onDismissRequest = { isMonthDropdownOpen = false },
                                        modifier = Modifier.background(DarkSurfaceCard)
                                    ) {
                                        monthsList.forEachIndexed { idx, monthName ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = monthName,
                                                        color = if (idx == selectedMonthIndex) ElectricOrange else TextPrimary,
                                                        fontWeight = if (idx == selectedMonthIndex) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    selectedMonthIndex = idx
                                                    isMonthDropdownOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Horizontal Timeline Strip scrolled through all days of the month
                            val timelineScrollState = rememberScrollState()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(timelineScrollState),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                daysInSelectedMonth.forEach { (dNum, dName) ->
                                    val isSelected = selectedDayNumber == dNum
                                    Box(
                                        modifier = Modifier
                                            .width(46.dp)
                                            .height(60.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) ElectricOrange else DarkSurfaceElevated)
                                            .border(
                                                1.dp,
                                                if (isSelected) ElectricOrange else DarkBorder,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedDayNumber = dNum }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$dNum",
                                                color = if (isSelected) Color.White else TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = dName,
                                                color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextTertiary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Priority Section Heading (with dynamic metrics and zero mock data!)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Priority Matrix",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val urgentHighCount = workspaceTasks.count { it.priority == "Urgent" || it.priority == "High" }
                            Text(
                                text = "$urgentHighCount Urgent/High • ${workspaceTasks.size} Total",
                                color = if (urgentHighCount > 0) CoralRed else TextTertiary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // 2 Bento Columns (White Card = Low Priority; Orange Card = Medium Priority)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // LEFT WHITE CARD: LOW PRIORITY TASKS
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(Color(0xFFF2F2F5))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Low Priority Tasks",
                                            color = Color.Black,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (lowPriorityTasks.isEmpty()) {
                                            Text(
                                                text = "No low priority tasks",
                                                color = Color.DarkGray,
                                                fontSize = 11.sp
                                            )
                                        } else {
                                            lowPriorityTasks.take(4).forEach { t ->
                                                ClickableBentoRow(
                                                    task = t,
                                                    textColor = Color.Black,
                                                    onToggle = { onToggleTask(t) },
                                                    onLongClick = { inspectingTask = t }
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }

                                // Daily Task Completion Ratio Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Daily Task",
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "$completedCount/$totalCount done",
                                                color = TextTertiary,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier.size(42.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                progress = { completionRatio },
                                                modifier = Modifier.size(38.dp),
                                                color = Color.White,
                                                trackColor = Color(0x30FFFFFF),
                                                strokeWidth = 3.5.dp,
                                                strokeCap = StrokeCap.Round
                                            )
                                            Text(
                                                text = "${(completionRatio * 100).toInt()}%",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // RIGHT ORANGE CARD: MEDIUM PRIORITY TASKS
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(ElectricOrange)
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Medium Priority Tasks",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (mediumPriorityTasks.isEmpty()) {
                                        Text(
                                            text = "No medium priority tasks",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                    } else {
                                        mediumPriorityTasks.take(5).forEach { t ->
                                            ClickableBentoRow(
                                                task = t,
                                                textColor = Color.White,
                                                onToggle = { onToggleTask(t) },
                                                onLongClick = { inspectingTask = t }
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // "Today's Task" Section Header
                    item {
                        Text(
                            text = "Today's Task",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // Minimalist Task Cards
                    if (sortedTasks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tasks found",
                                    color = TextTertiary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(sortedTasks, key = { it.id }) { task ->
                            MinimalistTaskCardWithBadges(
                                task = task,
                                selectedDateStr = selectedFullDateString,
                                onToggle = { onToggleTask(task) },
                                onClick = { onEditTask(task) },
                                onLongClick = { inspectingTask = task }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }

    // Detail Dialog on Long Press
    inspectingTask?.let { task ->
        TaskCreationDetailDialog(
            task = task,
            onDismiss = { inspectingTask = null }
        )
    }

    // Create New Workspace Dialog
    if (isCreateWorkspaceDialogOpen) {
        Dialog(onDismissRequest = { isCreateWorkspaceDialogOpen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, ElectricOrange.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
                color = DarkSurfaceCard
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = null,
                                tint = ElectricOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create New Workspace",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { isCreateWorkspaceDialogOpen = false },
                            modifier = Modifier.size(28.dp)
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
                        text = "Separate projects, clients, or courses into dedicated real workspaces.",
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newWorkspaceInput,
                        onValueChange = { newWorkspaceInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_workspace_input_field"),
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("e.g. Design Agency, Thesis 2026", color = TextTertiary, fontSize = 13.sp) },
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

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isCreateWorkspaceDialogOpen = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val trimmed = newWorkspaceInput.trim()
                                if (trimmed.isNotBlank()) {
                                    onAddNewWorkspace(trimmed)
                                    selectedWorkspaceFilter = trimmed
                                    newWorkspaceInput = ""
                                    isCreateWorkspaceDialogOpen = false
                                }
                            },
                            enabled = newWorkspaceInput.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("confirm_create_workspace_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricOrange,
                                disabledContainerColor = DarkSurfaceElevated
                            )
                        ) {
                            Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClickableBentoRow(
    task: TaskItem,
    textColor: Color,
    onToggle: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onLongClick
            )
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) textColor else Color.Transparent)
                .border(1.2.dp, textColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (textColor == Color.White) ElectricOrange else Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = task.title,
            color = if (task.isCompleted) textColor.copy(alpha = 0.5f) else textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
        )
    }
}

/**
 * Minimalist Task Card:
 * - High priority shown first, completed tasks changed to orange background and sent to bottom.
 * - Prominent Priority Badge in the top right corner!
 * - Displays deadline relationship to selected date (Due today, Overdue, Upcoming).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MinimalistTaskCardWithBadges(
    task: TaskItem,
    selectedDateStr: String,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isCompleted = task.isCompleted

    val priorityColor = when (task.priority) {
        "Urgent" -> CoralRed
        "High" -> ElectricOrange
        "Medium" -> SunsetAmber
        else -> CyanBlue
    }

    // Completed card changes to orange background color as requested!
    val cardBackground = if (isCompleted) {
        OrangeSubtle // Orange-tinted background for completed tasks!
    } else {
        DarkSurfaceElevated
    }

    val cardBorder = if (isCompleted) {
        ElectricOrange.copy(alpha = 0.5f)
    } else {
        DarkBorder
    }

    // Determine status relative to selected timeline day
    val dateStatus = remember(task.dueDate, selectedDateStr, isCompleted) {
        val due = task.dueDate
        when {
            isCompleted -> "Completed"
            due == null -> ""
            due == selectedDateStr -> "Due on selected date"
            due < selectedDateStr -> "Overdue (Should be done before this day)"
            else -> "Due on $due"
        }
    }

    com.example.ui.components.SwipeActionCard(
        onSwipeRight = onToggle,
        onSwipeLeft = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(cardBackground)
                .border(1.dp, cardBorder, RoundedCornerShape(22.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(16.dp)
                .testTag("task_card_${task.id}")
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Top Row: Title + PROMINENT PRIORITY BADGE in top right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        color = if (isCompleted) TextPrimary.copy(alpha = 0.7f) else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Prominent Priority Badge in top right corner
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(priorityColor.copy(alpha = 0.18f))
                            .border(1.dp, priorityColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority.uppercase(),
                            color = priorityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                if (!task.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.notes,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2,
                        lineHeight = 16.sp
                    )
                }

                // Date relationship status + Burning Alert test trigger
                val context = LocalContext.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    if (dateStatus.isNotBlank()) {
                        Text(
                            text = dateStatus,
                            color = if (dateStatus.contains("Overdue")) CoralRed else if (isCompleted) SuccessEmerald else SunsetAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if ((task.priority == "Urgent" || task.priority == "High") && !isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CoralRed.copy(alpha = 0.16f))
                                .border(0.8.dp, CoralRed.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                .clickable {
                                    ForgeNotificationHelper.sendBurningTaskAlert(
                                        context = context,
                                        taskTitle = task.title,
                                        priority = task.priority,
                                        timeRemainingText = "Burning down!"
                                    )
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Alert",
                                    tint = CoralRed,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Alert 🔥",
                                    color = CoralRed,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Checkmark button (matching Image 3)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) ElectricOrange else DarkSurfaceCard)
                    .border(1.2.dp, if (isCompleted) ElectricOrange else DarkBorderHighlight, CircleShape)
                    .clickable(onClick = onToggle)
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
    }
}
}

/**
 * Detail modal shown when user long-presses a task, showing creation time and metadata.
 */
@Composable
private fun TaskCreationDetailDialog(
    task: TaskItem,
    onDismiss: () -> Unit
) {
    val createdFormatted = remember(task.createdAt) {
        val sdf = SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a", Locale.US)
        sdf.format(Date(task.createdAt))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkBorderHighlight, RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ElectricOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Task Specifications",
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
                    text = task.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!task.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = task.notes,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Priority", color = TextTertiary, fontSize = 12.sp)
                            Text(text = task.priority, color = ElectricOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Workspace", color = TextTertiary, fontSize = 12.sp)
                            Text(text = task.clientOrCourse ?: "General", color = TextPrimary, fontSize = 12.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Due Date", color = TextTertiary, fontSize = 12.sp)
                            Text(text = task.dueDate ?: "No deadline", color = TextPrimary, fontSize = 12.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Created Timestamp", color = TextTertiary, fontSize = 12.sp)
                            Text(text = createdFormatted, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
