package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.service.AryaVoiceBackgroundService
import java.io.File

data class ServiceCacheFixResult(
    val freedBytes: Long,
    val freedDisplay: String,
    val isServiceRefreshed: Boolean,
    val message: String
)

class ServiceCacheFixController(private val context: Context) {

    /**
     * Cleans application internal cache, code cache, external cache, trims memory,
     * and restarts/refreshes the AryaVoiceBackgroundService to fix memory leaks and crashes.
     */
    fun fixServiceAndClearCache(): ServiceCacheFixResult {
        var totalFreedBytes = 0L
        try {
            totalFreedBytes += clearDirectory(context.cacheDir)
            totalFreedBytes += clearDirectory(context.codeCacheDir)
            context.externalCacheDir?.let {
                totalFreedBytes += clearDirectory(it)
            }
        } catch (e: Exception) {
            Log.w("ServiceCacheFix", "Cache clear warning: ${e.message}")
        }

        // Trigger garbage collection to reclaim unused memory
        try {
            System.gc()
        } catch (_: Exception) {}

        // Restart / Refresh Background Service
        var serviceRefreshed = false
        try {
            val refreshIntent = Intent(context, AryaVoiceBackgroundService::class.java).apply {
                action = AryaVoiceBackgroundService.ACTION_REFRESH_SERVICE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(refreshIntent)
            } else {
                context.startService(refreshIntent)
            }
            serviceRefreshed = true
        } catch (e: Exception) {
            Log.w("ServiceCacheFix", "Service refresh warning: ${e.message}")
        }

        val freedMb = totalFreedBytes / (1024.0 * 1024.0)
        val freedDisplay = if (freedMb >= 1.0) {
            String.format("%.1f MB", freedMb)
        } else {
            "${totalFreedBytes / 1024} KB"
        }

        val summaryMessage = if (serviceRefreshed) {
            "कैश साफ़ किया गया ($freedDisplay मुक्त) और बैकग्राउंड सर्विस को सफलतापूर्वक रीस्टार्ट/फिक्स किया गया।"
        } else {
            "कैश साफ़ किया गया ($freedDisplay मुक्त)।"
        }

        return ServiceCacheFixResult(
            freedBytes = totalFreedBytes,
            freedDisplay = freedDisplay,
            isServiceRefreshed = serviceRefreshed,
            message = summaryMessage
        )
    }

    private fun clearDirectory(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var bytes = 0L
        dir.listFiles()?.forEach { file ->
            bytes += if (file.isDirectory) {
                clearDirectory(file) + file.length()
            } else {
                val len = file.length()
                try {
                    file.delete()
                } catch (_: Exception) {}
                len
            }
        }
        return bytes
    }
}
