package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun StreakFreezeProtocolCard(
    userProfile: UserProfile?,
    onActivateFreeze: () -> Unit,
    onToggleComeback: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val freezesAvailable = userProfile?.streakFreezesAvailable ?: 1
    val isComebackActive = userProfile?.isComebackProtocolActive ?: false
    var showExplanation by remember { mutableStateOf(false) }

    val frostGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F1E2E), Color(0xFF162A40))
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(frostGradient)
            .border(1.dp, CyanBlue.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyanBlue.copy(alpha = 0.18f))
                        .border(1.dp, CyanBlue.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Streak Freeze",
                        tint = CyanBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "STREAK FREEZE VAULT",
                        color = CyanBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (freezesAvailable > 0) "$freezesAvailable Shield Available" else "0 Shields Available (Recharges 1st of month)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (freezesAvailable > 0) {
                Button(
                    onClick = onActivateFreeze,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Freeze Day", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "When intense university exams or client rendering marathons hit, your shield protects the unbroken streak without cheating.",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // "Never Miss Twice" Comeback Protocol Switcher
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0C1622))
                .border(1.dp, if (isComebackActive) ElectricOrange else Color(0x30FFFFFF), RoundedCornerShape(14.dp))
                .clickable { onToggleComeback(!isComebackActive) }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (isComebackActive) ElectricOrange else TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "“Never Miss Twice” Protocol",
                            color = if (isComebackActive) ElectricOrange else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isComebackActive) "ACTIVATED: Focus on 1 quick win today" else "Tap to toggle rapid momentum reboot",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isComebackActive) ElectricOrange else Color.Transparent)
                        .border(1.5.dp, if (isComebackActive) ElectricOrange else Color(0x40FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isComebackActive) {
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
