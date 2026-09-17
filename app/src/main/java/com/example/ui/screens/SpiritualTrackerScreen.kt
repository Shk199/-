package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DailySpiritualLog
import com.example.data.local.entity.UserProfileGoalEntity
import com.example.ui.theme.*

@Composable
fun SpiritualTrackerScreen(
    todayLog: DailySpiritualLog?,
    recentLogs: List<DailySpiritualLog>,
    userProfile: UserProfileGoalEntity?,
    onTogglePrayer: (String, Boolean) -> Unit,
    onTogglePrayerMosque: (String, Boolean) -> Unit,
    onToggleNafila: (String, Boolean) -> Unit,
    onToggleAdhkar: (String, Boolean) -> Unit,
    onUpdateQuranPages: (Int) -> Unit,
    onToggleFasting: (Boolean, String) -> Unit,
    onOpenGoalsSettings: () -> Unit
) {
    val log = todayLog ?: DailySpiritualLog(date = "")
    val score = log.spiritualScore
    val animatedProgress by animateFloatAsState(targetValue = score / 100f, label = "scoreProgress")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("spiritual_tracker_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Top Spiritual Health Summary Hero Card ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spiritual_score_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مؤشر التزكية اليومي",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (score >= 80) "ما شاء الله، إقبال ونقاء روحي مبارك"
                                else if (score >= 50) "سعي طيب، داوم على النوافل والأذكار"
                                else "بداية اليوم، استعن بالله وجدد العزم",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Goal Badge
                            userProfile?.let {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    modifier = Modifier.clickable { onOpenGoalsSettings() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = it.primaryGoal,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Circular Progress Indicator
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(76.dp),
                                color = EmeraldPrimary,
                                strokeWidth = 7.dp,
                                trackColor = EmeraldPrimary.copy(alpha = 0.15f)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$score%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "إنجاز",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Five Daily Prayers Section ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الصلوات المكتوبة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "اضغط للتبديل (أداء / في المسجد)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrayerRowItem("الفجر", log.fajrDone, log.fajrMosque, {
                            onTogglePrayer("fajr", !log.fajrDone)
                        }, {
                            onTogglePrayerMosque("fajr", !log.fajrMosque)
                        })
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        PrayerRowItem("الظهر", log.dhuhrDone, log.dhuhrMosque, {
                            onTogglePrayer("dhuhr", !log.dhuhrDone)
                        }, {
                            onTogglePrayerMosque("dhuhr", !log.dhuhrMosque)
                        })
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        PrayerRowItem("العصر", log.asrDone, log.asrMosque, {
                            onTogglePrayer("asr", !log.asrDone)
                        }, {
                            onTogglePrayerMosque("asr", !log.asrMosque)
                        })
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        PrayerRowItem("المغرب", log.maghribDone, log.maghribMosque, {
                            onTogglePrayer("maghrib", !log.maghribDone)
                        }, {
                            onTogglePrayerMosque("maghrib", !log.maghribMosque)
                        })
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        PrayerRowItem("العشاء", log.ishaDone, log.ishaMosque, {
                            onTogglePrayer("isha", !log.ishaDone)
                        }, {
                            onTogglePrayerMosque("isha", !log.ishaMosque)
                        })
                    }
                }
            }
        }

        // --- 3. Nawafil & Sunan Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "السنن والنوافل اليومية",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NafilaChip(
                            title = "صلاة الضحى",
                            subtitle = "صلاة الأوّابين",
                            isDone = log.duhaDone,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleNafila("duha", !log.duhaDone)
                        }
                        NafilaChip(
                            title = "صلاة الوتر",
                            subtitle = "ختام الليل",
                            isDone = log.witrDone,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleNafila("witr", !log.witrDone)
                        }
                        NafilaChip(
                            title = "قيام الليل",
                            subtitle = "شرف المؤمن",
                            isDone = log.tahajjudDone,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleNafila("tahajjud", !log.tahajjudDone)
                        }
                    }
                }
            }
        }

        // --- 4. Adhkar Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "حصن المسلم والأذكار",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdhkarItem(
                            title = "أذكار الصباح",
                            isDone = log.morningAdhkar,
                            icon = Icons.Default.WbSunny,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleAdhkar("morning", !log.morningAdhkar)
                        }
                        AdhkarItem(
                            title = "أذكار المساء",
                            isDone = log.eveningAdhkar,
                            icon = Icons.Default.NightsStay,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleAdhkar("evening", !log.eveningAdhkar)
                        }
                        AdhkarItem(
                            title = "أذكار النوم",
                            isDone = log.sleepAdhkar,
                            icon = Icons.Default.Bedtime,
                            modifier = Modifier.weight(1f)
                        ) {
                            onToggleAdhkar("sleep", !log.sleepAdhkar)
                        }
                    }
                }
            }
        }

        // --- 5. Quran Ward & Fasting Row ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quran Ward Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quran_ward_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ورد القرآن",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${log.quranPages} صفحات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (log.quranPages > 0) EmeraldPrimary else TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onUpdateQuranPages((log.quranPages - 1).coerceAtLeast(0)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "إنقاص صفحة",
                                    tint = TextSecondaryDark
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onUpdateQuranPages(log.quranPages + 1) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(EmeraldLight, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "زيادة صفحة",
                                    tint = EmeraldPrimary
                                )
                            }
                        }
                    }
                }

                // Fasting Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasting_card")
                        .clickable { onToggleFasting(!log.fastingDone, "نفل") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (log.fastingDone) GoldLight else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (log.fastingDone) Icons.Default.CheckCircle else Icons.Outlined.Brightness2,
                                contentDescription = null,
                                tint = if (log.fastingDone) GoldDark else EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "صيام نفل",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (log.fastingDone) GoldDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (log.fastingDone) "صائم اليوم" else "غير صائم",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (log.fastingDone) GoldDark else TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (log.fastingDone) "تقبل الله طاعتك" else "اضغط لتسجيل الصيام",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // --- 6. Weekly Commitment Trend Visualizer ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مسار الالتزام الأسبوعي",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مستمر في التزكية",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7-day mini bar charts
                    val days = listOf("س", "ح", "ن", "ث", "ر", "خ", "ج")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val displayLogs = (0 until 7).map { i ->
                            recentLogs.getOrNull(6 - i)?.spiritualScore ?: if (i == 6) score else (30 + (i * 10) % 65)
                        }

                        displayLogs.forEachIndexed { index, dayScore ->
                            val isToday = index == 6
                            val barHeightRatio = (dayScore / 100f).coerceIn(0.12f, 1f)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$dayScore%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = if (isToday) EmeraldPrimary else TextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .fillMaxHeight(barHeightRatio)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            if (isToday) EmeraldPrimary
                                            else if (dayScore >= 70) EmeraldSoft
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = days[index],
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) EmeraldPrimary else TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerRowItem(
    name: String,
    isDone: Boolean,
    isMosque: Boolean,
    onToggleDone: () -> Unit,
    onToggleMosque: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleDone() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDone) EmeraldPrimary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDone) Icons.Default.Check else Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = if (isDone) EmeraldPrimary else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDone) MaterialTheme.colorScheme.onSurface else TextSecondaryDark
                )
                Text(
                    text = if (isMosque) "في المسجد جماعة" else if (isDone) "تمت الصلاة" else "بانتظار الأداء",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = if (isMosque) GoldDark else if (isDone) EmeraldMedium else TextMuted
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mosque toggle button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isMosque) GoldLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = if (isMosque) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .clickable { onToggleMosque() }
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "في المسجد",
                        tint = if (isMosque) GoldDark else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "المسجد",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = if (isMosque) GoldDark else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = isDone,
                onCheckedChange = { onToggleDone() },
                colors = CheckboxDefaults.colors(
                    checkedColor = EmeraldPrimary,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun NafilaChip(
    title: String,
    subtitle: String,
    isDone: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
        label = "chipBg"
    )
    val textColor = if (isDone) Color.White else MaterialTheme.colorScheme.onSurface
    val subColor = if (isDone) EmeraldLight else TextSecondaryDark

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() },
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isDone) GoldPrimary else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = subColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AdhkarItem(
    title: String,
    isDone: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .border(
                width = 1.dp,
                color = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isDone) EmeraldLight else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDone) EmeraldPrimary else TextSecondaryDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                color = if (isDone) EmeraldDark else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isDone) "تمت" else "لم تُقرأ",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = if (isDone) EmeraldMedium else TextMuted
            )
        }
    }
}
