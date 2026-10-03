package com.example.ui.boss

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.HabitRepository
import com.example.data.model.BossChallenge
import com.example.data.model.Habit
import com.example.ui.dashboard.HabitWithEntry
import com.example.ui.theme.BossCrimson
import com.example.ui.theme.BossFlame
import com.example.ui.theme.BossFlameGlow
import com.example.ui.theme.BossGold
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun BossModeScreen(
    activeChallenge: BossChallenge?,
    allHabits: List<Habit>,
    habitsWithEntries: List<HabitWithEntry>,
    onStartChallenge: (
        title: String,
        selectedHabitIds: List<Long>,
        durationDays: Int,
        intensity: String,
        punishment: String
    ) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onQuitChallenge: (id: Long, phrase: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeChallenge != null && activeChallenge.isActive) {
        ActiveBossChallengeView(
            challenge = activeChallenge,
            allHabits = allHabits,
            habitsWithEntries = habitsWithEntries,
            onToggleHabit = onToggleHabit,
            onQuitChallenge = onQuitChallenge,
            modifier = modifier
        )
    } else {
        BossModeInitiationWizard(
            allHabits = allHabits,
            onStartChallenge = onStartChallenge,
            modifier = modifier
        )
    }
}

@Composable
private fun ActiveBossChallengeView(
    challenge: BossChallenge,
    allHabits: List<Habit>,
    habitsWithEntries: List<HabitWithEntry>,
    onToggleHabit: (Habit) -> Unit,
    onQuitChallenge: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuitDialog by remember { mutableStateOf(false) }

    val targetIds = remember(challenge.targetHabitIds) {
        challenge.targetHabitIds.split(",").mapNotNull { it.trim().toLongOrNull() }.toSet()
    }

    val targetHabits = remember(habitsWithEntries, targetIds) {
        habitsWithEntries.filter { targetIds.contains(it.habit.id) }
    }

    val completedToday = targetHabits.count { it.isCompleted }
    val totalTarget = targetHabits.size
    val dailyProgress = if (totalTarget > 0) completedToday.toFloat() / totalTarget else 0f
    val overallProgress = (challenge.currentDayNumber.toFloat() / challenge.durationDays).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Boss Mode Crimson Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = BossFlame,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BOSS MODE ACTIVE",
                        color = BossFlame,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = challenge.title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BossFlameGlow)
                    .border(1.dp, BossFlame.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = challenge.intensity.uppercase(),
                    color = BossFlame,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Large HUD Progress Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF22090D), Color(0xFF14070A))
                    )
                )
                .border(1.5.dp, BossFlame.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DAY ${challenge.currentDayNumber}",
                            color = BossFlame,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "of ${challenge.durationDays} Days Contract",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Circular ring of completion
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { overallProgress },
                            modifier = Modifier.size(56.dp),
                            color = BossFlame,
                            trackColor = Color(0x30FF3B30),
                            strokeWidth = 5.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "${(overallProgress * 100).toInt()}%",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily check-off progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Today's Target Habits ($completedToday / $totalTarget)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (dailyProgress >= 1f) "SEALED FOR TODAY ✓" else "IN PROGRESS",
                        color = if (dailyProgress >= 1f) SuccessEmerald else SunsetAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { dailyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (dailyProgress >= 1f) SuccessEmerald else BossFlame,
                    trackColor = Color(0x20FFFFFF),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Declared Punishment / Cost of Failure Callout Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, CoralRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dangerous,
                        contentDescription = null,
                        tint = CoralRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DECLARED COST OF FAILURE",
                        color = CoralRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${challenge.punishment}”",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Target Habits List for Today
        Text(
            text = "CONTRACT CORE HABITS",
            color = BossFlame,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            targetHabits.forEach { item ->
                val habit = item.habit
                val isDone = item.isCompleted

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isDone) BossFlame.copy(alpha = 0.4f) else DarkBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onToggleHabit(habit) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔥", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = habit.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${habit.targetCount} ${habit.unit} daily",
                                    color = TextTertiary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Checkmark
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDone) BossFlame else Color.Transparent)
                                .border(1.5.dp, if (isDone) BossFlame else DarkBorderHighlight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Psychological Anti-Quit Trigger
        Button(
            onClick = { showQuitDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("abandon_challenge_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22080B)),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(CoralRed.copy(alpha = 0.5f))
            )
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = CoralRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Break Contract & Give Up",
                color = CoralRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(90.dp))
    }

    // Anti-Quit Chamber Dialog
    if (showQuitDialog) {
        AntiQuitChamberDialog(
            challenge = challenge,
            onDismiss = { showQuitDialog = false },
            onConfirmQuit = { phrase ->
                onQuitChallenge(challenge.id, phrase)
                showQuitDialog = false
            }
        )
    }
}

@Composable
private fun AntiQuitChamberDialog(
    challenge: BossChallenge,
    onDismiss: () -> Unit,
    onConfirmQuit: (String) -> Unit
) {
    val shamePhrase = "I GIVE UP ON MYSELF AND ACCEPT DEFEAT"
    var inputPhrase by remember { mutableStateOf("") }
    val isMatch = inputPhrase.trim() == shamePhrase

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, BossFlame, RoundedCornerShape(24.dp)),
            color = Color(0xFF14080A)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = BossFlame,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE QUIT CHAMBER",
                        color = BossFlame,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Boss Mode was built to eliminate impulsive quitting. If you abandon now, your contract is permanently failed and you must execute your declared punishment:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF260D12))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "“${challenge.punishment}”",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "To confirm failure, type the exact phrase in ALL CAPS below:",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = shamePhrase,
                    color = BossFlame,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputPhrase,
                    onValueChange = { inputPhrase = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("anti_quit_text_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BossFlame,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    placeholder = { Text("TYPE HERE IN ALL CAPS", color = TextTertiary, fontSize = 11.sp) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Text("Stay In The Fight", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { if (isMatch) onConfirmQuit(inputPhrase) },
                        enabled = isMatch,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("confirm_quit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BossFlame,
                            disabledContainerColor = Color(0xFF2C1318)
                        )
                    ) {
                        Text(
                            text = "Surrender",
                            color = if (isMatch) Color.White else TextTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BossModeInitiationWizard(
    allHabits: List<Habit>,
    onStartChallenge: (
        title: String,
        selectedHabitIds: List<Long>,
        durationDays: Int,
        intensity: String,
        punishment: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var currentStep by remember { mutableIntStateOf(0) } // 0: Welcome, 1: Habits, 2: Title, 3: Duration, 4: Intensity, 5: Stakes, 6: Seal Contract
    var title by remember { mutableStateOf("Operation Iron Will") }
    val selectedHabitIds = remember { mutableStateListOf<Long>() }
    var selectedDuration by remember { mutableIntStateOf(66) } // Default to Lally 66-day standard
    var selectedIntensity by remember { mutableStateOf("Extreme") }
    var punishment by remember { mutableStateOf("Donate $150 to a cause I strongly oppose") }

    // Initialize with first 2 habits if available
    remember(allHabits) {
        if (selectedHabitIds.isEmpty() && allHabits.isNotEmpty()) {
            selectedHabitIds.addAll(allHabits.take(3).map { it.id })
        }
    }

    val durationOptions = listOf(
        Triple(21, "21 Days", "Rapid installation protocol (Eliminate initial friction)"),
        Triple(30, "30 Days", "One month baseline discipline (Neural foundation)"),
        Triple(66, "66 Days", "Lally Science Gold Standard (Optimal habit automaticity)"),
        Triple(75, "75 Days", "75 Hard Toughness (Extreme mental resilience)"),
        Triple(100, "100 Days", "Century Club (Legendary elite identity transformation)")
    )

    val intensityOptions = listOf(
        Pair("Hard", "No zero days. Miss 1 day = reset to Day 1 immediately."),
        Pair("Extreme", "Strict adherence. Miss 1 day = immediate contract failure & loss of stakes."),
        Pair("Legendary", "Zero compromises. Strict daily execution + double targets on weekends.")
    )

    val punishmentPresets = listOf(
        "Donate $150 to a cause I strongly oppose",
        "Cold plunge at 5:00 AM every morning for two weeks",
        "Post a public video confession of failure on Instagram / LinkedIn",
        "Transfer $200 cash to my accountability partner"
    )

    val titlePresets = listOf(
        "⚔️ Operation Iron Will",
        "🧘 Monk Mode 2026",
        "🔥 Zero Compromise",
        "⚡ Hyper-Velocity Sprint",
        "🛡️ Titan Discipline",
        "🎯 The Forge Crucible"
    )

    // Validation for proceeding
    val canProceed = when (currentStep) {
        0 -> true
        1 -> selectedHabitIds.isNotEmpty()
        2 -> title.isNotBlank()
        3 -> selectedDuration > 0
        4 -> selectedIntensity.isNotBlank()
        5 -> punishment.isNotBlank()
        else -> true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0406))
    ) {
        // TOP NAVIGATION & STEP PROGRESS (Visible on steps 1..6)
        if (currentStep > 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentStep--
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF20090D))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BossFlame,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = BossFlame,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentStep == 6) "FINAL STEP • EXECUTION" else "QUESTION 0$currentStep OF 05",
                            color = BossFlame,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    // Spacer placeholder to balance back button
                    Box(modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentStep / 6f).coerceIn(0.1f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = BossFlame,
                    trackColor = Color(0xFF260A0F)
                )
            }
        }

        // STEP CONTENT WITH SMOOTH ANIMATED TRANSITIONS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width / 2 } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> -width / 2 } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width / 2 } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> width / 2 } + fadeOut())
                    }
                },
                label = "boss_mode_step_transition"
            ) { step ->
                when (step) {
                    0 -> BossWelcomeStep(
                        onStart = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentStep = 1
                        }
                    )
                    1 -> BossHabitsStep(
                        allHabits = allHabits,
                        selectedHabitIds = selectedHabitIds
                    )
                    2 -> BossTitleStep(
                        title = title,
                        onTitleChange = { title = it },
                        titlePresets = titlePresets
                    )
                    3 -> BossDurationStep(
                        selectedDuration = selectedDuration,
                        onSelectDuration = { selectedDuration = it },
                        durationOptions = durationOptions
                    )
                    4 -> BossIntensityStep(
                        selectedIntensity = selectedIntensity,
                        onSelectIntensity = { selectedIntensity = it },
                        intensityOptions = intensityOptions
                    )
                    5 -> BossPunishmentStep(
                        punishment = punishment,
                        onPunishmentChange = { punishment = it },
                        punishmentPresets = punishmentPresets
                    )
                    6 -> BossSealContractStep(
                        title = title.ifBlank { "Operation Iron Will" },
                        selectedDuration = selectedDuration,
                        selectedIntensity = selectedIntensity,
                        punishment = punishment,
                        habitsCount = selectedHabitIds.size,
                        onSealCompleted = {
                            onStartChallenge(
                                title.ifBlank { "Operation Iron Will" },
                                selectedHabitIds.toList(),
                                selectedDuration,
                                selectedIntensity,
                                punishment.trim()
                            )
                        }
                    )
                    else -> Box(modifier = Modifier.fillMaxSize())
                }
            }
        }

        // BOTTOM BAR FOR STEPS 1..5
        if (currentStep in 1..5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (canProceed) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentStep++
                        }
                    },
                    enabled = canProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("boss_step_continue_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BossFlame,
                        disabledContainerColor = Color(0xFF22080D)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (currentStep == 5) "Review & Seal Contract" else "Continue",
                            color = if (canProceed) Color.White else TextTertiary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (canProceed) Color.White else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 0: WELCOME & MANIFESTO INTRO
// -------------------------------------------------------------
@Composable
private fun BossWelcomeStep(onStart: () -> Unit) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "boss_fire_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Glowing Fire Emblem
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF24070C))
                .border(2.dp, BossFlame.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(BossFlameGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = BossFlame,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2B0A10))
                .border(1.dp, BossFlame.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = BossFlame,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "THE BEHAVIORAL BINDING PROTOCOL",
                    color = BossFlame,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome To\nBoss Mode",
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Casual habit trackers allow quiet quits. Boss Mode transforms consistency into an unbreakable high-stakes pact. Five brief questions will calibrate your rules of engagement.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3 Pillars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BossPillarCard(title = "LOCKED IN", subtitle = "Anti-quit chamber", modifier = Modifier.weight(1f))
            BossPillarCard(title = "ZERO ZEROES", subtitle = "Strict execution", modifier = Modifier.weight(1f))
            BossPillarCard(title = "STAKES", subtitle = "Real consequences", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("begin_boss_wizard_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BossFlame)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Begin Contract Calibration",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun BossPillarCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF190609))
            .border(1.dp, Color(0xFF330E14), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = BossFlame, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextTertiary, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

// -------------------------------------------------------------
// STEP 1: TARGET CORE HABITS
// -------------------------------------------------------------
@Composable
private fun BossHabitsStep(
    allHabits: List<Habit>,
    selectedHabitIds: MutableList<Long>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        BossQuestionHeader(
            tag = "Core Targets",
            title = "Which Habits Will You Forge In Fire?",
            description = "Select 1 or more core daily habits that MUST be executed every single day without excuse."
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (allHabits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF20090D))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active habits found. Please return to the Today tab and create at least 1 habit first!",
                    color = TextTertiary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Select All / Deselect All Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedHabitIds.size} of ${allHabits.size} selected",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (selectedHabitIds.size == allHabits.size) "Deselect All" else "Select All",
                    color = BossFlame,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (selectedHabitIds.size == allHabits.size) {
                            if (allHabits.isNotEmpty()) {
                                selectedHabitIds.clear()
                                selectedHabitIds.add(allHabits.first().id)
                            }
                        } else {
                            selectedHabitIds.clear()
                            selectedHabitIds.addAll(allHabits.map { it.id })
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                allHabits.forEach { habit ->
                    val isChecked = selectedHabitIds.contains(habit.id)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isChecked) Color(0xFF260A10) else Color(0xFF160608))
                            .border(
                                width = if (isChecked) 1.5.dp else 1.dp,
                                color = if (isChecked) BossFlame else DarkBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isChecked) {
                                    if (selectedHabitIds.size > 1) selectedHabitIds.remove(habit.id)
                                } else {
                                    selectedHabitIds.add(habit.id)
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) BossFlameGlow else Color(0xFF22080D)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (isChecked) BossFlame else TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = habit.name,
                                        color = if (isChecked) TextPrimary else TextSecondary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = habit.category,
                                        color = TextTertiary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isChecked) BossFlame else Color.Transparent)
                                    .border(1.dp, if (isChecked) BossFlame else Color(0xFF44121B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
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
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// STEP 2: CAMPAIGN TITLE / CODENAME
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BossTitleStep(
    title: String,
    onTitleChange: (String) -> Unit,
    titlePresets: List<String>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        BossQuestionHeader(
            tag = "Challenge Identity",
            title = "Name Your Challenge",
            description = "Assign a battle-tested codename that commands focus and accountability."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "CAMPAIGN CODENAME", color = BossFlame, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("boss_title_input"),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("e.g. Operation Iron Will", color = TextTertiary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BossFlame,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = Color(0xFF180609),
                unfocusedContainerColor = Color(0xFF180609),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "OR CHOOSE A PRESET CODENAME", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            titlePresets.forEach { preset ->
                val isSelected = title == preset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF2C0A10) else Color(0xFF1A070A))
                        .border(
                            1.dp,
                            if (isSelected) BossFlame else DarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onTitleChange(preset) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = preset,
                        color = if (isSelected) BossFlame else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// STEP 3: DURATION ARCHITECTURE
// -------------------------------------------------------------
@Composable
private fun BossDurationStep(
    selectedDuration: Int,
    onSelectDuration: (Int) -> Unit,
    durationOptions: List<Triple<Int, String, String>>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        BossQuestionHeader(
            tag = "Commitment Timeline",
            title = "How Long Will You Hold The Line?",
            description = "Select your contract duration. Behavioral science proves consistent streaks rewire permanent neural pathways."
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            durationOptions.forEach { (days, label, subtitle) ->
                val isSelected = selectedDuration == days
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF260A10) else Color(0xFF160608))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) BossFlame else DarkBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectDuration(days) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = label,
                                    color = if (isSelected) BossFlame else TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (days == 66) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BossFlameGlow)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("SCIENCE STANDARD", color = BossFlame, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = subtitle,
                                color = TextTertiary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = BossFlame,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// STEP 4: INTENSITY & STRICTNESS
// -------------------------------------------------------------
@Composable
private fun BossIntensityStep(
    selectedIntensity: String,
    onSelectIntensity: (String) -> Unit,
    intensityOptions: List<Pair<String, String>>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        BossQuestionHeader(
            tag = "Rules Of Engagement",
            title = "Select Strictness Level",
            description = "How severe are the terms of consistency when testing your discipline?"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            intensityOptions.forEach { (level, rule) ->
                val isSelected = selectedIntensity == level
                val flameCount = when (level) {
                    "Hard" -> 1
                    "Extreme" -> 2
                    else -> 3
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF260A10) else Color(0xFF160608))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) BossFlame else DarkBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectIntensity(level) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = level.uppercase(),
                                color = if (isSelected) BossFlame else TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Row {
                                repeat(flameCount) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (isSelected) BossFlame else TextTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = rule,
                            color = if (isSelected) TextSecondary else TextTertiary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// STEP 5: DECLARED STAKES & LOSS AVERSION
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BossPunishmentStep(
    punishment: String,
    onPunishmentChange: (String) -> Unit,
    punishmentPresets: List<String>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        BossQuestionHeader(
            tag = "Loss Aversion Stakes",
            title = "Declare Your Failure Consequence",
            description = "Behavioral economics proves humans fight twice as hard to avoid a painful loss. What price will you pay if you abandon this contract?"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "REAL-WORLD PENALTY PRESETS", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            punishmentPresets.forEach { preset ->
                val isSelected = punishment == preset
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF280B10) else Color(0xFF160608))
                        .border(
                            1.dp,
                            if (isSelected) BossFlame else DarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onPunishmentChange(preset) }
                        .padding(12.dp)
                ) {
                    Text(
                        text = preset,
                        color = if (isSelected) BossFlame else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(text = "OR CUSTOM PENALTY STATEMENT", color = BossFlame, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = punishment,
            onValueChange = onPunishmentChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("boss_punishment_input"),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Write your declared penalty...", color = TextTertiary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BossFlame,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = Color(0xFF180609),
                unfocusedContainerColor = Color(0xFF180609),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = false,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// STEP 6: BIOMETRIC SEAL & COVENANT SIGNATURE
// -------------------------------------------------------------
@Composable
private fun BossSealContractStep(
    title: String,
    selectedDuration: Int,
    selectedIntensity: String,
    punishment: String,
    habitsCount: Int,
    onSealCompleted: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }

    // Coroutine to animate hold progress over 2 seconds
    LaunchedEffect(isHolding) {
        if (isHolding) {
            val startTime = System.currentTimeMillis()
            val totalDurationMs = 1800f
            while (isHolding && holdProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                holdProgress = (elapsed / totalDurationMs).coerceIn(0f, 1f)
                if (holdProgress >= 1f) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSealCompleted()
                    break
                }
                delay(16)
            }
        } else {
            // Reset if released early
            holdProgress = 0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BossQuestionHeader(
            tag = "Execution Phase",
            title = "Review & Seal The Contract",
            description = "Carefully inspect your covenant. Once sealed, Boss Mode is locked and cannot be deactivated without facing the Quit Chamber."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Covenant Parchment Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1C070B))
                .border(1.5.dp, BossFlame.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "COVENANT DETAILS", color = BossFlame, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BossFlameGlow)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = selectedIntensity.uppercase(), color = BossFlame, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Campaign", color = TextTertiary, fontSize = 12.sp)
                    Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Duration", color = TextTertiary, fontSize = 12.sp)
                    Text("$selectedDuration Days", color = BossFlame, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Target Habits", color = TextTertiary, fontSize = 12.sp)
                    Text("$habitsCount Core Habits", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF260D12))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("DECLARED PENALTY UPON FORFEIT:", color = BossFlame, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("“$punishment”", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // HOLD TO SEAL BIOMETRIC BUTTON
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF22080D))
                .border(2.dp, if (isHolding) BossFlame else Color(0xFF44121B), CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isHolding = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            tryAwaitRelease()
                            isHolding = false
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Circular progress ring
            CircularProgressIndicator(
                progress = { holdProgress },
                modifier = Modifier.size(112.dp),
                color = BossFlame,
                strokeWidth = 4.dp,
                trackColor = Color.Transparent
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Hold To Seal",
                    tint = if (isHolding) BossFlame else Color.White,
                    modifier = Modifier.size(46.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHolding) "${(holdProgress * 100).toInt()}%" else "HOLD TO SEAL",
                    color = if (isHolding) BossFlame else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Press and hold fingerprint for 2 seconds to seal",
            color = TextTertiary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -------------------------------------------------------------
// QUESTION HEADER REUSABLE COMPONENT
// -------------------------------------------------------------
@Composable
private fun BossQuestionHeader(
    tag: String,
    title: String,
    description: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = tag.uppercase(),
            color = BossFlame,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
