package com.example.arduinomonitor

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.arduinomonitor.ui.screens.DashboardScreen
import com.example.arduinomonitor.ui.screens.DeviceListScreen
import com.example.arduinomonitor.ui.screens.SettingsScreen
import com.example.arduinomonitor.ui.theme.ArduinoMonitorTheme
import com.example.arduinomonitor.viewmodel.AppViewModel

private enum class Screen { DEVICE_LIST, DASHBOARD, SETTINGS }

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val manager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(manager?.adapter) as T
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* نتیجه در recomposition بعدی بررسی می‌شود */ }

    private fun requiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasPermissions(): Boolean {
        return requiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions()) {
            permissionLauncher.launch(requiredPermissions())
        }

        setContent {
            ArduinoMonitorTheme {
                var screen by remember { mutableStateOf(Screen.DEVICE_LIST) }
                var permissionsGranted by remember { mutableStateOf(hasPermissions()) }
                val snackbarHostState = remember { SnackbarHostState() }
                val uiState by viewModel.uiState.collectAsState()

                // بررسی مجدد مجوزها هر بار که صفحه دوباره فوکوس می‌گیرد
                LaunchedEffect(Unit) {
                    permissionsGranted = hasPermissions()
                }

                LaunchedEffect(uiState.connectionState) {
                    if (uiState.connectionState.name == "CONNECTED") {
                        screen = Screen.DASHBOARD
                    }
                }

                LaunchedEffect(uiState.lastError) {
                    uiState.lastError?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearError()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    when (screen) {
                        Screen.DEVICE_LIST -> DeviceListScreen(
                            devices = if (permissionsGranted) viewModel.pairedDevices() else emptyList(),
                            bluetoothEnabled = viewModel.bluetoothEnabled,
                            onDeviceSelected = { device ->
                                viewModel.connectTo(device)
                            },
                            onRefresh = {
                                if (!hasPermissions()) {
                                    permissionLauncher.launch(requiredPermissions())
                                }
                                permissionsGranted = hasPermissions()
                            }
                        )
                        Screen.DASHBOARD -> DashboardScreen(
                            state = uiState,
                            onOpenSettings = { screen = Screen.SETTINGS },
                            onDisconnect = {
                                viewModel.disconnect()
                                screen = Screen.DEVICE_LIST
                            }
                        )
                        Screen.SETTINGS -> SettingsScreen(
                            state = uiState,
                            onBack = { screen = Screen.DASHBOARD },
                            onToggleLed = viewModel::toggleLed,
                            onToggleInstantSave = viewModel::toggleInstantSave,
                            onSendCustomCommand = viewModel::sendRawCommand,
                            onDisconnect = {
                                viewModel.disconnect()
                                screen = Screen.DEVICE_LIST
                            }
                        )
                    }
                }
            }
        }
    }
}
