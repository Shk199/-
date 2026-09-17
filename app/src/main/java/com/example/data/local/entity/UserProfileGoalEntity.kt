package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ملف أهداف المستخدم والتخصيص الذكي
 */
@Entity(tableName = "user_profile_goal")
data class UserProfileGoalEntity(
    @PrimaryKey
    val id: Int = 1,
    val primaryGoal: String = "الانتظام في صلاة الفجر", // "الانتظام في صلاة الفجر", "ترك عادة سيئة", "المحافظة على النوافل", "حفظ اللسان"
    val secondaryGoal: String = "الاستغفار اليومي وتزكية النفس",
    val streakDays: Int = 1,
    val totalIstighfarCount: Int = 0,
    val selectedCity: String = "مكة المكرمة",
    val latitude: Double = 21.4225,
    val longitude: Double = 39.8262,
    val isPrivateLockEnabled: Boolean = false,
    val privatePin: String = ""
)
