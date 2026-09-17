package com.example.data.repository

import com.example.data.local.dao.RaqebDao
import com.example.data.local.entity.DailySpiritualLog
import com.example.data.local.entity.MoralChallengeEntity
import com.example.data.local.entity.MuhasabahEntity
import com.example.data.local.entity.PracticalVerseEntity
import com.example.data.local.entity.UserProfileGoalEntity
import com.example.data.model.SpiritualWisdomData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class RaqebRepository(private val dao: RaqebDao) {

    // --- Daily Logs ---
    fun getLogByDate(date: String): Flow<DailySpiritualLog?> = dao.getLogByDate(date)

    fun getRecentLogs(limit: Int = 14): Flow<List<DailySpiritualLog>> = dao.getRecentLogs(limit)

    suspend fun saveOrUpdateLog(log: DailySpiritualLog) = withContext(Dispatchers.IO) {
        // Calculate score automatically
        val score = calculateScore(log)
        dao.insertOrUpdateLog(log.copy(spiritualScore = score, updatedAt = System.currentTimeMillis()))
    }

    private fun calculateScore(log: DailySpiritualLog): Int {
        var points = 0
        // Prayers (50 points total, 10 per prayer + 2 bonus if in mosque)
        if (log.fajrDone) points += if (log.fajrMosque) 12 else 10
        if (log.dhuhrDone) points += if (log.dhuhrMosque) 12 else 10
        if (log.asrDone) points += if (log.asrMosque) 12 else 10
        if (log.maghribDone) points += if (log.maghribMosque) 12 else 10
        if (log.ishaDone) points += if (log.ishaMosque) 12 else 10

        // Nawafil (15 points)
        if (log.duhaDone) points += 5
        if (log.witrDone) points += 5
        if (log.tahajjudDone) points += 5

        // Adhkar (15 points)
        if (log.morningAdhkar) points += 5
        if (log.eveningAdhkar) points += 5
        if (log.sleepAdhkar) points += 5

        // Quran Ward (10 points)
        if (log.quranPages > 0) points += (log.quranPages * 2).coerceAtMost(10)

        // Fasting (10 points)
        if (log.fastingDone) points += 10

        return points.coerceIn(0, 100)
    }

    // --- Moral Challenges ---
    fun getAllChallenges(): Flow<List<MoralChallengeEntity>> = dao.getAllChallenges()

    fun getActiveChallenge(): Flow<MoralChallengeEntity?> = dao.getActiveChallenge()

    suspend fun updateChallenge(challenge: MoralChallengeEntity) = withContext(Dispatchers.IO) {
        dao.updateChallenge(challenge)
    }

    suspend fun setActiveChallenge(id: Int) = withContext(Dispatchers.IO) {
        dao.deactivateAllChallenges()
        dao.setActiveChallenge(id)
    }

    // --- Muhasabah & Istighfar ---
    fun getAllMuhasabah(): Flow<List<MuhasabahEntity>> = dao.getAllMuhasabah()

    fun getMuhasabahByDate(date: String): Flow<List<MuhasabahEntity>> = dao.getMuhasabahByDate(date)

    suspend fun insertMuhasabah(entry: MuhasabahEntity) = withContext(Dispatchers.IO) {
        dao.insertMuhasabah(entry)
    }

    suspend fun updateMuhasabah(entry: MuhasabahEntity) = withContext(Dispatchers.IO) {
        dao.updateMuhasabah(entry)
    }

    suspend fun deleteMuhasabah(entry: MuhasabahEntity) = withContext(Dispatchers.IO) {
        dao.deleteMuhasabah(entry)
    }

    // --- Practical Verses ---
    fun getAllPracticalVerses(): Flow<List<PracticalVerseEntity>> = dao.getAllPracticalVerses()

    suspend fun updatePracticalVerse(verse: PracticalVerseEntity) = withContext(Dispatchers.IO) {
        dao.updateVerse(verse)
    }

    // --- User Profile ---
    fun getUserProfile(): Flow<UserProfileGoalEntity?> = dao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfileGoalEntity) = withContext(Dispatchers.IO) {
        dao.insertUserProfile(profile)
    }

    // --- Initialization Seed ---
    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingChallenges = dao.getAllChallenges().firstOrNull()
        if (existingChallenges.isNullOrEmpty()) {
            dao.insertChallenges(SpiritualWisdomData.getDefaultChallenges())
        }

        val existingVerses = dao.getAllPracticalVerses().firstOrNull()
        if (existingVerses.isNullOrEmpty()) {
            dao.insertVerses(SpiritualWisdomData.getDefaultPracticalVerses())
        }

        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            dao.insertUserProfile(UserProfileGoalEntity())
        }
    }
}
