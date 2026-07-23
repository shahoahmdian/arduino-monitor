package com.example.arduinomonitor.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduinomonitor.bluetooth.BluetoothService
import com.example.arduinomonitor.data.ArduinoCommands
import com.example.arduinomonitor.data.ChannelStats
import com.example.arduinomonitor.data.ConnectionState
import com.example.arduinomonitor.data.DataAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val channelStats: List<ChannelStats> = emptyList(),
    val chartSeries: List<List<Float>> = emptyList(),
    val lastError: String? = null,
    val relayStates: List<Boolean> = List(4) { false },
    val instantSaveOn: Boolean = false,
    val samplesReceived: Long = 0L,
    val connectedDeviceName: String? = null
)

class AppViewModel(
    private val adapter: BluetoothAdapter?
) : ViewModel() {

    private val analyzer = DataAnalyzer(windowSize = 150)
    private val bluetoothService: BluetoothService? = adapter?.let { BluetoothService(it) }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val bluetoothAvailable: Boolean = adapter != null
    val bluetoothEnabled: Boolean get() = adapter?.isEnabled == true

    init {
        bluetoothService?.let { service ->
            viewModelScope.launch {
                service.connectionState.collect { state ->
                    _uiState.value = _uiState.value.copy(connectionState = state)
                }
            }
            viewModelScope.launch {
                service.incomingData.collect { sample ->
                    val stats = analyzer.addSample(sample)
                    val series = (0 until analyzer.channelCount()).map { analyzer.getSeries(it) }
                    _uiState.value = _uiState.value.copy(
                        channelStats = stats,
                        chartSeries = series,
                        samplesReceived = _uiState.value.samplesReceived + 1
                    )
                }
            }
            viewModelScope.launch {
                service.errorMessages.collect { message ->
                    _uiState.value = _uiState.value.copy(lastError = message)
                }
            }
        }
    }

    fun pairedDevices(): List<BluetoothDevice> = bluetoothService?.pairedDevices() ?: emptyList()

    fun connectTo(device: BluetoothDevice) {
        analyzer.reset()
        _uiState.value = _uiState.value.copy(connectedDeviceName = safeDeviceName(device), samplesReceived = 0)
        bluetoothService?.connect(device)
    }

    @Suppress("MissingPermission")
    private fun safeDeviceName(device: BluetoothDevice): String {
        return try {
            device.name ?: device.address
        } catch (e: SecurityException) {
            device.address
        }
    }

    fun disconnect() {
        bluetoothService?.disconnect()
        _uiState.value = _uiState.value.copy(connectedDeviceName = null)
    }

    fun toggleRelay(index: Int, turnOn: Boolean) {
        if (index !in ArduinoCommands.RELAY_ON.indices) return
        val command = if (turnOn) ArduinoCommands.RELAY_ON[index] else ArduinoCommands.RELAY_OFF[index]
        bluetoothService?.sendCommand(command)
        val updated = _uiState.value.relayStates.toMutableList()
        updated[index] = turnOn
        _uiState.value = _uiState.value.copy(relayStates = updated)
    }

    fun toggleInstantSave(turnOn: Boolean) {
        bluetoothService?.sendCommand(if (turnOn) ArduinoCommands.SAVE_ON else ArduinoCommands.SAVE_OFF)
        _uiState.value = _uiState.value.copy(instantSaveOn = turnOn)
    }

    fun sendRawCommand(command: String) {
        bluetoothService?.sendCommand(command)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(lastError = null)
    }
}
