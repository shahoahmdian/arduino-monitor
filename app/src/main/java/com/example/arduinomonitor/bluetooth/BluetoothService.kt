package com.example.arduinomonitor.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.example.arduinomonitor.data.ConnectionState
import com.example.arduinomonitor.data.SensorSample
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.UUID

/**
 * مدیریت اتصال کلاسیک بلوتوث (SPP) به ماژول‌های HC-05 / HC-06 روی آردوینو.
 * UUID زیر، UUID استاندارد سرویس Serial Port Profile است که تقریبا تمام ماژول‌های
 * بلوتوث ارزان‌قیمت آن را پیاده‌سازی می‌کنند.
 */
class BluetoothService(
    private val adapter: BluetoothAdapter
) {
    companion object {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingData = MutableSharedFlow<SensorSample>(extraBufferCapacity = 64)
    val incomingData = _incomingData.asSharedFlow()

    private val _errorMessages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val errorMessages = _errorMessages.asSharedFlow()

    @SuppressLint("MissingPermission")
    fun pairedDevices(): List<BluetoothDevice> {
        return try {
            adapter.bondedDevices?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        _connectionState.value = ConnectionState.CONNECTING
        scope.launch {
            try {
                adapter.cancelDiscovery()
                val sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
                sock.connect()
                socket = sock
                outputStream = sock.outputStream
                _connectionState.value = ConnectionState.CONNECTED
                listenForData(sock)
            } catch (e: IOException) {
                _connectionState.value = ConnectionState.FAILED
                _errorMessages.emit("اتصال ناموفق بود: ${e.message}")
                closeQuietly()
            }
        }
    }

    private fun listenForData(sock: BluetoothSocket) {
        try {
            val reader = BufferedReader(InputStreamReader(sock.inputStream))
            while (_connectionState.value == ConnectionState.CONNECTED) {
                val line = reader.readLine() ?: break
                parseLine(line)?.let { sample ->
                    _incomingData.tryEmit(sample)
                }
            }
        } catch (e: IOException) {
            scope.launch {
                _errorMessages.emit("ارتباط قطع شد: ${e.message}")
            }
        } finally {
            _connectionState.value = ConnectionState.DISCONNECTED
            closeQuietly()
        }
    }

    /**
     * انتظار می‌رود آردوینو خطوطی مانند "23.5,60.2,1" ارسال کند (جدا شده با کاما).
     * این تابع را در صورت تغییر فرمت داده آردوینوی خودتان، ویرایش کنید.
     */
    private fun parseLine(line: String): SensorSample? {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return null
        return try {
            val parts = trimmed.split(",").map { it.trim().toFloat() }
            SensorSample(timestampMs = System.currentTimeMillis(), values = parts)
        } catch (e: NumberFormatException) {
            null // خط ناقص یا نامعتبر، نادیده گرفته می‌شود
        }
    }

    /** ارسال یک دستور متنی به آردوینو (به همراه \n در انتها) */
    fun sendCommand(command: String) {
        scope.launch {
            try {
                outputStream?.write((command + "\n").toByteArray())
                outputStream?.flush()
            } catch (e: IOException) {
                _errorMessages.emit("ارسال دستور ناموفق بود: ${e.message}")
            }
        }
    }

    fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
        closeQuietly()
    }

    private fun closeQuietly() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            // نادیده گرفته می‌شود
        }
        outputStream = null
        socket = null
    }
}
