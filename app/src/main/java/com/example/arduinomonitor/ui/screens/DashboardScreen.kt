package com.example.arduinomonitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.arduinomonitor.data.ChannelStats
import com.example.arduinomonitor.data.ConnectionState
import com.example.arduinomonitor.ui.components.RealtimeChart
import com.example.arduinomonitor.ui.theme.*
import com.example.arduinomonitor.viewmodel.UiState

private val channelColors = listOf(AccentCyan, AccentPurple, AccentAmber, SuccessGreen, DangerRed)
private val channelLabels = listOf("کانال ۱", "کانال ۲", "کانال ۳", "کانال ۴", "کانال ۵")

@Composable
fun DashboardScreen(
    state: UiState,
    onOpenSettings: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        TopBar(state = state, onOpenSettings = onOpenSettings, onDisconnect = onDisconnect)

        if (state.chartSeries.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.ShowChart, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(12.dp))
                Text("در انتظار دریافت داده از آردوینو...", color = TextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.chartSeries.size) { index ->
                    val color = channelColors[index % channelColors.size]
                    val label = channelLabels.getOrElse(index) { "کانال ${index + 1}" }
                    val stats = state.channelStats.getOrNull(index) ?: ChannelStats()
                    ChannelCard(label = label, color = color, series = state.chartSeries[index], stats = stats)
                }
            }
        }
    }
}

@Composable
private fun TopBar(state: UiState, onOpenSettings: () -> Unit, onDisconnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (dotColor, statusText) = when (state.connectionState) {
            ConnectionState.CONNECTED -> SuccessGreen to (state.connectedDeviceName ?: "متصل")
            ConnectionState.CONNECTING -> AccentAmber to "در حال اتصال..."
            ConnectionState.FAILED -> DangerRed to "اتصال ناموفق"
            ConnectionState.DISCONNECTED -> TextSecondary to "قطع شده"
        }
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(statusText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("نمونه‌های دریافتی: ${state.samplesReceived}", style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "تنظیمات", tint = TextPrimary)
        }
    }
}

@Composable
private fun ChannelCard(label: String, color: androidx.compose.ui.graphics.Color, series: List<Float>, stats: ChannelStats) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.titleLarge)
                Text(
                    "%.2f".format(stats.last),
                    style = MaterialTheme.typography.headlineMedium,
                    color = color
                )
            }
            Spacer(Modifier.height(10.dp))
            RealtimeChart(data = series, lineColor = color)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("حداقل", "%.2f".format(stats.min))
                StatItem("حداکثر", "%.2f".format(stats.max))
                StatItem("میانگین", "%.2f".format(stats.average))
                StatItem("انحراف معیار", "%.2f".format(stats.stdDev))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
