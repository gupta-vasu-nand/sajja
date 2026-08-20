package com.vng.sajja.domain.analytics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.vng.sajja.data.local.AppDatabase
import com.vng.sajja.data.local.DeviceUsageStatsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsageTrackerManager(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.deviceUsageDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    private var screenOnStartTime: Long = 0L
    private var isReceiverRegistered = false

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    recordChargeEvent(ctx)
                }
                Intent.ACTION_SCREEN_ON -> {
                    screenOnStartTime = System.currentTimeMillis()
                }
                Intent.ACTION_SCREEN_OFF -> {
                    if (screenOnStartTime > 0L) {
                        val duration = System.currentTimeMillis() - screenOnStartTime
                        screenOnStartTime = 0L
                        addScreenOnTime(duration)
                    }
                }
            }
        }
    }

    fun startTracking() {
        initTodayStatsIfMissing()

        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
            context.applicationContext.registerReceiver(receiver, filter)
            isReceiverRegistered = true
            screenOnStartTime = System.currentTimeMillis()
        }
    }

    fun stopTracking() {
        if (isReceiverRegistered) {
            try {
                if (screenOnStartTime > 0L) {
                    val duration = System.currentTimeMillis() - screenOnStartTime
                    screenOnStartTime = 0L
                    addScreenOnTime(duration)
                }
                context.applicationContext.unregisterReceiver(receiver)
            } catch (_: Exception) {}
            isReceiverRegistered = false
        }
    }

    fun recordChargeEvent(ctx: Context?) {
        scope.launch {
            val today = getTodayDateString()
            val existing = dao.getStatsForDate(today) ?: DeviceUsageStatsEntity(dateString = today)
            val batteryPct = getBatteryPercentage(ctx ?: context)
            val updated = existing.copy(
                chargeCount = existing.chargeCount + 1,
                lastBatteryLevel = batteryPct,
                timestamp = System.currentTimeMillis()
            )
            dao.upsertStats(updated)
        }
    }

    fun addScreenOnTime(durationMillis: Long) {
        if (durationMillis <= 0) return
        scope.launch {
            val today = getTodayDateString()
            val existing = dao.getStatsForDate(today) ?: DeviceUsageStatsEntity(dateString = today)
            val updated = existing.copy(
                screenOnTimeMillis = existing.screenOnTimeMillis + durationMillis,
                timestamp = System.currentTimeMillis()
            )
            dao.upsertStats(updated)
        }
    }

    private fun getBatteryPercentage(ctx: Context): Int {
        val bm = ctx.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        return bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 100
    }

    private fun initTodayStatsIfMissing() {
        scope.launch {
            try {
                val today = getTodayDateString()
                val existing = dao.getStatsForDate(today)
                if (existing == null) {
                    dao.upsertStats(
                        DeviceUsageStatsEntity(
                            dateString = today,
                            timestamp = System.currentTimeMillis(),
                            chargeCount = 0,
                            screenOnTimeMillis = 0L,
                            screenOffTimeMillis = 0L,
                            lastBatteryLevel = getBatteryPercentage(context)
                        )
                    )
                }
            } catch (_: Exception) {}
        }
    }
}
