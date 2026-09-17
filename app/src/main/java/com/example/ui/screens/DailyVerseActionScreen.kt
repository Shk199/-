package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entity.PracticalVerseEntity
import com.example.ui.theme.*

@Composable
fun DailyVerseActionScreen(
    verses: List<PracticalVerseEntity>,
    onTogglePracticalAction: (PracticalVerseEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }

    val categories = listOf("الكل", "العفو وكظم الغيظ", "الصدقة والبذل", "حفظ اللسان والقلب", "بر الوالدين", "التوكل واليقين")

    val filteredVerses = if (selectedCategory == "الكل") {
        verses
    } else {
        verses.filter { it.category.contains(selectedCategory) }
    }

    val primaryVerse = verses.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("daily_verse_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Hero Featured Daily Verse Card ---
        if (primaryVerse != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_verse_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF163E32), Color(0xFF265A49))
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "آية وحديث اليوم وسياقها",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldPrimary.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = primaryVerse.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldLight,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Verse Content
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = primaryVerse.text,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = primaryVerse.reference,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Context & Wisdom
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "السياق والمعنى التربوي:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = primaryVerse.contextAndWisdom,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Box (The Core Unique Feature)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (primaryVerse.isActionDone) EmeraldLight else GoldLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.DirectionsRun,
                                                contentDescription = null,
                                                tint = if (primaryVerse.isActionDone) EmeraldPrimary else GoldDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "الخطوة العملية الفورية للتطبيق اليوم:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (primaryVerse.isActionDone) EmeraldPrimary else GoldDark
                                            )
                                        }

                                        if (primaryVerse.isActionDone) {
                                            Text(
                                                text = "تم التطبيق ✓",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = primaryVerse.practicalStep,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark,
                                        lineHeight = 22.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { onTogglePracticalAction(primaryVerse) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (primaryVerse.isActionDone) EmeraldPrimary else GoldDark
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("toggle_action_btn")
                                    ) {
                                        Icon(
                                            imageVector = if (primaryVerse.isActionDone) Icons.Default.CheckCircle else Icons.Default.TaskAlt,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (primaryVerse.isActionDone) "أنجزت الخطوة اليوم بنجاح" else "أنجزت هذه الخطوة الآن",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Category Filter Tabs ---
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // --- 3. Verses with Practical Actions Library ---
        item {
            Text(
                text = "سجل الآيات والخطوات التطبيقية",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(filteredVerses) { verse ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verse_item_${verse.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (verse.isActionDone) EmeraldLight.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
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
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = verse.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = { onTogglePracticalAction(verse) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (verse.isActionDone) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = "تبديل الإنجاز",
                                tint = if (verse.isActionDone) EmeraldPrimary else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = verse.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = verse.reference,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = verse.practicalStep,
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
