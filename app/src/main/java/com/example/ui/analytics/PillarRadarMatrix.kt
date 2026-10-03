package com.example.ui.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.HabitEntry
import com.example.data.model.TaskItem
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PillarRadarMatrix(
    habits: List<Habit>,
    entries: List<HabitEntry>,
    tasks: List<TaskItem>,
    modifier: Modifier = Modifier
) {
    // 4 Core Student/Developer/Creator Pillars:
    // 1. Software Engineering
    // 2. Video & Photo Editing
    // 3. University Academics
    // 4. Physical Vitality & Daily Habits

    val devTasks = tasks.filter { it.category == "Software Dev" }
    val devScore = if (devTasks.isEmpty()) 0.45f else (devTasks.count { it.isCompleted }.toFloat() / devTasks.size).coerceIn(0.2f, 1f)

    val videoTasks = tasks.filter { it.category == "Video & Photo" }
    val videoScore = if (videoTasks.isEmpty()) 0.4f else (videoTasks.count { it.isCompleted }.toFloat() / videoTasks.size).coerceIn(0.2f, 1f)

    val uniTasks = tasks.filter { it.category == "University" }
    val uniScore = if (uniTasks.isEmpty()) 0.5f else (uniTasks.count { it.isCompleted }.toFloat() / uniTasks.size).coerceIn(0.2f, 1f)

    val completedEntries = entries.filter { it.isCompleted }
    val vitalityScore = if (habits.isEmpty()) 0.5f else ((completedEntries.size.toFloat() / (habits.size * 7).coerceAtLeast(1))).coerceIn(0.25f, 1f)

    val scores = listOf(devScore, videoScore, uniScore, vitalityScore)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(scores) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, tween(1000))
    }

    val balancePercentage = ((scores.average() * 100).toInt()).coerceIn(40, 98)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PILLAR BALANCE MATRIX",
                        color = ElectricOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Polymath Equilibrium ($balancePercentage%)",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricOrange.copy(alpha = 0.15f))
                        .border(1.dp, ElectricOrange.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (balancePercentage >= 75) "OPTIMAL" else "CALIBRATING",
                        color = ElectricOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spider Radar Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val maxRadius = size.minDimension / 2.3f

                    // 4 Concentric Diamond / Polygon Web Rings
                    val ringFractions = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                    ringFractions.forEach { frac ->
                        val r = maxRadius * frac
                        val path = Path()
                        for (i in 0..3) {
                            val angle = (i * PI / 2) - (PI / 2)
                            val x = center.x + (r * cos(angle)).toFloat()
                            val y = center.y + (r * sin(angle)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(
                            path = path,
                            color = Color(0x30FFFFFF),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // 4 Radial Axis Rays
                    for (i in 0..3) {
                        val angle = (i * PI / 2) - (PI / 2)
                        val endX = center.x + (maxRadius * cos(angle)).toFloat()
                        val endY = center.y + (maxRadius * sin(angle)).toFloat()
                        drawLine(
                            color = Color(0x40FFFFFF),
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Dynamic User Polygonal Footprint
                    val anim = animatedProgress.value
                    val userPath = Path()
                    val vertexPoints = mutableListOf<Offset>()

                    for (i in 0..3) {
                        val angle = (i * PI / 2) - (PI / 2)
                        val scoreRadius = maxRadius * scores[i] * anim
                        val x = center.x + (scoreRadius * cos(angle)).toFloat()
                        val y = center.y + (scoreRadius * sin(angle)).toFloat()
                        val pt = Offset(x, y)
                        vertexPoints.add(pt)
                        if (i == 0) userPath.moveTo(x, y) else userPath.lineTo(x, y)
                    }
                    userPath.close()

                    // Filled Polygon with Orange Glow
                    drawPath(
                        path = userPath,
                        brush = Brush.radialGradient(
                            colors = listOf(ElectricOrange.copy(alpha = 0.55f), SunsetAmber.copy(alpha = 0.25f)),
                            center = center,
                            radius = maxRadius
                        )
                    )

                    // Polygon Border
                    drawPath(
                        path = userPath,
                        color = ElectricOrange,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Glowing Vertex Nodes
                    vertexPoints.forEach { pt ->
                        drawCircle(color = NeonOrange, radius = 4.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Pillar Labels with indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PillarLegendItem("Dev Sprints", Icons.Default.Code, ElectricOrange, "${(devScore * 100).toInt()}%")
                PillarLegendItem("Video/Media", Icons.Default.Movie, SunsetAmber, "${(videoScore * 100).toInt()}%")
                PillarLegendItem("Academics", Icons.Default.School, CyanBlue, "${(uniScore * 100).toInt()}%")
                PillarLegendItem("Vitality", Icons.Default.LocalFireDepartment, SuccessEmerald, "${(vitalityScore * 100).toInt()}%")
            }
        }
    }
}

@Composable
private fun PillarLegendItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
