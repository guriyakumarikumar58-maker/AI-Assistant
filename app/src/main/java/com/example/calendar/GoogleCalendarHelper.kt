package com.example.calendar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast

data class CalendarEventItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val location: String = "घर / ऑनलाइन",
    val startMillis: Long,
    val endMillis: Long = startMillis + (60 * 60 * 1000L), // 1 hour default
    val isGoogleSynced: Boolean = true
)

object GoogleCalendarHelper {

    /**
     * Directly inserts an event into Google Calendar on the device using standard Android Intent.
     */
    fun openAddEventInGoogleCalendar(
        context: Context,
        title: String,
        description: String = "",
        location: String = "",
        startMillis: Long = System.currentTimeMillis() + 3600000L,
        endMillis: Long = startMillis + 3600000L
    ) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Google Calendar खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Directly opens the user's Google Calendar app on their Android device.
     */
    /**
     * Directly opens the user's Calendar app using Android system intents.
     * Never resorts to website links or web search.
     */
    fun openGoogleCalendarApp(context: Context) {
        val pm = context.packageManager
        // 1. Try Google Calendar package launch intent
        val gCalIntent = pm.getLaunchIntentForPackage("com.google.android.calendar")
        if (gCalIntent != null) {
            gCalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(gCalIntent)
            return
        }
        // 2. Try generic Calendar content URI intent
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("content://com.android.calendar/time")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        } catch (_: Exception) {}

        // 3. Try standard Android Calendar Category intent
        try {
            val categoryIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALENDAR).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(categoryIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "कैलेंडर ऐप डिवाइस पर नहीं मिला", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Directly share a note to Google Keep or other apps.
     */
    fun shareNoteToExternalApp(context: Context, title: String, content: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "$title\n\n$content\n\n— Arya AI Smart Notes")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Google Keep या अन्य ऐप में शेयर करें"))
        } catch (e: Exception) {
            Toast.makeText(context, "शेयर करने में असमर्थ", Toast.LENGTH_SHORT).show()
        }
    }
}
