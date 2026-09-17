package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * قيود دفتر المحاسبة والاستغفار اليومي الخاص
 */
@Entity(tableName = "muhasabah_entries")
data class MuhasabahEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val slipCategory: String, // "زلة لسان أو غيبة", "تضييع وقت", "غضب وانفعال", "تقصير في حق", "تهاون في ذكر"
    val slipDetail: String, // ملاحظة خاصة للمحاسبة
    val expiationDua: String, // الدعاء أو الذكر المأثور المقترح
    val expiationDuaMeaning: String, // فضل الذكر وسنده
    val tasbeehTarget: Int = 100, // عدد مرات الذكر المقترحة للتكفير
    val tasbeehCompleted: Int = 0, // ما تم إنجازه في المسبحة
    val isRepented: Boolean = false, // تم الاستغفار والتوبة
    val timestamp: Long = System.currentTimeMillis()
)
