package com.example.smarthome

enum class DeviceType(val displayNameHindi: String) {
    LIGHT("स्मार्ट लाइट"),
    AC_THERMOSTAT("स्मार्ट एसी"),
    FAN("स्मार्ट पंखा"),
    SMART_LOCK("स्मार्ट लॉक"),
    SMART_TV("स्मार्ट टीवी"),
    SMART_PLUG("स्मार्ट सॉकेट")
}

data class SmartDevice(
    val id: String,
    val nameHindi: String,
    val type: DeviceType,
    val roomHindi: String,
    val isPoweredOn: Boolean,
    val value: Int = 50, // 0-100% brightness, 16-30°C temperature, 1-5 fan speed
    val stateLabel: String = "",
    val powerWatts: Int = 15
)

data class SmartRoutine(
    val id: String,
    val titleHindi: String,
    val iconEmoji: String,
    val descriptionHindi: String,
    val timeHintHindi: String,
    val badge: String = "ऑटोमेशन"
)

