package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MuhasabahEntity
import com.example.data.model.SpiritualWisdomData
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuhasabahScreen(
    todayMuhasabah: List<MuhasabahEntity>,
    allMuhasabah: List<MuhasabahEntity>,
    onAddMuhasabah: (slipCategory: String, note: String, dua: String, meaning: String, targetCount: Int) -> Unit,
    onIncrementTasbeeh: (MuhasabahEntity) -> Unit,
    onMarkRepented: (MuhasabahEntity) -> Unit,
    onDeleteMuhasabah: (MuhasabahEntity) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedActiveForTasbeeh by remember { mutableStateOf<MuhasabahEntity?>(null) }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("muhasabah_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Header Card: Private Tranquil Muhasabah Banner ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("muhasabah_banner_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF14241C), Color(0xFF1B382B))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "دفتر المحاسبة والاستغفار (خاص وآمن)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = EmeraldSoft,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "«حَاسِبُوا أَنْفُسَكُمْ قَبْلَ أَنْ تُحَاسَبُوا، وَزِنُوهَا قَبْلَ أَنْ تُوزَنُوا»",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 26.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "مساحتك الهادئة بنهاية اليوم لتفريغ العوارض والتقصير، واستبدال كل ذنب باستغفار وكفارة مأثورة تمحوه بإذن الله.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldLight.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_muhasabah_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = DarkBg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تسجيل محاسبة جديدة وتفريغ زلّة",
                                color = DarkBg,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Interactive Digital Tasbeeh / Repentance Counter Card ---
        item {
            val activeEntry = selectedActiveForTasbeeh ?: todayMuhasabah.firstOrNull { !it.isRepented }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("digital_tasbeeh_card"),
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
                        Text(
                            text = "مسبحة التكفير والاستغفار التفاعلية",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (activeEntry != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = EmeraldLight.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "الذكر المكفّر المختار:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeEntry.expiationDua,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeEntry.expiationDuaMeaning,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryDark,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Circular Tap Counter
                        val target = activeEntry.tasbeehTarget
                        val completed = activeEntry.tasbeehCompleted
                        val progress = (completed.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
                        val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbeehProgress")

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(170.dp)
                                .clip(CircleShape)
                                .clickable {
                                    vibrateShort()
                                    onIncrementTasbeeh(activeEntry)
                                }
                                .testTag("tasbeeh_tap_circle")
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 10.dp,
                                color = if (completed >= target) GoldPrimary else EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$completed",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (completed >= target) GoldDark else EmeraldPrimary
                                )
                                Text(
                                    text = "الهدف: $target",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (completed >= target) "أتممت الكفارة بإذن الله" else "المس للتسبيح",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (completed >= target) GoldDark else TextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        // General Istighfar Default when no active entry
                        Text(
                            text = "«أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ»",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "لا توجد ذنوب مسجلة اليوم بحمد الله. يمكنك الاستغفار المطلق أو تفريغ عارض جديد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // --- 3. Recorded Muhasabah Entries List ---
        item {
            Text(
                text = "سجل تفريغ اليوم والمحاسبة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (todayMuhasabah.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يوم نقي بإذن الله",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "لم تدون أي زلات اليوم. إذا تذكرت موقفاً تود التكفير عنه، اضغط على زر التسجيل أعلاه.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(todayMuhasabah) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedActiveForTasbeeh = entry },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (entry.isRepented) EmeraldLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (entry.isRepented) EmeraldPrimary else AmberAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = entry.slipCategory,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (entry.isRepented) Color.White else AmberAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (entry.isRepented) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "تم التكفير",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "${entry.tasbeehCompleted}/${entry.tasbeehTarget}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteMuhasabah(entry) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف القيد",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        if (entry.slipDetail.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = entry.slipDetail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Expiation Dua box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = entry.expiationDua,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Add Muhasabah Dialog ---
    if (showAddDialog) {
        var selectedCategoryIndex by remember { mutableStateOf(0) }
        var privateNote by remember { mutableStateOf("") }

        val suggestions = SpiritualWisdomData.slipSuggestions
        val currentSuggestion = suggestions[selectedCategoryIndex]

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "محاسبة النفس وتفريغ زلّة", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "اختر العارض أو الزلة لتحديد الكفارة المأثورة:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    suggestions.forEachIndexed { index, item ->
                        val isSelected = selectedCategoryIndex == index
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategoryIndex = index },
                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedCategoryIndex = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.categoryName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Suggested Prophetic Expiation Preview
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "الكفارة والذكر النبوي المقترح:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentSuggestion.suggestedDua,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "الهدف المقترح بالمسبحة: ${currentSuggestion.defaultTasbeehTarget} مرة",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    OutlinedTextField(
                        value = privateNote,
                        onValueChange = { privateNote = it },
                        label = { Text("ملاحظة سرية للمحاسبة (اختياري)") },
                        placeholder = { Text("اكتب ما يعينك على عدم العودة للذنب...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddMuhasabah(
                            currentSuggestion.categoryName,
                            privateNote,
                            currentSuggestion.suggestedDua,
                            currentSuggestion.duaMeaning,
                            currentSuggestion.defaultTasbeehTarget
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("حفظ والبدء بالاستغفار", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
