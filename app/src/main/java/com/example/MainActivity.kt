package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RaqebViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RaqebTheme {
                // Ensure Arabic RTL layout direction across the entire app
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: RaqebViewModel = viewModel()

                    val todayLog by viewModel.todayLog.collectAsStateWithLifecycle()
                    val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()
                    val activeChallenge by viewModel.activeChallenge.collectAsStateWithLifecycle()
                    val allChallenges by viewModel.allChallenges.collectAsStateWithLifecycle()
                    val todayMuhasabah by viewModel.todayMuhasabah.collectAsStateWithLifecycle()
                    val allMuhasabah by viewModel.allMuhasabah.collectAsStateWithLifecycle()
                    val practicalVerses by viewModel.practicalVerses.collectAsStateWithLifecycle()
                    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                    val prayerSchedule by viewModel.currentPrayerSchedule.collectAsStateWithLifecycle()
                    val qiblaBearing by viewModel.currentQiblaBearing.collectAsStateWithLifecycle()
                    val advisorResponse by viewModel.advisorResponse.collectAsStateWithLifecycle()
                    val isAdvisorLoading by viewModel.isAdvisorLoading.collectAsStateWithLifecycle()

                    var currentTab by remember { mutableStateOf(RaqebTab.TRACKER) }
                    var showGoalsDialog by remember { mutableStateOf(false) }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_scaffold"),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_raqeb_icon),
                                                contentDescription = "شعار راقب",
                                                modifier = Modifier.size(30.dp).clip(CircleShape)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "رَاقِبْ",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldDark
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = GoldPrimary.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "التزكية",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = GoldDark,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "المرافق الرقمي للتزكية والرقابة الذاتية",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                color = TextSecondaryDark
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    // Goal button
                                    IconButton(
                                        onClick = { showGoalsDialog = true },
                                        modifier = Modifier.testTag("settings_goal_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = "أهداف التزكية",
                                            tint = GoldPrimary
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp,
                                modifier = Modifier
                                    .testTag("main_navigation_bar")
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                RaqebTab.entries.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            Icon(
                                                imageVector = tab.iconVector,
                                                contentDescription = tab.titleArabic,
                                                tint = if (isSelected) EmeraldPrimary else TextMuted
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.titleArabic,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.sp,
                                                color = if (isSelected) EmeraldDark else TextMuted
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = EmeraldLight
                                        ),
                                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                RaqebTab.TRACKER -> {
                                    SpiritualTrackerScreen(
                                        todayLog = todayLog,
                                        recentLogs = recentLogs,
                                        userProfile = userProfile,
                                        onTogglePrayer = { prayer, isDone ->
                                            viewModel.togglePrayer(prayer, isDone)
                                        },
                                        onTogglePrayerMosque = { prayer, isMosque ->
                                            viewModel.togglePrayerMosque(prayer, isMosque)
                                        },
                                        onToggleNafila = { nafila, isDone ->
                                            viewModel.toggleNafila(nafila, isDone)
                                        },
                                        onToggleAdhkar = { adhkar, isDone ->
                                            viewModel.toggleAdhkar(adhkar, isDone)
                                        },
                                        onUpdateQuranPages = { pages ->
                                            viewModel.updateQuranPages(pages)
                                        },
                                        onToggleFasting = { isDone, type ->
                                            viewModel.toggleFasting(isDone, type)
                                        },
                                        onOpenGoalsSettings = {
                                            showGoalsDialog = true
                                        }
                                    )
                                }

                                RaqebTab.MORAL -> {
                                    MoralCounselorScreen(
                                        activeChallenge = activeChallenge,
                                        allChallenges = allChallenges,
                                        advisorResponse = advisorResponse,
                                        isAdvisorLoading = isAdvisorLoading,
                                        onToggleChallengeDay = { challenge, day, isDone ->
                                            viewModel.toggleChallengeDay(challenge, day, isDone)
                                        },
                                        onSwitchActiveChallenge = { id ->
                                            viewModel.switchActiveChallenge(id)
                                        },
                                        onAskAdvisor = { topic, ctx ->
                                            viewModel.askAdvisor(topic, ctx)
                                        },
                                        onClearAdvisorResponse = {
                                            viewModel.clearAdvisorResponse()
                                        }
                                    )
                                }

                                RaqebTab.MUHASABAH -> {
                                    MuhasabahScreen(
                                        todayMuhasabah = todayMuhasabah,
                                        allMuhasabah = allMuhasabah,
                                        onAddMuhasabah = { category, note, dua, meaning, target ->
                                            viewModel.addMuhasabah(category, note, dua, meaning, target)
                                        },
                                        onIncrementTasbeeh = { entry ->
                                            viewModel.incrementTasbeeh(entry)
                                        },
                                        onMarkRepented = { entry ->
                                            viewModel.markMuhasabahRepented(entry)
                                        },
                                        onDeleteMuhasabah = { entry ->
                                            viewModel.deleteMuhasabah(entry)
                                        }
                                    )
                                }

                                RaqebTab.DAILY_VERSE -> {
                                    DailyVerseActionScreen(
                                        verses = practicalVerses,
                                        onTogglePracticalAction = { verse ->
                                            viewModel.togglePracticalAction(verse)
                                        }
                                    )
                                }

                                RaqebTab.PRAYER_QIBLA -> {
                                    PrayerAndQiblaScreen(
                                        prayerSchedule = prayerSchedule,
                                        qiblaBearing = qiblaBearing,
                                        userProfile = userProfile,
                                        onSelectCity = { city ->
                                            viewModel.selectCity(city)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showGoalsDialog) {
                        SettingsAndGoalsDialog(
                            currentProfile = userProfile,
                            onDismiss = { showGoalsDialog = false },
                            onSaveGoals = { primary, secondary ->
                                viewModel.updateUserGoals(primary, secondary)
                            }
                        )
                    }
                }
            }
        }
    }
}
