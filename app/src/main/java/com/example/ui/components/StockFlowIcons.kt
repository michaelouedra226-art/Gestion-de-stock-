package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalBrandColor
import com.example.ui.theme.RedDanger

/**
 * Custom geometric vector icon set for StockFlow.
 * Clean 1.75dp stroke outlines with signature brand accent dot.
 */
@Composable
fun IconDashboard(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        // Asymmetric grid 2-1-1
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = Size(w * 0.35f, h * 0.45f),
            cornerRadius = CornerRadius(w * 0.08f),
            style = stroke
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.53f, h * 0.12f),
            size = Size(w * 0.35f, h * 0.28f),
            cornerRadius = CornerRadius(w * 0.08f),
            style = stroke
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.63f),
            size = Size(w * 0.35f, h * 0.25f),
            cornerRadius = CornerRadius(w * 0.08f),
            style = stroke
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.53f, h * 0.46f),
            size = Size(w * 0.35f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.08f),
            style = stroke
        )
        // Signature brand accent dot
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.78f, h * 0.26f))
    }
}

@Composable
fun IconProducts(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        // Open geometric isometric parallelepiped box
        val boxPath = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.32f)
            lineTo(w * 0.88f, h * 0.72f)
            lineTo(w * 0.5f, h * 0.90f)
            lineTo(w * 0.12f, h * 0.72f)
            lineTo(w * 0.12f, h * 0.32f)
            close()
            moveTo(w * 0.5f, h * 0.90f)
            lineTo(w * 0.5f, h * 0.50f)
            moveTo(w * 0.12f, h * 0.32f)
            lineTo(w * 0.5f, h * 0.50f)
            lineTo(w * 0.88f, h * 0.32f)
        }
        drawPath(boxPath, color = tint, style = stroke)
        // Accent dot on apex node
        drawCircle(color = accent, radius = w * 0.075f, center = Offset(w * 0.5f, h * 0.12f))
    }
}

@Composable
fun IconMovements(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        // Double crossing dynamic arrows
        val arrow1 = Path().apply {
            moveTo(w * 0.2f, h * 0.35f)
            lineTo(w * 0.75f, h * 0.35f)
            lineTo(w * 0.6f, h * 0.2f)
            moveTo(w * 0.75f, h * 0.35f)
            lineTo(w * 0.6f, h * 0.5f)
        }
        val arrow2 = Path().apply {
            moveTo(w * 0.8f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.4f, h * 0.5f)
            moveTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.4f, h * 0.8f)
        }
        drawPath(arrow1, color = tint, style = stroke)
        drawPath(arrow2, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.78f, h * 0.35f))
    }
}

@Composable
fun IconAlerts(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = Color(0xFFF59E0B)
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        // Bell outline
        val bell = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            cubicTo(w * 0.3f, h * 0.15f, w * 0.25f, h * 0.45f, w * 0.2f, h * 0.68f)
            lineTo(w * 0.8f, h * 0.68f)
            cubicTo(w * 0.75f, h * 0.45f, w * 0.7f, h * 0.15f, w * 0.5f, h * 0.15f)
            close()
            moveTo(w * 0.42f, h * 0.76f)
            cubicTo(w * 0.42f, h * 0.86f, w * 0.58f, h * 0.86f, w * 0.58f, h * 0.76f)
        }
        drawPath(bell, color = tint, style = stroke)
        // Accent alert dot emitting signal
        drawCircle(color = accent, radius = w * 0.08f, center = Offset(w * 0.75f, h * 0.22f))
    }
}

@Composable
fun IconCategories(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        // 3 stacked geometric tiles
        val tile1 = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.30f)
            lineTo(w * 0.5f, h * 0.45f)
            lineTo(w * 0.15f, h * 0.30f)
            close()
        }
        val tile2 = Path().apply {
            moveTo(w * 0.15f, h * 0.50f)
            lineTo(w * 0.5f, h * 0.65f)
            lineTo(w * 0.85f, h * 0.50f)
        }
        val tile3 = Path().apply {
            moveTo(w * 0.15f, h * 0.70f)
            lineTo(w * 0.5f, h * 0.85f)
            lineTo(w * 0.85f, h * 0.70f)
        }
        drawPath(tile1, color = tint, style = stroke)
        drawPath(tile2, color = tint, style = stroke)
        drawPath(tile3, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.85f, h * 0.30f))
    }
}

@Composable
fun IconSettings(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = tint, radius = w * 0.30f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
        // 6 subtle teeth
        for (i in 0 until 6) {
            val angle = i * (Math.PI / 3)
            val startX = (w * 0.5f + Math.cos(angle) * (w * 0.30f)).toFloat()
            val startY = (h * 0.5f + Math.sin(angle) * (h * 0.30f)).toFloat()
            val endX = (w * 0.5f + Math.cos(angle) * (w * 0.42f)).toFloat()
            val endY = (h * 0.5f + Math.sin(angle) * (h * 0.42f)).toFloat()
            drawLine(color = tint, start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        }
        drawCircle(color = accent, radius = w * 0.09f, center = Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
fun IconSearch(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = tint, radius = w * 0.28f, center = Offset(w * 0.42f, h * 0.42f), style = stroke)
        drawLine(
            color = tint,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.85f, h * 0.85f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(color = accent, radius = w * 0.065f, center = Offset(w * 0.42f, h * 0.42f))
    }
}

@Composable
fun IconAdd(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        val w = this.size.width
        val h = this.size.height

        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.22f), end = Offset(w * 0.5f, h * 0.78f), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color = tint, start = Offset(w * 0.22f, h * 0.5f), end = Offset(w * 0.78f, h * 0.5f), strokeWidth = stroke.width, cap = stroke.cap)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.72f, h * 0.28f))
    }
}

