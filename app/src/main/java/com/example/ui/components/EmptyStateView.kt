package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class EmptyStateType {
    PRODUCTS,
    MOVEMENTS,
    ALERTS,
    CATEGORIES,
    SEARCH
}

@Composable
fun EmptyStateView(
    type: EmptyStateType,
    title: String,
    description: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val brand = LocalBrandColor.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(110.dp)) {
            val w = size.width
            val h = size.height
            val strokeDashed = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                cap = StrokeCap.Round
            )
            val strokeSolid = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

            when (type) {
                EmptyStateType.PRODUCTS -> {
                    // Dotted isometric open crate
                    val box = Path().apply {
                        moveTo(w * 0.5f, h * 0.15f)
                        lineTo(w * 0.85f, h * 0.35f)
                        lineTo(w * 0.85f, h * 0.75f)
                        lineTo(w * 0.5f, h * 0.95f)
                        lineTo(w * 0.15f, h * 0.75f)
                        lineTo(w * 0.15f, h * 0.35f)
                        close()
                        moveTo(w * 0.5f, h * 0.95f)
                        lineTo(w * 0.5f, h * 0.55f)
                        moveTo(w * 0.15f, h * 0.35f)
                        lineTo(w * 0.5f, h * 0.55f)
                        lineTo(w * 0.85f, h * 0.35f)
                    }
                    drawPath(box, color = Color(0xFF334155), style = strokeDashed)
                    // Floating mint sparkles
                    drawCircle(color = brand, radius = 4.dp.toPx(), center = Offset(w * 0.5f, h * 0.32f))
                    drawCircle(color = brand.copy(alpha = 0.5f), radius = 2.5f.dp.toPx(), center = Offset(w * 0.32f, h * 0.22f))
                    drawCircle(color = brand.copy(alpha = 0.5f), radius = 2.5f.dp.toPx(), center = Offset(w * 0.68f, h * 0.22f))
                }
                EmptyStateType.MOVEMENTS -> {
                    // Minimalist timeline track with empty circular nodes
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(w * 0.5f, h * 0.15f),
                        end = Offset(w * 0.5f, h * 0.85f),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                    drawCircle(color = Color(0xFF1E293B), radius = 12.dp.toPx(), center = Offset(w * 0.5f, h * 0.3f))
                    drawCircle(color = brand, radius = 5.dp.toPx(), center = Offset(w * 0.5f, h * 0.3f))
                    drawCircle(color = Color(0xFF1E293B), radius = 12.dp.toPx(), center = Offset(w * 0.5f, h * 0.7f))
                    drawCircle(color = Color(0xFF475569), radius = 5.dp.toPx(), center = Offset(w * 0.5f, h * 0.7f))
                }
                EmptyStateType.ALERTS -> {
                    // Peaceful quiet bell with gentle waves
                    val bell = Path().apply {
                        moveTo(w * 0.5f, h * 0.25f)
                        cubicTo(w * 0.35f, h * 0.25f, w * 0.30f, h * 0.55f, w * 0.25f, h * 0.75f)
                        lineTo(w * 0.75f, h * 0.75f)
                        cubicTo(w * 0.70f, h * 0.55f, w * 0.65f, h * 0.25f, w * 0.5f, h * 0.25f)
                        close()
                    }
                    drawPath(bell, color = Color(0xFF334155), style = strokeSolid)
                    drawCircle(color = brand, radius = 5.dp.toPx(), center = Offset(w * 0.5f, h * 0.85f))
                    // Subtle glowing pulse wave
                    drawCircle(color = brand.copy(alpha = 0.2f), radius = 35.dp.toPx(), center = Offset(w * 0.5f, h * 0.5f), style = strokeDashed)
                }
                EmptyStateType.CATEGORIES -> {
                    // Floating geometric layered cards
                    drawRoundRect(
                        color = Color(0xFF334155),
                        topLeft = Offset(w * 0.2f, h * 0.25f),
                        size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.25f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                        style = strokeDashed
                    )
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(w * 0.25f, h * 0.45f),
                        size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.25f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                        style = strokeSolid
                    )
                    drawCircle(color = brand, radius = 5.dp.toPx(), center = Offset(w * 0.5f, h * 0.575f))
                }
                EmptyStateType.SEARCH -> {
                    // Search glass with question hint
                    drawCircle(color = Color(0xFF334155), radius = 28.dp.toPx(), center = Offset(w * 0.45f, h * 0.45f), style = strokeDashed)
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(w * 0.65f, h * 0.65f),
                        end = Offset(w * 0.85f, h * 0.85f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(color = brand, radius = 4.dp.toPx(), center = Offset(w * 0.45f, h * 0.45f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 17.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if (!actionLabel.isNullOrBlank() && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brand,
                    contentColor = Color(0xFF0A0E1A)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("empty_state_action_button")
            ) {
                Text(
                    text = actionLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
