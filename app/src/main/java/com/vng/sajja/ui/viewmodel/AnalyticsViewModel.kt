package com.vng.sajja.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vng.sajja.data.local.AppDatabase
import com.vng.sajja.data.local.DeviceUsageStatsEntity
import com.vng.sajja.domain.analytics.UsageTrackerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.deviceUsageDao()
    private val trackerManager = UsageTrackerManager(application)

    init {
        trackerManager.startTracking()
    }

    val pastWeekStats: StateFlow<List<DeviceUsageStatsEntity>> = dao.getPastWeekStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun recordSimulatedCharge() {
        viewModelScope.launch(Dispatchers.IO) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val existing = dao.getStatsForDate(today) ?: DeviceUsageStatsEntity(dateString = today)
            dao.upsertStats(
                existing.copy(
                    chargeCount = existing.chargeCount + 1,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackerManager.stopTracking()
    }
}
