package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * سجل العبادات اليومية والصحة الروحية
 */
@Entity(tableName = "daily_spiritual_logs")
data class DailySpiritualLog(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val fajrDone: Boolean = false,
    val fajrMosque: Boolean = false,
    val dhuhrDone: Boolean = false,
    val dhuhrMosque: Boolean = false,
    val asrDone: Boolean = false,
    val asrMosque: Boolean = false,
    val maghribDone: Boolean = false,
    val maghribMosque: Boolean = false,
    val ishaDone: Boolean = false,
    val ishaMosque: Boolean = false,
    val duhaDone: Boolean = false,
    val witrDone: Boolean = false,
    val tahajjudDone: Boolean = false,
    val morningAdhkar: Boolean = false,
    val eveningAdhkar: Boolean = false,
    val sleepAdhkar: Boolean = false,
    val quranPages: Int = 0,
    val fastingDone: Boolean = false,
    val fastingType: String = "", // "الاثنين", "الخميس", "الأيام البيض", "نفل"
    val spiritualScore: Int = 0, // 0 - 100
    val updatedAt: Long = System.currentTimeMillis()
)
