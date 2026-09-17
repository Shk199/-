package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.RaqebDatabase
import com.example.data.local.entity.DailySpiritualLog
import com.example.data.local.entity.MoralChallengeEntity
import com.example.data.local.entity.MuhasabahEntity
import com.example.data.local.entity.PracticalVerseEntity
import com.example.data.local.entity.UserProfileGoalEntity
import com.example.data.model.PrayerSchedule
import com.example.data.model.PrayerTimesCalculator
import com.example.data.model.QiblaCalculator
import com.example.data.remote.GeminiSpiritualAdvisor
import com.example.data.repository.RaqebRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RaqebViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RaqebRepository
    val todayDateFormatted: String

    private val _advisorResponse = MutableStateFlow<String?>(null)
    val advisorResponse: StateFlow<String?> = _advisorResponse.asStateFlow()

    private val _isAdvisorLoading = MutableStateFlow(false)
    val isAdvisorLoading: StateFlow<Boolean> = _isAdvisorLoading.asStateFlow()

    private val _currentPrayerSchedule = MutableStateFlow<PrayerSchedule?>(null)
    val currentPrayerSchedule: StateFlow<PrayerSchedule?> = _currentPrayerSchedule.asStateFlow()

    private val _currentQiblaBearing = MutableStateFlow(0f)
    val currentQiblaBearing: StateFlow<Float> = _currentQiblaBearing.asStateFlow()

    init {
        val database = RaqebDatabase.getDatabase(application)
        repository = RaqebRepository(database.raqebDao())

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        todayDateFormatted = dateFormat.format(Date())

        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            updateCalculationsForCity(21.4225, 39.8262) // Default Makkah
        }
    }

    val todayLog: StateFlow<DailySpiritualLog?> = repository.getLogByDate(todayDateFormatted)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentLogs: StateFlow<List<DailySpiritualLog>> = repository.getRecentLogs(7)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChallenge: StateFlow<MoralChallengeEntity?> = repository.getActiveChallenge()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allChallenges: StateFlow<List<MoralChallengeEntity>> = repository.getAllChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMuhasabah: StateFlow<List<MuhasabahEntity>> = repository.getMuhasabahByDate(todayDateFormatted)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMuhasabah: StateFlow<List<MuhasabahEntity>> = repository.getAllMuhasabah()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val practicalVerses: StateFlow<List<PracticalVerseEntity>> = repository.getAllPracticalVerses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileGoalEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Daily Spiritual Actions ---

    fun togglePrayer(prayer: String, isDone: Boolean) {
        val current = todayLog.value ?: DailySpiritualLog(date = todayDateFormatted)
        val updated = when (prayer) {
            "fajr" -> current.copy(fajrDone = isDone)
            "dhuhr" -> current.copy(dhuhrDone = isDone)
            "asr" -> current.copy(asrDone = isDone)
            "maghrib" -> current.copy(maghribDone = isDone)
            "isha" -> current.copy(ishaDone = isDone)
            else -> current
        }
        viewModelScope.launch {
            repository.saveOrUpdateLog(updated)
        }
    }

    fun togglePrayerMosque(prayer: String, isMosque: Boolean) {
        val current = todayLog.value ?: DailySpiritualLog(date = todayDateFormatted)
        val updated = when (prayer) {
            "fajr" -> current.copy(fajrMosque = isMosque, fajrDone = if (isMosque) true else current.fajrDone)
            "dhuhr" -> current.copy(dhuhrMosque = isMosque, dhuhrDone = if (isMosque) true else current.dhuhrDone)
            "asr" -> current.copy(asrMosque = isMosque, asrDone = if (isMosque) true else current.asrDone)
            "maghrib" -> current.copy(maghribMosque = isMosque, maghribDone = if (isMosque) true else current.maghribDone)
            "isha" -> current.copy(ishaMosque = isMosque, ishaDone = if (isMosque) true else current.ishaDone)
            else -> current
        }
        viewModelScope.launch {
            repository.saveOrUpdateLog(updated)
        }
    }

    fun toggleNafila(nafila: String, isDone: Boolean) {
        val current = todayLog.value ?: DailySpiritualLog(date = todayDateFormatted)
        val updated = when (nafila) {
            "duha" -> current.copy(duhaDone = isDone)
            "witr" -> current.copy(witrDone = isDone)
            "tahajjud" -> current.copy(tahajjudDone = isDone)
            else -> current
        }
        viewModelScope.launch {
            repository.saveOrUpdateLog(updated)
        }
    }

    fun toggleAdhkar(adhkarType: String, isDone: Boolean) {
        val current = todayLog.value ?: DailySpiritualLog(date = todayDateFormatted)
        val updated = when (adhkarType) {
            "morning" -> current.copy(morningAdhkar = isDone)
            "evening" -> current.copy(eveningAdhkar = isDone)
            "sleep" -> current.copy(sleepAdhkar = isDone)
            else -> current
        }
        viewModelScope.launch {
            repository.saveOrUpdateLog(updated)
        }
    }

    fun updateQuranPages(pages: Int) {
        val current = todayLog.value ?: DailySpiritualLog(date = todayDateFormatted)
        val updated = current.copy(quranPages = pages.coerceAtLeast(0))
        viewModelScope.launch {
            repository.saveOrUpdateLog(updated)
        }
    }

    fun toggleFasting(isDone: Boolean, type: String = "نفل عام") {
        val current = todayLog.value ?: DailySpiritualLog(date = todayDateFormatted)
        val updated = current.copy(fastingDone = isDone, fastingType = if (isDone) type else "")
        viewModelScope.launch {
            repository.saveOrUpdateLog(updated)
        }
    }

    // --- Moral Challenge Actions ---

    fun toggleChallengeDay(challenge: MoralChallengeEntity, dayIndex: Int, isDone: Boolean) {
        val updated = when (dayIndex) {
            1 -> challenge.copy(day1Completed = isDone)
            2 -> challenge.copy(day2Completed = isDone)
            3 -> challenge.copy(day3Completed = isDone)
            4 -> challenge.copy(day4Completed = isDone)
            5 -> challenge.copy(day5Completed = isDone)
            6 -> challenge.copy(day6Completed = isDone)
            7 -> challenge.copy(day7Completed = isDone)
            else -> challenge
        }
        viewModelScope.launch {
            repository.updateChallenge(updated)
        }
    }

    fun switchActiveChallenge(challengeId: Int) {
        viewModelScope.launch {
            repository.setActiveChallenge(challengeId)
        }
    }

    // --- Muhasabah Actions ---

    fun addMuhasabah(
        slipCategory: String,
        note: String,
        suggestedDua: String,
        duaMeaning: String,
        targetCount: Int
    ) {
        val entry = MuhasabahEntity(
            date = todayDateFormatted,
            slipCategory = slipCategory,
            slipDetail = note,
            expiationDua = suggestedDua,
            expiationDuaMeaning = duaMeaning,
            tasbeehTarget = targetCount,
            tasbeehCompleted = 0,
            isRepented = false
        )
        viewModelScope.launch {
            repository.insertMuhasabah(entry)
        }
    }

    fun incrementTasbeeh(entry: MuhasabahEntity) {
        val newCount = entry.tasbeehCompleted + 1
        val repented = newCount >= entry.tasbeehTarget
        val updated = entry.copy(
            tasbeehCompleted = newCount,
            isRepented = repented || entry.isRepented
        )
        viewModelScope.launch {
            repository.updateMuhasabah(updated)
            // Also update total Istighfar count in profile
            val profile = userProfile.value ?: UserProfileGoalEntity()
            repository.saveUserProfile(profile.copy(totalIstighfarCount = profile.totalIstighfarCount + 1))
        }
    }

    fun markMuhasabahRepented(entry: MuhasabahEntity) {
        viewModelScope.launch {
            repository.updateMuhasabah(entry.copy(isRepented = true))
        }
    }

    fun deleteMuhasabah(entry: MuhasabahEntity) {
        viewModelScope.launch {
            repository.deleteMuhasabah(entry)
        }
    }

    // --- Practical Verses Actions ---

    fun togglePracticalAction(verse: PracticalVerseEntity) {
        val newState = !verse.isActionDone
        val updated = verse.copy(
            isActionDone = newState,
            completedAt = if (newState) System.currentTimeMillis() else 0L
        )
        viewModelScope.launch {
            repository.updatePracticalVerse(updated)
        }
    }

    // --- Advisor AI & Wisdom Actions ---

    fun askAdvisor(topic: String, context: String) {
        viewModelScope.launch {
            _isAdvisorLoading.value = true
            _advisorResponse.value = null
            val result = GeminiSpiritualAdvisor.getSpiritualAdvice(topic, context)
            _advisorResponse.value = result.getOrNull()
            _isAdvisorLoading.value = false
        }
    }

    fun clearAdvisorResponse() {
        _advisorResponse.value = null
    }

    // --- Prayer Times & Qibla Actions ---

    fun updateCalculationsForCity(lat: Double, lng: Double) {
        val schedule = PrayerTimesCalculator.calculatePrayerTimes(lat, lng, Calendar.getInstance())
        _currentPrayerSchedule.value = schedule
        _currentQiblaBearing.value = QiblaCalculator.calculateQiblaBearing(lat, lng)
    }

    fun selectCity(city: PrayerTimesCalculator.CityCoordinates) {
        updateCalculationsForCity(city.lat, city.lng)
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileGoalEntity()
            repository.saveUserProfile(
                current.copy(
                    selectedCity = city.cityName,
                    latitude = city.lat,
                    longitude = city.lng
                )
            )
        }
    }

    fun updateUserGoals(primaryGoal: String, secondaryGoal: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileGoalEntity()
            repository.saveUserProfile(
                current.copy(
                    primaryGoal = primaryGoal,
                    secondaryGoal = secondaryGoal
                )
            )
        }
    }
}
