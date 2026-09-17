package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MoralChallengeEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoralCounselorScreen(
    activeChallenge: MoralChallengeEntity?,
    allChallenges: List<MoralChallengeEntity>,
    advisorResponse: String?,
    isAdvisorLoading: Boolean,
    onToggleChallengeDay: (MoralChallengeEntity, Int, Boolean) -> Unit,
    onSwitchActiveChallenge: (Int) -> Unit,
    onAskAdvisor: (String, String) -> Unit,
    onClearAdvisorResponse: () -> Unit
) {
    var showAdvisorDialog by remember { mutableStateOf(false) }
    var selectedTopic by remember { mutableStateOf("كظم الغيظ وضبط الغضب") }
    var userSituationText by remember { mutableStateOf("") }

    val challenge = activeChallenge ?: allChallenges.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("moral_counselor_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Active Weekly Challenge Hero Card ---
        if (challenge != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_challenge_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Header Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(EmeraldPrimary, EmeraldMedium)
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = GoldPrimary.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "تحدي الأسبوع ${challenge.weekNumber}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = challenge.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = challenge.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EmeraldLight
                                )
                            }
                        }

                        // Prophetic Reference & Explanation
                        Column(modifier = Modifier.padding(18.dp)) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.6f))
                            ) {
                                Row(modifier = Modifier.padding(14.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = challenge.propheticHadith,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = EmeraldDark,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = challenge.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Daily Situation Prompt
                            Text(
                                text = "موقف اليوم لتطبيق الخلق:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = challenge.dailySituationPrompt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 7 Days Interactive Tracker
                            Text(
                                text = "متابعة التطبيق اليومي (7 أيام):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondaryDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val daysStatus = listOf(
                                Pair("اليوم 1", challenge.day1Completed),
                                Pair("اليوم 2", challenge.day2Completed),
                                Pair("اليوم 3", challenge.day3Completed),
                                Pair("اليوم 4", challenge.day4Completed),
                                Pair("اليوم 5", challenge.day5Completed),
                                Pair("اليوم 6", challenge.day6Completed),
                                Pair("اليوم 7", challenge.day7Completed)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                daysStatus.forEachIndexed { index, pair ->
                                    val dayNum = index + 1
                                    val isDone = pair.second

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable {
                                            onToggleChallengeDay(challenge, dayNum, !isDone)
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "تم",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "$dayNum",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = TextSecondaryDark,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isDone) "أنجزت" else "ي $dayNum",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = if (isDone) EmeraldPrimary else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Smart Moral Counselor AI & Guidance Button ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("advisor_action_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GoldLight),
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
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مستشار التزكية الذكي",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "واجهت موقفاً صعباً أو تحتاج خطة لترك عادة سيئة؟ اطلب نصيحة موجهة الآن.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { showAdvisorDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("open_advisor_btn")
                    ) {
                        Text(text = "استشر", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 3. Display Advisor Advice Result If Available ---
        if (advisorResponse != null || isAdvisorLoading) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("advisor_response_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "توجيه مستشار التزكية",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                            IconButton(onClick = { onClearAdvisorResponse() }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isAdvisorLoading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = EmeraldPrimary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "جاري استحضار الحكمة النبوية والتوجيه العملي...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondaryDark
                                )
                            }
                        } else if (advisorResponse != null) {
                            Text(
                                text = advisorResponse,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 4. Library of Moral Challenges ---
        item {
            Text(
                text = "جميع التحديات الأخلاقية",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(allChallenges) { ch ->
            val isCurrent = ch.id == challenge?.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchActiveChallenge(ch.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) EmeraldLight else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${ch.weekNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else TextSecondaryDark
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = ch.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) EmeraldDark else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ch.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCurrent) EmeraldMedium else TextSecondaryDark
                            )
                        }
                    }

                    if (isCurrent) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPrimary
                        ) {
                            Text(
                                text = "المفعل حالياً",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onSwitchActiveChallenge(ch.id) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "تفعيل", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

    // --- Advisor Input Dialog ---
    if (showAdvisorDialog) {
        AlertDialog(
            onDismissRequest = { showAdvisorDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = GoldDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "استشارة مستشار التزكية", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "اختر الموضوع أو الخلق الذي تجاهد نفسك فيه:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    val quickTopics = listOf(
                        "كظم الغيظ وضبط الغضب",
                        "حفظ اللسان من الغيبة",
                        "الانتظام في صلاة الفجر",
                        "صلة رحم فيها جفاء",
                        "ترك عادة سيئة والتوبة منها"
                    )

                    quickTopics.forEach { topic ->
                        val isSelected = selectedTopic == topic
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedTopic = topic },
                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedTopic = topic },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EmeraldDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = userSituationText,
                        onValueChange = { userSituationText = it },
                        label = { Text("أضف تفاصيل الموقف (اختياري)") },
                        placeholder = { Text("مثال: زميل في العمل استفزني وأريد التصرف بحكمة...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAskAdvisor(selectedTopic, userSituationText)
                        showAdvisorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("طلب التوجيه", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdvisorDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
