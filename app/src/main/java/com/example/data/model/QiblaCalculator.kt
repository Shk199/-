package com.example.data.model

import kotlin.math.*

object QiblaCalculator {

    // Kaaba, Makkah Coordinates
    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    /**
     * Calculates the Qibla bearing in degrees (0..360 from true North)
     */
    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val latK = Math.toRadians(KAABA_LAT)
        val lngK = Math.toRadians(KAABA_LNG)
        val latU = Math.toRadians(userLat)
        val lngU = Math.toRadians(userLng)

        val deltaLng = lngK - lngU

        val y = sin(deltaLng)
        val x = cos(latU) * tan(latK) - sin(latU) * cos(deltaLng)

        var qiblaAngle = Math.toDegrees(atan2(y, x))
        qiblaAngle = (qiblaAngle + 360) % 360
        return qiblaAngle.toFloat()
    }
}
