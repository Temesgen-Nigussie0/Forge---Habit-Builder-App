package com.example.ui.onboarding

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.OrangeGlow
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    if (state.isCompleted) {
        onFinished()
    }

    Scaffold(
        containerColor = ObsidianBlack,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Header with back button and progress
            if (state.currentStep in 1..10) {
                OnboardingHeader(
                    step = state.currentStep,
                    totalSteps = 10,
                    onBackClick = { viewModel.previousStep() }
                )
            } else if (state.currentStep == 12) {
                OnboardingHeader(
                    step = 10,
                    totalSteps = 10,
                    onBackClick = { viewModel.previousStep() }
                )
            }

            // Animated Screen Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width / 2 } + fadeIn()) togetherWith
                                    (slideOutHorizontally { width -> -width / 2 } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width / 2 } + fadeIn()) togetherWith
                                    (slideOutHorizontally { width -> width / 2 } + fadeOut())
                        }
                    },
                    label = "onboarding_step_transition"
                ) { step ->
                    when (step) {
                        0 -> WelcomeStep(onStart = { viewModel.nextStep() })
                        1 -> Question1Focus(state, viewModel)
                        2 -> Question2Consistency(state, viewModel)
                        3 -> Question3Hurdle(state, viewModel)
                        4 -> Question4HabitCount(state, viewModel)
                        5 -> Question5PrimeTime(state, viewModel)
                        6 -> Question6StarterHabits(state, viewModel)
                        7 -> Question7Motivation(state, viewModel)
                        8 -> Question8TimeCommitment(state, viewModel)
                        9 -> Question9BriefingTime(state, viewModel)
                        10 -> Question10PledgeWithFingerprint(state, viewModel)
                        11 -> Smooth3SecSynthesisStep(state)
                        12 -> Question0Identity(state, viewModel)
                        else -> Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }

            // Bottom Continue Bar (Only for steps 1 to 9; step 10 uses Biometric Fingerprint hold; step 11 is automatic; step 12 has its own complete button)
            if (state.currentStep in 1..9) {
                val canProceed = when (state.currentStep) {
                    1 -> state.focusArea.isNotBlank()
                    2 -> state.consistency.isNotBlank()
                    3 -> state.hurdle.isNotBlank()
                    4 -> state.habitCountLoad.isNotBlank()
                    5 -> state.primeTime.isNotBlank()
                    6 -> state.selectedHabits.isNotEmpty()
                    7 -> state.motivationStyle.isNotBlank()
                    8 -> state.timeCommitment.isNotBlank()
                    9 -> state.briefingTime.isNotBlank()
                    else -> false
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    OnboardingPrimaryButton(
                        text = "Continue",
                        enabled = canProceed,
                        onClick = { if (canProceed) viewModel.nextStep() },
                        testTag = "onboarding_continue_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onStart: () -> Unit) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "welcome_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
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
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            MovingHabitWaveAnimation(modifier = Modifier.fillMaxSize())

            Image(
                painter = painterResource(id = R.drawable.hero_habit_onboard),
                contentDescription = "Forge Habit Engine",
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .border(2.dp, ElectricOrange.copy(alpha = 0.6f), CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OrangeSubtle)
                .border(1.dp, ElectricOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = ElectricOrange,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PRECISION HABIT ARCHITECTURE",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Build Habits That\nActually Stick",
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Engineered for students, developers & creators. 10 brief lifestyle questions to calibrate your daily streak momentum.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // iOS Style Pure Arrow Floating Action Button
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonOrange, ElectricOrange)
                    )
                )
                .border(2.dp, Color(0x60FFFFFF), CircleShape)
                .clickable(onClick = onStart)
                .testTag("welcome_start_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap to Begin",
            color = TextTertiary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MovingHabitWaveAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "vector_wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2.3f

        for (i in 0..2) {
            val waveOffset = (i * PI / 3).toFloat()
            val path = Path()
            val points = 60

            for (step in 0..points) {
                val angle = (step.toFloat() / points) * 2f * PI.toFloat()
                val waveAmplitude = 10f * sin(angle * 3 + phase + waveOffset)
                val currentR = radius + waveAmplitude + (i * 8f)
                val x = centerX + currentR * cos(angle)
                val y = centerY + currentR * sin(angle)

                if (step == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            val alpha = when (i) {
                0 -> 0.6f
                1 -> 0.35f
                else -> 0.15f
            }

            drawPath(
                path = path,
                color = ElectricOrange.copy(alpha = alpha),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        for (p in 0..3) {
            val particleAngle = phase + (p * PI.toFloat() / 2f)
            val orbitR = radius + 14f * sin(particleAngle * 2)
            val px = centerX + orbitR * cos(particleAngle)
            val py = centerY + orbitR * sin(particleAngle)

            drawCircle(
                color = if (p % 2 == 0) SunsetAmber else ElectricOrange,
                radius = 4.dp.toPx(),
                center = Offset(px, py)
            )
        }
    }
}

@Composable
private fun QuestionHeader(
    tag: String,
    title: String,
    description: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = tag.uppercase(),
            color = ElectricOrange,
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
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Question0Identity(
    state: OnboardingState,
    viewModel: OnboardingViewModel
) {
    val scrollState = rememberScrollState()
    val defaultOccupations = listOf(
        "Computer Science",
        "Client Freelancer",
        "Visual Creator",
        "Software Dev",
        "UI/UX Design",
        "University Academics",
        "Founder / Business",
        "Health & Fitness"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        QuestionHeader(
            tag = "Final Calibration • Your Profile & Craft",
            title = "Tell Us What Defines You",
            description = "Customize your name, headline, and domain specializations for your dashboard."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name input
        Text(text = "YOUR NAME", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.userName,
            onValueChange = { viewModel.setUserName(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_name_input"),
            placeholder = { Text("e.g. Alex, Jordan", color = TextTertiary, fontSize = 14.sp) },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Headline input
        Text(text = "HEADLINE / CRAFT TITLE", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.headline,
            onValueChange = { viewModel.setHeadline(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_headline_input"),
            placeholder = { Text("e.g. CS Student & Visual Creator", color = TextTertiary, fontSize = 14.sp) },
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

        Spacer(modifier = Modifier.height(20.dp))

        // Occupations selection
        Text(text = "CHOOSE YOUR FIELDS & OCCUPATIONS", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Select what describes you or add your custom occupation:", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            defaultOccupations.forEach { occ ->
                val isSelected = state.selectedOccupations.contains(occ)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) OrangeSubtle else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) ElectricOrange else DarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.toggleOccupation(occ) }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ElectricOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = occ,
                            color = if (isSelected) ElectricOrange else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom occupation input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.customOccupation,
                onValueChange = { viewModel.setCustomOccupation(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add custom occupation (e.g. 3D Artist)", color = TextTertiary, fontSize = 13.sp) },
                shape = RoundedCornerShape(12.dp),
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

            Button(
                onClick = { viewModel.addCustomOccupation() },
                enabled = state.customOccupation.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricOrange,
                    disabledContainerColor = DarkSurfaceElevated
                )
            ) {
                Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.finalizeProfileAndComplete() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_complete_profile_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricOrange)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Enter Forge Dashboard",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
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

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun Question1Focus(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 01 of 10",
            title = "What is your primary focus area right now?",
            description = "Choose your core discipline objective:"
        )

        val options = listOf(
            Triple("Physical Vitality", "Energy, strength, movement & nutrition", "🏃‍♂️"),
            Triple("Mental Clarity", "Mindfulness, stress reduction & peace", "🧠"),
            Triple("Peak Productivity", "Deep work, focus & ruthless execution", "⚡"),
            Triple("Morning & Evening Flow", "Wake up energized & wind down smoothly", "🌅"),
            Triple("Continuous Learning", "Reading, new skills & personal growth", "📚")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = state.focusArea == title,
                    onClick = { viewModel.setFocusArea(title) },
                    testTag = "focus_${title.take(6).lowercase()}"
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question2Consistency(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 02 of 10",
            title = "How consistent are you with your habits today?",
            description = "Honesty helps calibrate the starting challenge:"
        )

        val options = listOf(
            Triple("Starting fresh", "Ready to build from scratch with a clean slate", "🌱"),
            Triple("Hot and cold", "I start strong for a week, then lose momentum", "🔄"),
            Triple("Fairly steady", "I have some solid basics, but want better tracking", "🎯"),
            Triple("High performer", "Looking to stack advanced micro-habits to excel", "🔥")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = state.consistency == title,
                    onClick = { viewModel.setConsistency(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question3Hurdle(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 03 of 10",
            title = "What usually breaks your streak?",
            description = "We design safeguards against your main friction point:"
        )

        val options = listOf(
            Triple("Not enough time", "Days get cluttered with unexpected urgent tasks", "⏳"),
            Triple("Motivation dips after days", "Excitement fades when initial novelty wears off", "📉"),
            Triple("Forgetting without reminders", "I intend to do it, but simply forget by night", "🔔"),
            Triple("Feeling overwhelmed", "Taking on too much at once and burning out", "🌊")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = state.hurdle == title,
                    onClick = { viewModel.setHurdle(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question4HabitCount(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 04 of 10",
            title = "How many daily habits feel realistic to start?",
            description = "Start small to establish unbreakable momentum:"
        )

        val options = listOf(
            Triple("1-2 Micro Habits", "Easy early wins to establish discipline", "✨"),
            Triple("3-4 Balanced Routine", "The gold standard for sustainable lifestyle shift", "⚖️"),
            Triple("5+ High Performance Stack", "Total daily transformation across all domains", "🚀")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    badgeText = if (title.contains("Balanced")) "RECOMMENDED" else null,
                    iconText = icon,
                    isSelected = state.habitCountLoad == title,
                    onClick = { viewModel.setHabitCountLoad(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question5PrimeTime(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 05 of 10",
            title = "When is your prime focus window?",
            description = "Align hardest habits with your natural energy rhythm:"
        )

        val options = listOf(
            Triple("Early Morning (6:00 - 9:00 AM)", "Win the morning before distractions start", "🌅"),
            Triple("Midday Power (11:00 AM - 2:00 PM)", "Re-energize and conquer the afternoon", "☀️"),
            Triple("Evening Wind-down (7:00 - 10:00 PM)", "Reflective quiet ritual before deep sleep", "🌙"),
            Triple("Flexible Throughout the Day", "Micro-actions interspersed as time allows", "⚡")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = state.primeTime == title,
                    onClick = { viewModel.setPrimeTime(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question6StarterHabits(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 06 of 10",
            title = "Choose your starter habits",
            description = "Select 2 to 4 habits for your routine:"
        )

        val habitPresets = listOf(
            Triple("Drink 2L Water", "Hydration baseline (8 glasses)", "💧"),
            Triple("Read 15 Pages", "Non-fiction or inspiring books", "📖"),
            Triple("10 Min Meditation", "Breathwork & calm focus", "🧘"),
            Triple("30 Min Exercise / Walk", "Cardio, weights, or brisk steps", "🏃"),
            Triple("Plan Top 3 Priorities", "Decide non-negotiables first", "📝"),
            Triple("No Screens 30m Before Bed", "Protect deep REM sleep", "📵"),
            Triple("Healthy Home Meal", "Whole ingredients & nourishment", "🥗"),
            Triple("5 Min Morning Sunlight", "Circadian melatonin reset", "☀️")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            habitPresets.forEach { (title, subtitle, icon) ->
                val isSelected = state.selectedHabits.contains(title)
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = isSelected,
                    onClick = { viewModel.toggleStarterHabit(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question7Motivation(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 07 of 10",
            title = "What kind of accountability drives you most?",
            description = "Customizes your dashboard visual feedback loops:"
        )

        val options = listOf(
            Triple("Streak Counter & Heatmaps", "Seeing uninterrupted chains gives me pure satisfaction", "🔥"),
            Triple("Completion % & Weekly Graphs", "Data-driven progress bars and consistency ratios", "📊"),
            Triple("Milestones & Achievement Badges", "Unlocking levels, trophies, and milestones", "🏆"),
            Triple("Minimalist Clean Checklist", "No clutter: just a crisp, peaceful daily checklist", "✅")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = state.motivationStyle == title,
                    onClick = { viewModel.setMotivationStyle(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Question8TimeCommitment(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 08 of 10",
            title = "How much daily time can you dedicate?",
            description = "We structure your habits so they never feel burdensome:"
        )

        val options = listOf(
            Triple("5 - 10 minutes", "Micro-actions with massive compound interest", "⚡"),
            Triple("15 - 30 minutes", "A solid daily foundation for lasting change", "⏱️"),
            Triple("45+ minutes", "Deep transformation and high-focus rituals", "🔥")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (title, subtitle, icon) ->
                OnboardingOptionCard(
                    title = title,
                    subtitle = subtitle,
                    iconText = icon,
                    isSelected = state.timeCommitment == title,
                    onClick = { viewModel.setTimeCommitment(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Question9BriefingTime(state: OnboardingState, viewModel: OnboardingViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        QuestionHeader(
            tag = "Question 09 of 10",
            title = "When should your daily briefing arrive?",
            description = "Choose your ideal moment for your daily check-in prompt:"
        )

        val times = listOf(
            Pair("06:30 AM", "Early Bird"),
            Pair("07:30 AM", "Morning Kickoff"),
            Pair("08:30 AM", "Workday Start"),
            Pair("12:30 PM", "Midday Reset"),
            Pair("08:00 PM", "Evening Check-in"),
            Pair("09:30 PM", "Bedtime Review")
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            times.forEach { (timeStr, label) ->
                OnboardingOptionCard(
                    title = timeStr,
                    subtitle = label,
                    iconText = "⏰",
                    isSelected = state.briefingTime == timeStr,
                    onClick = { viewModel.setBriefingTime(timeStr) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Question 10: User types their OWN name + Fingerprint Press & Hold (4 seconds with vibration!)
 */
@Composable
private fun Question10PledgeWithFingerprint(state: OnboardingState, viewModel: OnboardingViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isVerified by remember { mutableStateOf(false) }

    // Vibrator access
    val vibrator = remember {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Fingerprint hold timer: 4 seconds (40 ticks x 100ms)
    LaunchedEffect(isHolding) {
        if (isHolding && !isVerified && state.userName.isNotBlank()) {
            holdProgress = 0f
            for (tick in 1..40) {
                delay(100)
                if (!isHolding) break
                holdProgress = tick / 40f

                // Rhythmic gentle vibration feedback while pressing
                if (tick % 4 == 0) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(35)
                        }
                    } catch (e: Exception) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            }

            if (holdProgress >= 1f) {
                isVerified = true
                viewModel.setPledgeConfirmed(true)
                // Strong success vibration
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(120)
                    }
                } catch (e: Exception) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                delay(400)
            }
        } else if (!isHolding && !isVerified) {
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
        QuestionHeader(
            tag = "Question 10 of 10",
            title = "Seal your commitment",
            description = "Habits are an identity shift. Sign and biometrically seal your pact."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // The Pact Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, ElectricOrange.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = ElectricOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE FORGE PACT",
                        color = ElectricOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "“I commit to consistency over perfection. I will show up, even on hard days, aiming to be 1% better. A missed day is never missed twice.”",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // BIOMETRIC FINGERPRINT HOLD AREA (Bottom middle, Orange theme, 3-second hold with vibration!)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isVerified) "Pact Verified & Sealed ✓"
                else if (isHolding) "Keep holding to seal covenant..."
                else "Press and hold for 3 seconds to seal pact",
                color = if (isVerified) SuccessEmerald else ElectricOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        if (isVerified) SuccessEmerald.copy(alpha = 0.2f)
                        else OrangeSubtle
                    )
                    .border(
                        2.dp,
                        if (isVerified) SuccessEmerald
                        else ElectricOrange,
                        CircleShape
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHolding = true
                                tryAwaitRelease()
                                isHolding = false
                            }
                        )
                    }
                    .testTag("fingerprint_sensor_pad"),
                contentAlignment = Alignment.Center
            ) {
                // Circular progress ring during hold
                if (holdProgress > 0f && !isVerified) {
                    CircularProgressIndicator(
                        progress = { holdProgress },
                        modifier = Modifier.size(86.dp),
                        color = ElectricOrange,
                        trackColor = Color.Transparent,
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )
                }

                Icon(
                    imageVector = if (isVerified) Icons.Default.Check else Icons.Default.Fingerprint,
                    contentDescription = "Hold Fingerprint",
                    tint = if (isVerified) SuccessEmerald else ElectricOrange,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isVerified) "VERIFIED" else "BIOMETRIC SIGNATURE",
                color = if (isVerified) SuccessEmerald else TextTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

/**
 * Ultra-smooth 3-second kinetic orbital synthesis animation
 */
@Composable
private fun Smooth3SecSynthesisStep(state: OnboardingState) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbital_rings")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Multi-layered Kinetic Orbital Gyroscope
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val baseRadius = size.minDimension / 2.2f

                // Outer Ring
                drawCircle(
                    color = ElectricOrange.copy(alpha = 0.25f),
                    radius = baseRadius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Middle Ring with rotating nodes
                drawCircle(
                    color = SunsetAmber.copy(alpha = 0.35f),
                    radius = baseRadius * 0.75f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Rotating orbital nodes
                val rad = Math.toRadians(rotationAngle.toDouble())
                val nodeX = center.x + (baseRadius * 0.75f * cos(rad)).toFloat()
                val nodeY = center.y + (baseRadius * 0.75f * sin(rad)).toFloat()
                drawCircle(
                    color = ElectricOrange,
                    radius = 5.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )

                val node2X = center.x - (baseRadius * 0.75f * cos(rad)).toFloat()
                val node2Y = center.y - (baseRadius * 0.75f * sin(rad)).toFloat()
                drawCircle(
                    color = NeonOrange,
                    radius = 4.dp.toPx(),
                    center = Offset(node2X, node2Y)
                )
            }

            CircularProgressIndicator(
                progress = { state.analysisProgress },
                modifier = Modifier.size(110.dp),
                color = ElectricOrange,
                trackColor = DarkSurfaceElevated,
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ElectricOrange,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = "Assembling Your Routine...",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Calibrating ${state.focusArea.ifBlank { "Vitality" }} cues and streak momentum for ${state.userName.ifBlank { "You" }}.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Progress check items updating smoothly over 3 seconds
        val steps = listOf(
            "Synthesizing personal lifestyle inputs",
            "Configuring circadian peak energy zones",
            "Calibrating non-negotiable habit loops",
            "Activating Forge streak momentum"
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            steps.forEachIndexed { index, text ->
                val isDone = state.analysisProgress >= ((index + 1) * 0.25f)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isDone) SuccessEmerald else DarkBorderHighlight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        color = if (isDone) TextPrimary else TextTertiary,
                        fontSize = 13.sp,
                        fontWeight = if (isDone) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }
    }
}
