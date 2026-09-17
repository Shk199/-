package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DailySpiritualLog
import com.example.data.local.entity.MoralChallengeEntity
import com.example.data.local.entity.MuhasabahEntity
import com.example.data.local.entity.PracticalVerseEntity
import com.example.data.local.entity.UserProfileGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RaqebDao {

    // --- Daily Spiritual Logs ---
    @Query("SELECT * FROM daily_spiritual_logs WHERE date = :date LIMIT 1")
    fun getLogByDate(date: String): Flow<DailySpiritualLog?>

    @Query("SELECT * FROM daily_spiritual_logs WHERE date = :date LIMIT 1")
    suspend fun getLogByDateSync(date: String): DailySpiritualLog?

    @Query("SELECT * FROM daily_spiritual_logs ORDER BY date DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 14): Flow<List<DailySpiritualLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: DailySpiritualLog)

    // --- Moral Challenges ---
    @Query("SELECT * FROM moral_challenges ORDER BY id ASC")
    fun getAllChallenges(): Flow<List<MoralChallengeEntity>>

    @Query("SELECT * FROM moral_challenges WHERE isActive = 1 LIMIT 1")
    fun getActiveChallenge(): Flow<MoralChallengeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: MoralChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<MoralChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: MoralChallengeEntity)

    @Query("UPDATE moral_challenges SET isActive = 0")
    suspend fun deactivateAllChallenges()

    @Query("UPDATE moral_challenges SET isActive = 1 WHERE id = :id")
    suspend fun setActiveChallenge(id: Int)

    // --- Muhasabah & Istighfar ---
    @Query("SELECT * FROM muhasabah_entries ORDER BY timestamp DESC")
    fun getAllMuhasabah(): Flow<List<MuhasabahEntity>>

    @Query("SELECT * FROM muhasabah_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getMuhasabahByDate(date: String): Flow<List<MuhasabahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuhasabah(entry: MuhasabahEntity)

    @Update
    suspend fun updateMuhasabah(entry: MuhasabahEntity)

    @Delete
    suspend fun deleteMuhasabah(entry: MuhasabahEntity)

    // --- Practical Verses & Hadith ---
    @Query("SELECT * FROM practical_verses ORDER BY id ASC")
    fun getAllPracticalVerses(): Flow<List<PracticalVerseEntity>>

    @Query("SELECT * FROM practical_verses WHERE dateAssigned = :date LIMIT 1")
    fun getVerseByDate(date: String): Flow<PracticalVerseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<PracticalVerseEntity>)

    @Update
    suspend fun updateVerse(verse: PracticalVerseEntity)

    // --- User Profile & Goals ---
    @Query("SELECT * FROM user_profile_goal WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileGoalEntity)
}
