package me.alertrelay.aaidrive

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * Watches for notifications from alert apps (Radarbot, Waze) and re-posts each change
 * as a normal, dismissable, high-importance notification from this app.
 * AAIdrive already shows and pops up such notifications in the car.
 */
class AlertRelayService : NotificationListenerService() {
    companion object {
        const val CHANNEL_ID = "relayed_alerts"
        const val MIN_INTERVAL_MS = 6000L      // ignore updates that arrive faster than this
        const val TIMEOUT_MS = 60000L          // relayed notifications remove themselves
        /** Package name prefix to the label shown in the car */
        val SOURCES = mapOf(
            "com.vialsoft.radarbot" to "Radarbot",
            "com.waze" to "Waze"
        )
    }

    private val lastContent = HashMap<String, String>()
    private val lastPosted = HashMap<String, Long>()

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(CHANNEL_ID, "Relayed alerts", NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        val pkg = sbn.packageName ?: return
        if (pkg == packageName) return      // never relay our own notifications
        val label = SOURCES.entries.firstOrNull { pkg.startsWith(it.key) }?.value ?: return
        val extras = sbn.notification.extras ?: return

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = (extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
            ?: extras.getCharSequence(Notification.EXTRA_TEXT))?.toString()?.trim().orEmpty()
        if (title.isEmpty() && text.isEmpty()) return

        val content = "$title\n$text"
        if (lastContent[pkg] == content) return     // nothing changed
        val now = System.currentTimeMillis()
        val last = lastPosted[pkg]
        if (last != null && now - last < MIN_INTERVAL_MS) return   // too soon, wait for the next update

        lastContent[pkg] = content
        lastPosted[pkg] = now
        relay(pkg, label, title, text)
    }

    private fun relay(pkg: String, label: String, title: String, text: String) {
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(if (title.isNotEmpty()) "$label: $title" else label)
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setTimeoutAfter(TIMEOUT_MS)
            .build()
        getSystemService(NotificationManager::class.java).notify(pkg.hashCode(), notification)
    }
}
