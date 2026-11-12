package com.example.wellness

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BreakReminderState(
    val isBreakNeeded: Boolean = false,
    val minutesActive: Int = 0,
    val reminderMessage: String = "",
    val breakType: String = "20-20-20 नियम व जलपान"
)

class SmartBreakReminderEngine(
    private val context: Context,
    private val externalScope: CoroutineScope
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("arya_break_reminder_prefs", Context.MODE_PRIVATE)

    private val _breakState = MutableStateFlow(BreakReminderState())
    val breakState: StateFlow<BreakReminderState> = _breakState.asStateFlow()

    private var sessionStartTime = System.currentTimeMillis()
    private var lastBreakTime = System.currentTimeMillis()
    private val breakIntervalMinutes = 30 // 30 minutes recommended ergonomic interval

    init {
        startBackgroundMonitoring()
    }

    private fun startBackgroundMonitoring() {
        externalScope.launch(Dispatchers.Default) {
            while (true) {
                delay(60_000L) // Check every minute in background
                val now = System.currentTimeMillis()
                val minutesSinceLastBreak = ((now - lastBreakTime) / (60 * 1000)).toInt()

                if (minutesSinceLastBreak >= breakIntervalMinutes && !_breakState.value.isBreakNeeded) {
                    val message = when {
                        minutesSinceLastBreak >= 60 -> "आप 1 घंटे से अधिक समय से सक्रिय हैं। कृपया 5 मिनट का ब्रेक लें, पानी पिएं और हल्का खिंचाव (stretch) करें।"
                        else -> "विश्राम का समय: 20-20-20 नियम अपनाएं — 20 सेकंड के लिए दूर देखें, आंखों को आराम दें और थोड़ा पानी पिएं।"
                    }

                    _breakState.value = BreakReminderState(
                        isBreakNeeded = true,
                        minutesActive = minutesSinceLastBreak,
                        reminderMessage = message
                    )
                }
            }
        }
    }

    fun acknowledgeBreak() {
        lastBreakTime = System.currentTimeMillis()
        _breakState.value = BreakReminderState(
            isBreakNeeded = false,
            minutesActive = 0,
            reminderMessage = "शानदार! आपने विश्राम ले लिया है। अब आप तरोताजा हैं।"
        )
    }

    fun snoozeBreak(minutes: Int = 10) {
        lastBreakTime = System.currentTimeMillis() - ((breakIntervalMinutes - minutes) * 60 * 1000L)
        _breakState.value = _breakState.value.copy(isBreakNeeded = false)
    }

    fun resetSession() {
        sessionStartTime = System.currentTimeMillis()
        lastBreakTime = System.currentTimeMillis()
        _breakState.value = BreakReminderState()
    }
}
