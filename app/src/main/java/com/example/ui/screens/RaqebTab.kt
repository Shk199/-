package com.example.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

// Navigation enum for Raqeb tabs
enum class RaqebTab(val titleArabic: String, val iconVector: ImageVector) {
    TRACKER("الصحة الروحية", Icons.Filled.Favorite),
    MORAL("مستشار الأخلاق", Icons.Filled.Psychology),
    MUHASABAH("دفتر الاستغفار", Icons.Filled.SelfImprovement),
    DAILY_VERSE("خطوة اليوم", Icons.Filled.MenuBook),
    PRAYER_QIBLA("القبلة والمواقيت", Icons.Filled.Explore)
}