@Composable
fun IconStockIn(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF10B981),
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
        val arrow = Path().apply {
            moveTo(w * 0.5f, h * 0.30f)
            lineTo(w * 0.5f, h * 0.70f)
            moveTo(w * 0.35f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.70f)
            lineTo(w * 0.65f, h * 0.55f)
        }
        drawPath(arrow, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.30f))
    }
}

@Composable
fun IconStockOut(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFFEF4444),
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
        val arrow = Path().apply {
            moveTo(w * 0.5f, h * 0.70f)
            lineTo(w * 0.5f, h * 0.30f)
            moveTo(w * 0.35f, h * 0.45f)
            lineTo(w * 0.5f, h * 0.30f)
            lineTo(w * 0.65f, h * 0.45f)
        }
        drawPath(arrow, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.30f))
    }
}

@Composable
fun IconBuilding(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        val building = Path().apply {
            moveTo(w * 0.2f, h * 0.85f)
            lineTo(w * 0.2f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.45f)
            lineTo(w * 0.85f, h * 0.45f)
            lineTo(w * 0.85f, h * 0.85f)
            close()
        }
        drawPath(building, color = tint, style = stroke)
        // Windows
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.35f, h * 0.40f))
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.45f, h * 0.40f))
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.35f, h * 0.55f))
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.45f, h * 0.55f))
        // Brand accent dot on tower top
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.4f, h * 0.25f))
    }
}

@Composable
fun IconPalette(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = tint, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
        drawCircle(color = Color(0xFFFB7185), radius = w * 0.06f, center = Offset(w * 0.38f, h * 0.35f))
        drawCircle(color = Color(0xFFFBBF24), radius = w * 0.06f, center = Offset(w * 0.62f, h * 0.35f))
        drawCircle(color = Color(0xFF38BDF8), radius = w * 0.06f, center = Offset(w * 0.35f, h * 0.60f))
        drawCircle(color = accent, radius = w * 0.08f, center = Offset(w * 0.62f, h * 0.60f))
    }
}

@Composable
fun IconTrending(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        val trend = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.40f, h * 0.55f)
            lineTo(w * 0.60f, h * 0.65f)
            lineTo(w * 0.85f, h * 0.30f)
            moveTo(w * 0.65f, h * 0.30f)
            lineTo(w * 0.85f, h * 0.30f)
            lineTo(w * 0.85f, h * 0.50f)
        }
        drawPath(trend, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.85f, h * 0.30f))
    }
}

@Composable
fun IconExport(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        val tray = Path().apply {
            moveTo(w * 0.20f, h * 0.55f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.80f, h * 0.80f)
            lineTo(w * 0.80f, h * 0.55f)
            moveTo(w * 0.50f, h * 0.65f)
            lineTo(w * 0.50f, h * 0.22f)
            moveTo(w * 0.35f, h * 0.37f)
            lineTo(w * 0.50f, h * 0.22f)
            lineTo(w * 0.65f, h * 0.37f)
        }
        drawPath(tray, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.50f, h * 0.22f))
    }
}

@Composable
fun IconEdit(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = LocalBrandColor.current
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        val pen = Path().apply {
            moveTo(w * 0.25f, h * 0.75f)
            lineTo(w * 0.32f, h * 0.55f)
            lineTo(w * 0.68f, h * 0.19f)
            lineTo(w * 0.81f, h * 0.32f)
            lineTo(w * 0.45f, h * 0.68f)
            close()
        }
        drawPath(pen, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.065f, center = Offset(w * 0.75f, h * 0.25f))
    }
}

@Composable
fun IconDelete(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = RedDanger,
    accent: Color = RedDanger
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        drawLine(color = tint, start = Offset(w * 0.20f, h * 0.30f), end = Offset(w * 0.80f, h * 0.30f), strokeWidth = stroke.width, cap = stroke.cap)
        val bin = Path().apply {
            moveTo(w * 0.30f, h * 0.30f)
            lineTo(w * 0.34f, h * 0.80f)
            lineTo(w * 0.66f, h * 0.80f)
            lineTo(w * 0.70f, h * 0.30f)
        }
        drawPath(bin, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.065f, center = Offset(w * 0.50f, h * 0.20f))
    }
}

@Composable
fun IconLogout(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = RedDanger
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.75f.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        val door = Path().apply {
            moveTo(w * 0.45f, h * 0.20f)
            lineTo(w * 0.20f, h * 0.20f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.45f, h * 0.80f)
            moveTo(w * 0.40f, h * 0.50f)
            lineTo(w * 0.80f, h * 0.50f)
            moveTo(w * 0.65f, h * 0.35f)
            lineTo(w * 0.80f, h * 0.50f)
            lineTo(w * 0.65f, h * 0.65f)
        }
        drawPath(door, color = tint, style = stroke)
        drawCircle(color = accent, radius = w * 0.07f, center = Offset(w * 0.80f, h * 0.50f))
    }
}

@Composable
fun IconCheck(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF10B981)
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.20f, h * 0.50f)
            lineTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.80f, h * 0.28f)
        }
        drawPath(path, color = tint, style = stroke)
    }
}

@Composable
fun IconClose(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        val w = this.size.width
        val h = this.size.height

        drawLine(color = tint, start = Offset(w * 0.28f, h * 0.28f), end = Offset(w * 0.72f, h * 0.72f), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color = tint, start = Offset(w * 0.72f, h * 0.28f), end = Offset(w * 0.28f, h * 0.72f), strokeWidth = stroke.width, cap = stroke.cap)
    }
}
