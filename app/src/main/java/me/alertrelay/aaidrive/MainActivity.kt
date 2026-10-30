package me.alertrelay.aaidrive

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        layout.addView(TextView(this).apply {
            text = "Copies notifications from Radarbot and Waze as normal notifications, " +
                "so AAIdrive shows them on the car screen.\n\n" +
                "Both steps are needed. AAIdrive must also have Notifications enabled and notification access."
        })
        layout.addView(Button(this).apply {
            text = "1. Allow notification access"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        })
        layout.addView(Button(this).apply {
            text = "2. Allow posting notifications"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                }
            }
        })
        setContentView(layout)
    }
}
