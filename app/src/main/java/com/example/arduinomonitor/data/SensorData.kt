package com.example.arduinomonitor.data

/**
 * یک نمونه داده دریافتی از آردوینو.
 * فرمت مورد انتظار از سمت آردوینو (هر خط، پایان با \n):
 *   value1,value2,value3
 * مثال: "23.5,60.2,1"  -> دما، رطوبت، وضعیت رله/LED
 * در صورت نیاز به تعداد بیشتر یا کمتر مقدار، فقط کافیست پارسر را در BluetoothService تغییر دهید.
 */
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

/**
 * دستوراتی که از طریق منوی تنظیمات به آردوینو ارسال می‌شوند.
 * در کد آردوینوی نمونه (arduino_sketch.ino) این رشته‌ها هندل شده‌اند.
 */
object ArduinoCommands {
    const val LED_ON = "LED_ON"
    const val LED_OFF = "LED_OFF"
    const val SAVE_ON = "SAVE_ON"     // فعال کردن ذخیره‌سازی لحظه‌ای در حافظه آردوینو (مثلا SD Card)
    const val SAVE_OFF = "SAVE_OFF"
    const val STREAM_ON = "STREAM_ON"   // شروع ارسال مداوم داده
    const val STREAM_OFF = "STREAM_OFF" // توقف ارسال داده
}
