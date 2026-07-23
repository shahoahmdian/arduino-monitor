package com.example.arduinomonitor.data

data class SensorSample(
    val timestampMs: Long,
    val values: List<Float>
)

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    FAILED
}

object ArduinoCommands {
    const val RELAY1_ON = "RELAY1_ON"
    const val RELAY1_OFF = "RELAY1_OFF"
    const val RELAY2_ON = "RELAY2_ON"
    const val RELAY2_OFF = "RELAY2_OFF"
    const val RELAY3_ON = "RELAY3_ON"
    const val RELAY3_OFF = "RELAY3_OFF"
    const val RELAY4_ON = "RELAY4_ON"
    const val RELAY4_OFF = "RELAY4_OFF"
    const val SAVE_ON = "SAVE_ON"
    const val SAVE_OFF = "SAVE_OFF"
    const val STREAM_ON = "STREAM_ON"
    const val STREAM_OFF = "STREAM_OFF"

    val RELAY_ON = listOf(RELAY1_ON, RELAY2_ON, RELAY3_ON, RELAY4_ON)
    val RELAY_OFF = listOf(RELAY1_OFF, RELAY2_OFF, RELAY3_OFF, RELAY4_OFF)
    val RELAY_NAMES = listOf("رله یک", "رله دو", "رله سه", "رله چهار")
}
