package com.example.ui.analytics

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BossChallenge
import com.example.data.model.HabitEntry
import com.example.ui.theme.BossCrimson
import com.example.ui.theme.BossFlame
import com.example.ui.theme.BossGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeGlow
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

data class MilestoneBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isUnlocked: Boolean,
    val unlockRequirement: String
)

@Composable
fun MilestoneBadgesSection(
    habitEntries: List<HabitEntry>,
    activeBossChallenge: BossChallenge?,
    modifier: Modifier = Modifier
) {
    var selectedBadge by remember { mutableStateOf<MilestoneBadge?>(null) }

    val completedCount = remember(habitEntries) { habitEntries.count { it.isCompleted } }

    val infiniteTransition = rememberInfiniteTransition(label = "badge_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    // Build badges with real unlock status
    val badges = listOf(
        MilestoneBadge(
            id = "ignition",
            title = "Ignition Spark",
            description = "Completed your very first daily habit action.",
            icon = Icons.Default.ElectricBolt,
            accentColor = SunsetAmber,
            isUnlocked = completedCount >= 1,
            unlockRequirement = "Log 1 habit completion"
        ),
        MilestoneBadge(
            id = "7_streak",
            title = "7-Day Vanguard",
            description = "Maintained an unbroken habit streak across a full week.",
            icon = Icons.Default.LocalFireDepartment,
            accentColor = ElectricOrange,
            isUnlocked = completedCount >= 7,
            unlockRequirement = "Log 7 habit completions"
        ),
        MilestoneBadge(
            id = "21_habit",
            title = "21-Day Protocol",
            description = "Rapid habit installation protocol successfully established.",
            icon = Icons.Default.Star,
            accentColor = GoldYellow,
            isUnlocked = completedCount >= 21,
            unlockRequirement = "Log 21 habit completions"
        ),
        MilestoneBadge(
            id = "boss_conqueror",
            title = "Boss Conqueror",
            description = "Accepted and active in a binding Boss Mode behavioral challenge.",
            icon = Icons.Default.EmojiEvents,
            accentColor = BossFlame,
            isUnlocked = activeBossChallenge != null && activeBossChallenge.isActive,
            unlockRequirement = "Initiate an active Boss Mode challenge"
        ),
        MilestoneBadge(
            id = "iron_will",
            title = "Iron Will 66",
            description = "Hit the Lally psychological standard for automatic habit wiring.",
            icon = Icons.Default.Shield,
            accentColor = SuccessEmerald,
            isUnlocked = completedCount >= 66,
            unlockRequirement = "Log 66 habit completions"
        ),
        MilestoneBadge(
            id = "century_club",
            title = "Century Master",
            description = "100 recorded habit victories. True personal mastery.",
            icon = Icons.Default.WorkspacePremium,
            accentColor = BossGold,
            isUnlocked = completedCount >= 100,
            unlockRequirement = "Log 100 habit completions"
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DYNAMIC MILESTONE BADGES",
                    color = ElectricOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Unlocked Trophies (${badges.count { it.isUnlocked }}/${badges.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Column Grid of Glowing Badges
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in 0 until (badges.size + 2) / 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        if (index < badges.size) {
                            val badge = badges[index]
                            BadgeCard(
                                badge = badge,
                                pulseScale = pulseScale,
                                onClick = { selectedBadge = badge },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Modal when user taps on a badge
    selectedBadge?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadge = null }
        )
    }
}

@Composable
private fun BadgeCard(
    badge: MilestoneBadge,
    pulseScale: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnlocked = badge.isUnlocked

    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isUnlocked) {
                    Brush.verticalGradient(
                        colors = listOf(badge.accentColor.copy(alpha = 0.18f), DarkSurfaceElevated)
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(DarkSurfaceElevated, DarkSurface)
                    )
                }
            )
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                color = if (isUnlocked) badge.accentColor.copy(alpha = 0.6f) else DarkBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing Icon Emblem
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .then(if (isUnlocked) Modifier.scale(pulseScale) else Modifier)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) badge.accentColor.copy(alpha = 0.25f)
                        else DarkSurfaceCard
                    )
                    .border(
                        1.dp,
                        if (isUnlocked) badge.accentColor else DarkBorderHighlight,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(
                        imageVector = badge.icon,
                        contentDescription = badge.title,
                        tint = badge.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                color = if (isUnlocked) TextPrimary else TextTertiary,
                fontSize = 11.sp,
                fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                color = if (isUnlocked) badge.accentColor else TextTertiary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun BadgeDetailDialog(
    badge: MilestoneBadge,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.5.dp, badge.accentColor.copy(alpha = 0.6f), RoundedCornerShape(26.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                // Large Glowing Emblem
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(badge.accentColor.copy(alpha = 0.2f))
                        .border(2.dp, badge.accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badge.icon,
                        contentDescription = badge.title,
                        tint = badge.accentColor,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = badge.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (badge.isUnlocked) badge.accentColor.copy(alpha = 0.2f) else DarkSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (badge.isUnlocked) "TROPHY UNLOCKED ✓" else "LOCKED CHALLENGE",
                        color = if (badge.isUnlocked) badge.accentColor else TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = badge.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "UNLOCK CRITERIA",
                            color = TextTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = badge.unlockRequirement,
                            color = ElectricOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = badge.accentColor)
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
