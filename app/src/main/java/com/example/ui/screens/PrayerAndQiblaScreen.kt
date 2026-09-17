package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserProfileGoalEntity
import com.example.data.model.MosqueGuide
import com.example.data.model.PrayerSchedule
import com.example.data.model.PrayerTimesCalculator
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerAndQiblaScreen(
    prayerSchedule: PrayerSchedule?,
    qiblaBearing: Float,
    userProfile: UserProfileGoalEntity?,
    onSelectCity: (PrayerTimesCalculator.CityCoordinates) -> Unit
) {
    val context = LocalContext.current
    var showCityPicker by remember { mutableStateOf(false) }

    // Compass sensor heading
    var deviceAzimuth by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, gravity, 0, 3)
                    hasGravity = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                    hasGeomagnetic = true
                }

                if (hasGravity && hasGeomagnetic) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        val azimuthRad = orientation[0]
                        var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                        azimuthDeg = (azimuthDeg + 360) % 360
                        deviceAzimuth = azimuthDeg
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val currentCityName = userProfile?.selectedCity ?: "مكة المكرمة"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("prayer_qibla_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. City & Next Prayer Hero Banner ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prayer_times_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF13362A), Color(0xFF1E4E3E))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // City Selector Button
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = EmeraldDark.copy(alpha = 0.5f),
                                modifier = Modifier.clickable { showCityPicker = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentCityName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = GoldLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Next prayer badge
                            prayerSchedule?.let {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldPrimary
                                ) {
                                    Text(
                                        text = "القادمة: ${it.nextPrayerName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBg,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        prayerSchedule?.let {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "المتبقي على أذان ${it.nextPrayerName}:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldLight
                                    )
                                    Text(
                                        text = it.timeUntilNextFormatted,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Five Prayer Times Grid ---
        if (prayerSchedule != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مواعيد الصلاة اليوم",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val prayerList = listOf(
                            prayerSchedule.fajr,
                            prayerSchedule.sunrise,
                            prayerSchedule.dhuhr,
                            prayerSchedule.asr,
                            prayerSchedule.maghrib,
                            prayerSchedule.isha
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            prayerList.forEach { pt ->
                                val isNext = pt.isNext
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isNext) EmeraldLight else Color.Transparent
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = pt.nameArabic,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isNext) EmeraldPrimary else TextSecondaryDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = pt.timeFormatted,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isNext) EmeraldDark else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Interactive Qibla Compass Card ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_compass_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بوصلة اتجاه القبلة إلى مكة المكرمة",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${qiblaBearing.toInt()}°",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Compass Graphic Dial
                    // Angle difference relative to device heading
                    val relativeQiblaAngle = (qiblaBearing - deviceAzimuth + 360) % 360
                    val animatedAngle by animateFloatAsState(targetValue = relativeQiblaAngle, label = "qiblaAngle")

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .background(EmeraldLight.copy(alpha = 0.4f))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.width / 2 - 12.dp.toPx()

                            // Outer border ring
                            drawCircle(
                                color = EmeraldPrimary.copy(alpha = 0.3f),
                                radius = radius,
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Inner subtle ring
                            drawCircle(
                                color = EmeraldSoft.copy(alpha = 0.5f),
                                radius = radius - 16.dp.toPx(),
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Rotated Pointer towards Kaaba
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(animatedAngle),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxHeight().padding(vertical = 18.dp)
                            ) {
                                // Kaaba indicator at the top
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = "اتجاه الكعبة",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldDark
                                    ) {
                                        Text(
                                            text = "الكعبة المشرفة",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // South indicator
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TextMuted)
                                )
                            }
                        }

                        // Center Pivot Hub
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "وجّه هاتفك أفقياً حتى يتطابق مؤشر الكعبة الذهبي مع مقدمة الجهاز.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- 4. Nearby Mosques Quick Navigation ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "أقرب المساجد للمستخدم",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ابحث عن أقرب المساجد ومصليات الجماعة في محيطك عبر الخرائط.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=مساجد"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("فتح الخريطة", color = Color.White)
                    }
                }
            }
        }

        // --- 5. Mosque Adab & Duas Guide ---
        item {
            Text(
                text = "آداب وأذكار المسجد",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(MosqueGuide.etiquetteList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldLight.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.arabicText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // --- City Picker Dialog ---
    if (showCityPicker) {
        AlertDialog(
            onDismissRequest = { showCityPicker = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "اختر المدينة لحساب المواقيت والقبلة", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PrayerTimesCalculator.supportedCities) { city ->
                        val isSelected = city.cityName == currentCityName
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectCity(city)
                                    showCityPicker = false
                                },
                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = city.cityName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldDark else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityPicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
