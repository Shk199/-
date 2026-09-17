package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.entity.DailySpiritualLog
import com.example.data.local.entity.UserProfileGoalEntity
import com.example.ui.screens.SpiritualTrackerScreen
import com.example.ui.theme.RaqebTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun spiritual_tracker_screenshot() {
        val sampleLog = DailySpiritualLog(
            date = "2026-09-17",
            fajrDone = true,
            fajrMosque = true,
            dhuhrDone = true,
            duhaDone = true,
            morningAdhkar = true,
            quranPages = 4,
            spiritualScore = 65
        )

        composeTestRule.setContent {
            RaqebTheme {
                SpiritualTrackerScreen(
                    todayLog = sampleLog,
                    recentLogs = listOf(sampleLog),
                    userProfile = UserProfileGoalEntity(),
                    onTogglePrayer = { _, _ -> },
                    onTogglePrayerMosque = { _, _ -> },
                    onToggleNafila = { _, _ -> },
                    onToggleAdhkar = { _, _ -> },
                    onUpdateQuranPages = { _ -> },
                    onToggleFasting = { _, _ -> },
                    onOpenGoalsSettings = { }
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/spiritual_tracker.png")
    }
}
