package com.example.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.PixelCopy
import android.view.View
import java.io.ByteArrayOutputStream

/**
 * ScreenCaptureHelper for Google Gemini-style Live Screen Sharing & Screen Vision.
 */
object ScreenCaptureHelper {

    /**
     * Captures a screenshot of the current Activity window safely via PixelCopy (API 26+) or view drawing cache.
     */
    fun captureCurrentWindow(activity: Activity, onCaptured: (Bitmap?, String?) -> Unit) {
        val window = activity.window
        val view = window.decorView.rootView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bitmap = Bitmap.createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            val location = IntArray(2)
            view.getLocationInWindow(location)
            val rect = Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)

            try {
                PixelCopy.request(
                    window,
                    rect,
                    bitmap,
                    { result ->
                        if (result == PixelCopy.SUCCESS) {
                            val base64 = bitmapToBase64(bitmap)
                            onCaptured(bitmap, base64)
                        } else {
                            onCaptured(null, null)
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (_: Exception) {
                fallbackCapture(view, onCaptured)
            }
        } else {
            fallbackCapture(view, onCaptured)
        }
    }

    private fun fallbackCapture(view: View, onCaptured: (Bitmap?, String?) -> Unit) {
        try {
            val bitmap = Bitmap.createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            view.draw(canvas)
            val base64 = bitmapToBase64(bitmap)
            onCaptured(bitmap, base64)
        } catch (_: Exception) {
            onCaptured(null, null)
        }
    }

    fun uriToBase64(context: Context, uri: Uri): Pair<Bitmap?, String?> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (bitmap != null) {
                bitmap to bitmapToBase64(bitmap)
            } else null to null
        } catch (_: Exception) {
            null to null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
