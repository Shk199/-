package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PrayerTimesCalculator
import com.example.data.model.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("راقب", appName)
    }

    @Test
    fun `test qibla bearing calculation`() {
        // Cairo coordinates: Lat ~30.0444, Lng ~31.2357 -> Qibla towards Makkah (~136 deg South-East)
        val bearing = QiblaCalculator.calculateQiblaBearing(30.0444, 31.2357)
        assertTrue("Bearing should be between 130 and 145 degrees", bearing in 130f..145f)
    }

    @Test
    fun `test prayer times calculation`() {
        // Makkah coordinates
        val schedule = PrayerTimesCalculator.calculatePrayerTimes(21.4225, 39.8262, Calendar.getInstance())
        assertEquals("الفجر", schedule.fajr.nameArabic)
        assertEquals("الظهر", schedule.dhuhr.nameArabic)
        assertEquals("العصر", schedule.asr.nameArabic)
        assertEquals("المغرب", schedule.maghrib.nameArabic)
        assertEquals("العشاء", schedule.isha.nameArabic)
    }
}
