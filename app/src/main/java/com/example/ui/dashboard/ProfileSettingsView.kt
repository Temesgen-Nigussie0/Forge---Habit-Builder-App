package com.example.ui.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.OrangeSubtle
import com.example.data.model.Habit
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsView(
    profile: UserProfile?,
    habitsCount: Int,
    tasksCount: Int,
    completedTasksCount: Int,
    onRetakeOnboarding: () -> Unit,
    onUpdatePhotos: (bannerUri: String?, avatarUri: String?) -> Unit,
    onUpdateDetails: (name: String, headline: String, bio: String, occupations: String?) -> Unit,
    onActivateFreeze: () -> Unit = {},
    onToggleComeback: (Boolean) -> Unit = {},
    allHabits: List<Habit> = emptyList(),
    onQuickAddHabit: (Habit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var pendingAvatarUri by remember { mutableStateOf<Uri?>(null) }

    // Real Photo Pickers using Android Photo Picker API
    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingAvatarUri = uri
        }
    }

    val bannerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdatePhotos(uri.toString(), profile?.avatarUri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Banner Image with Camera Change Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            if (!profile?.bannerUri.isNullOrBlank()) {
                AsyncImage(
                    model = profile?.bannerUri,
                    contentDescription = "Profile Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_banner_default),
                    contentDescription = "Default Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Dark gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x80000000), Color(0xFF070709))
                        )
                    )
            )

            // Edit Banner Button
            IconButton(
                onClick = {
                    bannerPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
                    .border(1.dp, Color(0x40FFFFFF), CircleShape)
                    .testTag("edit_banner_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Banner",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Overlapping Avatar + Info Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-42).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Circular Avatar (User sets their OWN picture!)
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .border(2.5.dp, ElectricOrange, CircleShape)
                        .clickable {
                            avatarPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("profile_avatar_view"),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profile?.avatarUri.isNullOrBlank()) {
                        AsyncImage(
                            model = profile?.avatarUri,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Empty/new state: prompts user to set their photo
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = ElectricOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Add Photo",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Avatar edit badge icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(ElectricOrange)
                            .border(2.dp, DarkSurface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Edit Bio Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { showEditProfileDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("edit_profile_details_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = ElectricOrange,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Bio",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Name & Headline (Refined typography, no garish bold white)
            Column(modifier = Modifier.offset(y = (-28).dp)) {
                Text(
                    text = profile?.userName?.ifBlank { "Alex" } ?: "Alex",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = profile?.headline ?: "CS Student & Visual Creator",
                    color = ElectricOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (!profile?.bio.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = profile.bio,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dynamic User Occupations & Craft Badges (from onboarding / profile edit)
                val userOccupations = remember(profile?.occupations) {
                    profile?.occupations?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
                        ?: listOf("Computer Science", "Client Freelancer", "Visual Creator")
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    userOccupations.forEach { occ ->
                        val icon = when {
                            occ.contains("code", true) || occ.contains("computer", true) || occ.contains("software", true) || occ.contains("dev", true) -> Icons.Default.Code
                            occ.contains("video", true) || occ.contains("photo", true) || occ.contains("visual", true) || occ.contains("creator", true) -> Icons.Default.Movie
                            occ.contains("student", true) || occ.contains("academic", true) || occ.contains("school", true) || occ.contains("science", true) -> Icons.Default.School
                            else -> Icons.Default.LocalFireDepartment
                        }
                        CreatorBadge(text = occ, icon = icon)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Matrix Cards (Equal proportions and heights!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPill(title = "HABITS", value = "$habitsCount active", color = ElectricOrange, modifier = Modifier.weight(1f))
                    StatPill(title = "TASKS DONE", value = "$completedTasksCount / $tasksCount", color = SuccessEmerald, modifier = Modifier.weight(1f))
                    StatPill(title = "STATUS", value = "High Velocity", color = CyanBlue, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Lifestyle Blueprint Section (Clean vertical layout with NO text wrapping issues!)
                Text(
                    text = "LIFESTYLE BLUEPRINT",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CleanBlueprintCard(
                        icon = Icons.Default.ElectricBolt,
                        label = "Primary Focus Area",
                        value = profile?.focusArea ?: "Physical Vitality"
                    )
                    CleanBlueprintCard(
                        icon = Icons.Default.Speed,
                        label = "Baseline Consistency",
                        value = profile?.currentConsistency ?: "Starting fresh"
                    )
                    CleanBlueprintCard(
                        icon = Icons.Default.Psychology,
                        label = "Streak Safeguard",
                        value = profile?.frictionHurdle ?: "Motivation drops"
                    )
                    CleanBlueprintCard(
                        icon = Icons.Default.SelfImprovement,
                        label = "Prime Time Window",
                        value = profile?.primeZone ?: "Early Morning (6:00 - 9:00 AM)"
                    )
                    CleanBlueprintCard(
                        icon = Icons.Default.Alarm,
                        label = "Daily Prompt",
                        value = profile?.briefingTime ?: "07:30 AM"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Streak Freeze Vault (Moved to Profile / Settings Page!)
                Text(
                    text = "STREAK PRESERVATION & RESILIENCE",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StreakFreezeProtocolCard(
                    userProfile = profile,
                    onActivateFreeze = onActivateFreeze,
                    onToggleComeback = onToggleComeback
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Habit Adds Library (20 curated habits)
                Text(
                    text = "QUICK HABIT LIBRARY (1-TAP ADD)",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap any habit below to instantly add it to your Today home dashboard:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                QuickHabitsLibraryGrid(
                    allHabits = allHabits,
                    onAddHabit = onQuickAddHabit
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Retake Onboarding
                Button(
                    onClick = onRetakeOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("retake_onboarding_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = ElectricOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Retake 10-Question Onboarding",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    // Edit Profile Details Dialog
    if (showEditProfileDialog) {
        EditProfileDetailsDialog(
            initialName = profile?.userName ?: "Alex",
            initialHeadline = profile?.headline ?: "Creator & Habit Architect",
            initialBio = profile?.bio ?: "",
            initialOccupations = profile?.occupations ?: "Computer Science, Client Freelancer, Visual Creator",
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, headline, bio, occupations ->
                onUpdateDetails(name, headline, bio, occupations)
                showEditProfileDialog = false
            }
        )
    }

    // Image Zoom / Tilt / Crop Adjustment Dialog
    pendingAvatarUri?.let { uri ->
        com.example.ui.profile.ImageAdjustmentDialog(
            imageUri = uri,
            onDismiss = { pendingAvatarUri = null },
            onConfirm = { adjustedUri ->
                onUpdatePhotos(profile?.bannerUri, adjustedUri)
                pendingAvatarUri = null
            }
        )
    }
}

@Composable
private fun CleanBlueprintCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricOrange,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CreatorBadge(text: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricOrange, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = text, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatPill(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(text = title, color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EditProfileDetailsDialog(
    initialName: String,
    initialHeadline: String,
    initialBio: String,
    initialOccupations: String,
    onDismiss: () -> Unit,
    onSave: (name: String, headline: String, bio: String, occupations: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var headline by remember { mutableStateOf(initialHeadline) }
    var bio by remember { mutableStateOf(initialBio) }
    var occupations by remember { mutableStateOf(initialOccupations) }

    Dialog(onDismissRequest = onDismiss) {
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
                Text(text = "Edit Profile & Bio", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "FULL NAME", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "HEADLINE / TITLE", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "OCCUPATIONS & CRAFTS (comma separated)", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = occupations,
                    onValueChange = { occupations = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "ABOUT / BIO", color = ElectricOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onSave(name, headline, bio, occupations) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricOrange)
                ) {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Curated 20 Quick Habits Library for 1-tap addition to Today page
 */
data class CuratedQuickHabit(
    val name: String,
    val category: String,
    val emoji: String,
    val iconName: String,
    val colorHex: String,
    val targetCount: Int,
    val unit: String,
    val timeOfDay: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickHabitsLibraryGrid(
    allHabits: List<Habit>,
    onAddHabit: (Habit) -> Unit
) {
    val curatedHabits = remember {
        listOf(
            CuratedQuickHabit("Drink 2L Water", "Health", "💧", "water", "#3B82F6", 8, "glasses", "All Day"),
            CuratedQuickHabit("10,000 Steps", "Fitness", "👟", "walk", "#10B981", 10000, "steps", "All Day"),
            CuratedQuickHabit("Morning Meditation", "Mindfulness", "🧘", "meditation", "#8B5CF6", 1, "session", "Morning"),
            CuratedQuickHabit("Read 20 Pages", "Learning", "📖", "book", "#F59E0B", 20, "pages", "Evening"),
            CuratedQuickHabit("Cold Shower", "Discipline", "🚿", "shower", "#06B6D4", 1, "session", "Morning"),
            CuratedQuickHabit("30-Min Workout", "Fitness", "🏋️", "workout", "#EF4444", 1, "session", "Morning"),
            CuratedQuickHabit("No Sugar Day", "Nutrition", "🥗", "nutrition", "#EC4899", 1, "day", "All Day"),
            CuratedQuickHabit("Daily Journaling", "Mindfulness", "✍️", "journal", "#6366F1", 1, "entry", "Evening"),
            CuratedQuickHabit("Sleep 8 Hours", "Recovery", "😴", "sleep", "#818CF8", 8, "hours", "Night"),
            CuratedQuickHabit("Deep Work Block", "Productivity", "⚡", "focus", "#F97316", 90, "minutes", "Morning"),
            CuratedQuickHabit("Code for 1 Hour", "Skill", "💻", "code", "#0284C7", 60, "minutes", "Afternoon"),
            CuratedQuickHabit("Stretching & Mobility", "Fitness", "🤸", "stretch", "#14B8A6", 15, "minutes", "Morning"),
            CuratedQuickHabit("Take Daily Vitamins", "Health", "💊", "vitamins", "#22C55E", 1, "dose", "Morning"),
            CuratedQuickHabit("Plan Tomorrow's Tasks", "Productivity", "📝", "plan", "#A855F7", 1, "review", "Evening"),
            CuratedQuickHabit("3 Gratitudes Practice", "Mindfulness", "🙏", "gratitude", "#F43F5E", 3, "items", "Morning"),
            CuratedQuickHabit("Learn Language 15m", "Learning", "🌍", "language", "#3B82F6", 15, "minutes", "Afternoon"),
            CuratedQuickHabit("20-Min Nature Walk", "Mindfulness", "🌲", "walk", "#10B981", 20, "minutes", "Afternoon"),
            CuratedQuickHabit("Limit Screen Time (<2h)", "Discipline", "📵", "digital", "#D97706", 1, "day", "Evening"),
            CuratedQuickHabit("Floss & Dental Care", "Health", "🦷", "floss", "#0EA5E9", 1, "time", "Night"),
            CuratedQuickHabit("Clean Workspace", "Environment", "🧹", "clean", "#64748B", 1, "session", "Evening")
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        curatedHabits.forEach { habitDef ->
            val isAlreadyAdded = allHabits.any { it.name.equals(habitDef.name, ignoreCase = true) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .border(
                        1.dp,
                        if (isAlreadyAdded) SuccessEmerald.copy(alpha = 0.5f) else DarkBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = !isAlreadyAdded) {
                        onAddHabit(
                            Habit(
                                name = habitDef.name,
                                category = habitDef.category,
                                iconName = habitDef.iconName,
                                colorHex = habitDef.colorHex,
                                targetCount = habitDef.targetCount,
                                unit = habitDef.unit,
                                timeOfDay = habitDef.timeOfDay
                            )
                        )
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("quick_habit_${habitDef.name.take(6).lowercase()}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = habitDef.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = habitDef.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = habitDef.category,
                                    color = ElectricOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = " • ${habitDef.timeOfDay}",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isAlreadyAdded) SuccessEmerald.copy(alpha = 0.15f)
                                else OrangeSubtle
                            )
                            .border(
                                1.dp,
                                if (isAlreadyAdded) SuccessEmerald.copy(alpha = 0.6f)
                                else ElectricOrange,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isAlreadyAdded) "Added ✓" else "+ Add",
                            color = if (isAlreadyAdded) SuccessEmerald else ElectricOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
