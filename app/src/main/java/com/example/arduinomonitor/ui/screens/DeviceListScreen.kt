package com.example.arduinomonitor.ui.screens

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.arduinomonitor.ui.theme.*

@Composable
fun DeviceListScreen(
    devices: List<BluetoothDevice>,
    bluetoothEnabled: Boolean,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(20.dp)
    ) {
        Text("انتخاب دستگاه آردوینو", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            "دستگاه بلوتوث جفت‌شده با آردوینوی خود را از لیست زیر انتخاب کنید",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(20.dp))

        if (!bluetoothEnabled) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "بلوتوث دستگاه شما خاموش است. لطفا آن را روشن کرده و دوباره امتحان کنید.",
                    modifier = Modifier.padding(16.dp),
                    color = AccentAmber
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        if (devices.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.Memory, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(12.dp))
                Text("هیچ دستگاه جفت‌شده‌ای یافت نشد", color = TextSecondary)
                Text(
                    "ابتدا از تنظیمات بلوتوث گوشی، ماژول HC-05/HC-06 را جفت (Pair) کنید",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(devices) { device ->
                    DeviceRow(device = device, onClick = { onDeviceSelected(device) })
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
        ) {
            Text("به‌روزرسانی لیست دستگاه‌ها")
        }
    }
}

@Composable
private fun DeviceRow(device: BluetoothDevice, onClick: () -> Unit) {
    val name = try { device.name ?: "دستگاه ناشناس" } catch (e: SecurityException) { "دستگاه ناشناس" }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.BluetoothConnected, contentDescription = null, tint = AccentCyan)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(device.address, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
