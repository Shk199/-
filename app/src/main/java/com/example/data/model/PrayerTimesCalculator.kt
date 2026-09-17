package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

data class PrayerTime(
    val nameArabic: String,
    val timeFormatted: String,
    val timeMillis: Long,
    val isNext: Boolean = false
)

data class PrayerSchedule(
    val fajr: PrayerTime,
    val sunrise: PrayerTime,
    val dhuhr: PrayerTime,
    val asr: PrayerTime,
    val maghrib: PrayerTime,
    val isha: PrayerTime,
    val nextPrayerName: String,
    val timeUntilNextFormatted: String
)

object PrayerTimesCalculator {

    data class CityCoordinates(val cityName: String, val lat: Double, val lng: Double)

    val supportedCities = listOf(
        CityCoordinates("مكة المكرمة", 21.4225, 39.8262),
        CityCoordinates("المدينة المنورة", 24.5247, 39.5692),
        CityCoordinates("الرياض", 24.7136, 46.6753),
        CityCoordinates("القاهرة", 30.0444, 31.2357),
        CityCoordinates("القدس الشريف", 31.7683, 35.2137),
        CityCoordinates("عَمّان", 31.9539, 35.9106),
        CityCoordinates("دبي", 25.2048, 55.2708),
        CityCoordinates("الكويت", 29.3759, 47.9774),
        CityCoordinates("الدوحة", 25.2854, 51.5310),
        CityCoordinates("الجزائر", 36.7538, 3.0588),
        CityCoordinates("الرباط", 34.0209, -6.8416),
        CityCoordinates("إسطنبول", 41.0082, 28.9784)
    )

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerSchedule {
        val now = calendar.timeInMillis
        val timeZoneOffset = calendar.timeZone.getOffset(now) / 3600000.0

        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        // Approximate solar declination & equation of time
        val b = 2 * Math.PI * (dayOfYear - 81) / 365.0
        val equationOfTime = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b) // minutes
        val declination = 23.45 * sin(b) // degrees

        // Solar noon (Dhuhr)
        val solarNoonHours = 12.0 + (latitude * 0.0) + (timeZoneOffset * 15.0 - longitude) / 15.0 - (equationOfTime / 60.0)

        // Fajr angle (18.5 degrees below horizon for Umm Al-Qura / standard)
        val fajrHourAngle = calculateHourAngle(latitude, declination, -18.5)
        // Sunrise (-0.833 degrees)
        val sunriseHourAngle = calculateHourAngle(latitude, declination, -0.833)
        // Asr (Shafi/Hanbali/Maliki standard shadow = 1)
        val asrAltitude = 90.0 - Math.toDegrees(atan(1.0 + tan(Math.toRadians(abs(latitude - declination))))).let { 90.0 - it }
        val asrHourAngle = calculateHourAngle(latitude, declination, asrAltitude)
        // Maghrib (-0.833 degrees)
        val maghribHourAngle = sunriseHourAngle
        // Isha (-18.0 or 1.5h after maghrib)
        val ishaHourAngle = calculateHourAngle(latitude, declination, -18.0)

        val fajrTime = getTimeMillis(calendar, solarNoonHours - fajrHourAngle)
        val sunriseTime = getTimeMillis(calendar, solarNoonHours - sunriseHourAngle)
        val dhuhrTime = getTimeMillis(calendar, solarNoonHours)
        val asrTime = getTimeMillis(calendar, solarNoonHours + asrHourAngle)
        val maghribTime = getTimeMillis(calendar, solarNoonHours + maghribHourAngle)
        val ishaTime = getTimeMillis(calendar, solarNoonHours + ishaHourAngle)

        val times = listOf(
            Triple("الفجر", fajrTime, "صلاة الفجر"),
            Triple("الشروق", sunriseTime, "الشروق"),
            Triple("الظهر", dhuhrTime, "صلاة الظهر"),
            Triple("العصر", asrTime, "صلاة العصر"),
            Triple("المغرب", maghribTime, "صلاة المغرب"),
            Triple("العشاء", ishaTime, "صلاة العشاء")
        )

        val nextIndex = times.indexOfFirst { it.second > now }.let { if (it == -1) 0 else it }
        val nextPrayer = times[nextIndex]

        val diff = if (nextPrayer.second > now) nextPrayer.second - now else (nextPrayer.second + 86400000L) - now
        val diffHours = (diff / (1000 * 60 * 60)).toInt()
        val diffMinutes = ((diff / (1000 * 60)) % 60).toInt()

        val countdownStr = if (diffHours > 0) "$diffHours س و $diffMinutes د" else "$diffMinutes دقيقة"

        val fmt = SimpleDateFormat("hh:mm a", Locale("ar"))

        return PrayerSchedule(
            fajr = PrayerTime("الفجر", fmt.format(Date(fajrTime)), fajrTime, nextIndex == 0),
            sunrise = PrayerTime("الشروق", fmt.format(Date(sunriseTime)), sunriseTime, nextIndex == 1),
            dhuhr = PrayerTime("الظهر", fmt.format(Date(dhuhrTime)), dhuhrTime, nextIndex == 2),
            asr = PrayerTime("العصر", fmt.format(Date(asrTime)), asrTime, nextIndex == 3),
            maghrib = PrayerTime("المغرب", fmt.format(Date(maghribTime)), maghribTime, nextIndex == 4),
            isha = PrayerTime("العشاء", fmt.format(Date(ishaTime)), ishaTime, nextIndex == 5),
            nextPrayerName = nextPrayer.first,
            timeUntilNextFormatted = countdownStr
        )
    }

    private fun calculateHourAngle(latitude: Double, declination: Double, altitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val altRad = Math.toRadians(altitude)

        val cosH = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clamped = cosH.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped)) / 15.0
    }

    private fun getTimeMillis(base: Calendar, decimalHours: Double): Long {
        val c = base.clone() as Calendar
        val hours = decimalHours.toInt()
        val minutes = ((decimalHours - hours) * 60).toInt()
        c.set(Calendar.HOUR_OF_DAY, hours.coerceIn(0, 23))
        c.set(Calendar.MINUTE, minutes.coerceIn(0, 59))
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }
}
