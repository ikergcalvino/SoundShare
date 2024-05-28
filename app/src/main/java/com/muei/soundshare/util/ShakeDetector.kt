package com.muei.soundshare.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(private val onShakeListener: () -> Unit) : SensorEventListener {
    private var lastShakeTime = 0L

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
            if (acceleration > 18) { // Increased threshold to make it less sensitive
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) { // 1 second interval
                    lastShakeTime = currentTime
                    onShakeListener()
                }
            }
        }
    }
}
