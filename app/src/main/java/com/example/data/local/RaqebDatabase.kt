package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.RaqebDao
import com.example.data.local.entity.DailySpiritualLog
import com.example.data.local.entity.MoralChallengeEntity
import com.example.data.local.entity.MuhasabahEntity
import com.example.data.local.entity.PracticalVerseEntity
import com.example.data.local.entity.UserProfileGoalEntity

@Database(
    entities = [
        DailySpiritualLog::class,
        MoralChallengeEntity::class,
        MuhasabahEntity::class,
        PracticalVerseEntity::class,
        UserProfileGoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RaqebDatabase : RoomDatabase() {

    abstract fun raqebDao(): RaqebDao

    companion object {
        @Volatile
        private var INSTANCE: RaqebDatabase? = null

        fun getDatabase(context: Context): RaqebDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RaqebDatabase::class.java,
                    "raqeb_spiritual_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
