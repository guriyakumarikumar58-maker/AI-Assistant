package com.example.wellness

data class WellnessMetric(
    val todaySteps: Int = 7420,
    val targetSteps: Int = 10000,
    val caloriesBurned: Int = 368,
    val distanceKm: Float = 5.2f,
    val sleepHours: Float = 7.5f,
    val deepSleepPercent: Int = 32,
    val sleepQualityScore: Int = 88, // out of 100
    val waterGlasses: Int = 6,
    val targetWaterGlasses: Int = 8,
    val heartRateBpm: Int = 72,
    val aiInsightHindi: String = "आपकी 7.5 घंटे की नींद बहुत गहरी और संतुलित रही। आज के 10,000 कदमों के लक्ष्य के लिए केवल 2,580 कदम बाकी हैं। शाम को 2 ग्लास पानी और 15 मिनट की सैर से आपका दिन परफेक्ट रहेगा!"
)

enum class WellnessCategory(val titleHindi: String) {
    ACTIVITY("शारीरिक गतिविधि"),
    SLEEP("नींद व विश्राम"),
    HYDRATION("जल संतुलन"),
    RECOVERY("मानसिक संतुलन")
}
