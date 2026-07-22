package com.example.arduinomonitor.data

import kotlin.math.sqrt

data class ChannelStats(
    val min: Float = 0f,
    val max: Float = 0f,
    val average: Float = 0f,
    val stdDev: Float = 0f,
    val last: Float = 0f
)

/**
 * تحلیل آماری روی هر کانال (ستون) از داده‌های دریافتی.
 * روی یک پنجره لغزان (sliding window) کار می‌کند تا حجم محاسبات محدود بماند.
 */
class DataAnalyzer(private val windowSize: Int = 200) {

    private val channelBuffers = mutableListOf<MutableList<Float>>()

    fun reset() {
        channelBuffers.clear()
    }

    fun addSample(sample: SensorSample): List<ChannelStats> {
        // اطمینان از وجود بافر به تعداد کانال‌ها
        while (channelBuffers.size < sample.values.size) {
            channelBuffers.add(mutableListOf())
        }

        sample.values.forEachIndexed { index, value ->
            val buffer = channelBuffers[index]
            buffer.add(value)
            if (buffer.size > windowSize) {
                buffer.removeAt(0)
            }
        }

        return computeStats()
    }

    private fun computeStats(): List<ChannelStats> {
        return channelBuffers.map { buffer ->
            if (buffer.isEmpty()) return@map ChannelStats()
            val min = buffer.min()
            val max = buffer.max()
            val avg = buffer.average().toFloat()
            val variance = buffer.map { (it - avg) * (it - avg) }.average().toFloat()
            val stdDev = sqrt(variance)
            ChannelStats(min = min, max = max, average = avg, stdDev = stdDev, last = buffer.last())
        }
    }

    fun getSeries(channelIndex: Int): List<Float> {
        if (channelIndex >= channelBuffers.size) return emptyList()
        return channelBuffers[channelIndex].toList()
    }

    fun channelCount(): Int = channelBuffers.size
}
