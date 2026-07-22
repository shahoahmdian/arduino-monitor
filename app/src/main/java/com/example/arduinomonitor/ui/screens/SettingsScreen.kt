package com.example.arduinomonitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.arduinomonitor.ui.theme.*
import com.example.arduinomonitor.viewmodel.UiState

@Composable
fun SettingsScreen(
    state: UiState,
    onBack: () -> Unit,
    onToggleLed: (Boolean) -> Unit,
    onToggleInstantSave: (Boolean) -> Unit,
    onSendCustomCommand: (String) -> Unit,
    onDisconnect: () -> Unit
) {
    var customCommand by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "بازگشت", tint = TextPrimary)
            }
            Text("تنظیمات", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(Modifier.height(16.dp))

        SettingCard(
            icon = Icons.Filled.Lightbulb,
            iconTint = AccentAmber,
            title = "کنترل LED / رله آردوینو",
            description = "روشن یا خاموش کردن خروجی متصل به آردوینو",
            checked = state.ledOn,
            onCheckedChange = onToggleLed
        )

        Spacer(Modifier.height(12.dp))

        SettingCard(
            icon = Icons.Filled.Save,
            iconTint = AccentCyan,
            title = "ذخیره‌سازی لحظه‌ای داده",
            description = "فعال یا غیرفعال کردن ذخیره داده‌ها روی حافظه آردوینو (مثلا SD Card)",
            checked = state.instantSaveOn,
            onCheckedChange = onToggleInstantSave
        )

        Spacer(Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ارسال دستور دلخواه", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "برای هر فرمان سفارشی که در کد آردوینو تعریف کرده‌اید، آن را اینجا بنویسید",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = customCommand,
                    onValueChange = { customCommand = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("مثلا: CALIBRATE") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (customCommand.isNotBlank()) {
                            onSendCustomCommand(customCommand.trim())
                            customCommand = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text("ارسال به آردوینو")
                }
            }
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onDisconnect,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
        ) {
            Icon(Icons.Filled.BluetoothDisabled, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("قطع اتصال بلوتوث")
        }
    }
}

@Composable
private fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = AccentCyan)
            )
        }
    }
}
