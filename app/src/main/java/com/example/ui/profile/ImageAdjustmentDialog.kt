package com.example.ui.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.roundToInt

@Composable
fun ImageAdjustmentDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onConfirm: (adjustedUri: String) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, DarkBorderHighlight, RoundedCornerShape(28.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Adjust Profile Picture",
                        color = TextPrimary,
                        fontSize = 18.sp,
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

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pinch or use sliders to zoom, drag to position, or tilt.",
                    color = TextTertiary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Circular Crop Viewport with Gesture Support
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF06060A))
                        .border(3.dp, ElectricOrange, CircleShape)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotate ->
                                scale = (scale * zoom).coerceIn(0.8f, 3.5f)
                                rotation = (rotation + rotate).coerceIn(-45f, 45f)
                                offsetX += pan.x
                                offsetY += pan.y
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Crop Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                rotationZ = rotation
                                translationX = offsetX
                                translationY = offsetY
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Zoom Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = null,
                        tint = ElectricOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zoom ${(scale * 100).toInt()}%",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.width(75.dp)
                    )
                    Slider(
                        value = scale,
                        onValueChange = { scale = it },
                        valueRange = 0.8f..3.0f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricOrange,
                            activeTrackColor = ElectricOrange,
                            inactiveTrackColor = DarkSurfaceElevated
                        )
                    )
                }

                // Tilt / Rotate Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = null,
                        tint = ElectricOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tilt ${rotation.toInt()}°",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.width(75.dp)
                    )
                    Slider(
                        value = rotation,
                        onValueChange = { rotation = it },
                        valueRange = -45f..45f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricOrange,
                            activeTrackColor = ElectricOrange,
                            inactiveTrackColor = DarkSurfaceElevated
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            scale = 1.0f
                            rotation = 0f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset", color = TextSecondary, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            // Save image URI
                            onConfirm(imageUri.toString())
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("confirm_crop_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricOrange)
                    ) {
                        Text("Confirm & Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
