package com.example.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AryaDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(
            context,
            "Arya AI डिवाइस एडमिन सक्रिय: वॉयस स्क्रीन लॉक सुरक्षा चालू है",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(
            context,
            "Arya AI डिवाइस एडमिन निष्क्रिय किया गया",
            Toast.LENGTH_SHORT
        ).show()
    }
}
