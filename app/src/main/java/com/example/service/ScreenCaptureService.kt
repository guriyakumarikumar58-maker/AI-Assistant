package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Base64
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream

/**
 * ScreenCaptureService:
 * Foreground service that runs MediaProjection to capture a screenshot of the user's
 * screen upon explicit user action or consent.
 *
 * Privacy & Security Guarantees:
 * - Requires explicit user permission prompt via MediaProjectionManager.createScreenCaptureIntent().
 * - Displays a prominent, ongoing foreground notification while active.
 * - Captures a single snapshot upon user request, compresses it into memory, and passes
 *   it to the chat interface for AI analysis.
 * - Automatically terminates immediately after capture or when requested.
 */
class ScreenCaptureService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        const val CHANNEL_ID = "arya_screen_capture_channel"
        const val NOTIFICATION_ID = 2026

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_RESULT_DATA = "extra_result_data"

        private val _capturedScreenBase64 = MutableStateFlow<String?>(null)
        val capturedScreenBase64: StateFlow<String?> = _capturedScreenBase64.asStateFlow()

        private val _isScreenSharingActive = MutableStateFlow(false)
        val isScreenSharingActive: StateFlow<Boolean> = _isScreenSharingActive.asStateFlow()

        private val _screenCaptureStatus = MutableStateFlow<String?>("स्क्रीन शेयरिंग निष्क्रिय")
        val screenCaptureStatus: StateFlow<String?> = _screenCaptureStatus.asStateFlow()

        fun clearCapturedScreen() {
            _capturedScreenBase64.value = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildForegroundNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, 0) ?: 0
        val resultData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_RESULT_DATA)
        }

        if (resultCode != 0 && resultData != null) {
            startScreenCapture(resultCode, resultData)
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startScreenCapture(resultCode: Int, resultData: Intent) {
        val mpManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
        if (mpManager == null) {
            _screenCaptureStatus.value = "मीडिया प्रोजेक्शन सेवा अनुपलब्ध"
            stopSelf()
            return
        }

        try {
            mediaProjection = mpManager.getMediaProjection(resultCode, resultData)
        } catch (e: Exception) {
            _screenCaptureStatus.value = "प्रोजेक्शन अनुमति विफल: ${e.localizedMessage}"
            stopSelf()
            return
        }

        if (mediaProjection == null) {
            _screenCaptureStatus.value = "स्क्रीन अनुमति नहीं मिली"
            stopSelf()
            return
        }

        _isScreenSharingActive.value = true
        _screenCaptureStatus.value = "स्क्रीन कैप्चर प्रारंभ..."

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val screenDensity = metrics.densityDpi

        // Downscale slightly for rapid Base64 Gemini multimodal analysis
        val targetWidth = (screenWidth / 2).coerceAtLeast(360)
        val targetHeight = (screenHeight / 2).coerceAtLeast(640)

        imageReader = ImageReader.newInstance(targetWidth, targetHeight, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "AryaScreenCaptureDisplay",
            targetWidth,
            targetHeight,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            handler
        )

        var hasCaptured = false

        imageReader?.setOnImageAvailableListener({ reader ->
            if (hasCaptured) return@setOnImageAvailableListener
            var image: Image? = null
            try {
                image = reader.acquireLatestImage()
                if (image != null) {
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * targetWidth

                    val bitmap = Bitmap.createBitmap(
                        targetWidth + rowPadding / pixelStride,
                        targetHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.copyPixelsFromBuffer(buffer)

                    // Crop out row padding if necessary
                    val cleanBitmap = if (rowPadding > 0) {
                        Bitmap.createBitmap(bitmap, 0, 0, targetWidth, targetHeight)
                    } else {
                        bitmap
                    }

                    // Compress to JPEG for Vision AI
                    val outputStream = ByteArrayOutputStream()
                    cleanBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val byteArray = outputStream.toByteArray()
                    val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)

                    _capturedScreenBase64.value = base64String
                    _screenCaptureStatus.value = "स्क्रीन सफलतापूर्वक कैप्चर की गई!"
                    hasCaptured = true

                    handler.postDelayed({
                        stopScreenCapture()
                    }, 500)
                }
            } catch (e: Exception) {
                _screenCaptureStatus.value = "स्क्रीन कैप्चर त्रुटि: ${e.localizedMessage}"
            } finally {
                image?.close()
            }
        }, handler)

        // Safety timeout: If no frame acquired within 4 seconds, clean up
        handler.postDelayed({
            if (!hasCaptured) {
                _screenCaptureStatus.value = "स्क्रीन कैप्चर समय समाप्त"
                stopScreenCapture()
            }
        }, 4000)
    }

    private fun stopScreenCapture() {
        try {
            virtualDisplay?.release()
            virtualDisplay = null
            imageReader?.close()
            imageReader = null
            mediaProjection?.stop()
            mediaProjection = null
        } catch (ignored: Exception) {}

        _isScreenSharingActive.value = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopScreenCapture()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "आर्या स्क्रीन शेयरिंग सेवा",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "एआई असिस्टेंट के लिए स्क्रीन कैप्चर व शेयरिंग अधिसूचना"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("आर्या AI: स्क्रीन कैप्चर सक्रिय")
            .setContentText("आपकी सहमति से स्क्रीनशॉट कैप्चर किया जा रहा है...")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
