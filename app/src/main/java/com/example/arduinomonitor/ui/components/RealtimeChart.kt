package com.example.arduinomonitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.arduinomonitor.ui.theme.CardDark
import com.example.arduinomonitor.ui.theme.GridLine
import kotlin.math.max
import kotlin.math.min

/**
 * نمودار خطی زنده با پرشدگی گرادیانتی زیر منحنی، مناسب برای نمایش داده‌های حسگر
 * به‌صورت لحظه‌ای. کاملا با Canvas بومی Compose پیاده‌سازی شده (بدون کتابخانه جانبی).
 */
@Composable
fun RealtimeChart(
    data: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 180.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(CardDark)
    ) {
        val width = size.width
        val h = size.height
        val paddingPx = 12f

        // خطوط شبکه (grid)
        val gridRows = 4
        for (i in 0..gridRows) {
            val y = paddingPx + (h - paddingPx * 2) * i / gridRows
            drawLine(
                color = GridLine,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        if (data.size < 2) return@Canvas

        val minVal = data.min()
        val maxVal = data.max()
        val range = if (maxVal - minVal < 0.0001f) 1f else maxVal - minVal

        val stepX = width / (data.size - 1).coerceAtLeast(1)

        fun yFor(value: Float): Float {
            val normalized = (value - minVal) / range
            return h - paddingPx - normalized * (h - paddingPx * 2)
        }

        val linePath = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = yFor(value)
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(width, h)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.35f), lineColor.copy(alpha = 0.0f))
            )
        )

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
    }
}
