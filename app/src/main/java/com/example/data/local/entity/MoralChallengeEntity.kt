package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * تحديات الأخلاق والتزكية الأسبوعية واليومية
 */
@Entity(tableName = "moral_challenges")
data class MoralChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String, // e.g., "حفظ اللسان وضبط الكلام"
    val subtitle: String, // e.g., "أسبوع تنقية الكلمات من الغيبة واللغو"
    val propheticHadith: String, // الحديث الشريف أو الآية
    val explanation: String, // التفسير والتوجيه
    val dailySituationPrompt: String, // الموقف اليومي المقترح للتطبيق
    val day1Completed: Boolean = false,
    val day2Completed: Boolean = false,
    val day3Completed: Boolean = false,
    val day4Completed: Boolean = false,
    val day5Completed: Boolean = false,
    val day6Completed: Boolean = false,
    val day7Completed: Boolean = false,
    val isActive: Boolean = false,
    val reflectionsNote: String = "",
    val weekNumber: Int = 1
)
