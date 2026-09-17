package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * تذكير الآيات والأحاديث مع الخطوة العملية التطبيقية
 */
@Entity(tableName = "practical_verses")
data class PracticalVerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String, // الآية أو الحديث
    val reference: String, // اسم السورة أو الراوي
    val contextAndWisdom: String, // التفسير الميسر والسياق التربوي
    val practicalStep: String, // الخطوة العملية اليومية الملموسة
    val category: String, // "الصدقة", "الصبر", "الإحسان", "حفظ اللسان", "بر الوالدين"
    val dateAssigned: String, // YYYY-MM-DD
    val isActionDone: Boolean = false,
    val completedAt: Long = 0L
)
