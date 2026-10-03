package com.example.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.OrangeSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun OnboardingHeader(
    step: Int,
    totalSteps: Int = 10,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (step.coerceIn(0, totalSteps)).toFloat() / totalSteps,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 0) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("onboarding_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }

            if (step in 1..totalSteps) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Step $step",
                            color = ElectricOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " of $totalSteps",
                            color = TextTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.size(40.dp))
        }

        if (step in 1..totalSteps) {
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ElectricOrange,
                trackColor = DarkSurfaceElevated,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun OnboardingOptionCard(
    title: String,
    subtitle: String? = null,
    iconText: String? = null,
    badgeText: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.01f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "card_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ElectricOrange else DarkBorder,
        label = "border_color"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) DarkSurfaceCard else DarkSurfaceElevated,
        label = "bg_color"
    )

    // Generous ~40% rounded curves (26dp curvature)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(26.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(18.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconText != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) OrangeSubtle else DarkSurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) ElectricOrange.copy(alpha = 0.5f) else DarkBorderHighlight,
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iconText,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = if (isSelected) TextPrimary else TextPrimary.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OrangeSubtle)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = ElectricOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Selection indicator circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) ElectricOrange else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isSelected) ElectricOrange else DarkBorderHighlight,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
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

@Composable
fun OnboardingPrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "onboarding_next_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(28.dp), // 40% curvature pill
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0xFF1E1E26)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .then(
                    if (enabled) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ElectricOrange, NeonOrange)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                    } else {
                        // Inactive / greyed out state when nothing is selected
                        Modifier.background(Color(0xFF1E1E26), shape = RoundedCornerShape(28.dp))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    color = if (enabled) Color.White else TextTertiary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (enabled) Color.White else TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
