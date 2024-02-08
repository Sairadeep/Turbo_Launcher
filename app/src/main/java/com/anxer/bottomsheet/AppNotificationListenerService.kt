package com.anxer.bottomsheet

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.widget.Toast
import androidx.compose.runtime.mutableStateMapOf

class AppNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null) {
            BadgeCount.setAppBadgeCount(sbn.packageName, sbn.notification.number)
            Toast.makeText(
                this,
                "${BadgeCount.getWhatsappBadgeCount(sbn.packageName)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn != null) {
            BadgeCount.setAppBadgeCount(sbn.packageName, 0)
        }
    }
}


object BadgeCount {

    private val BadgeCount = mutableStateMapOf<String, Int>()

    fun setAppBadgeCount(packageName: String, number: Int) {
        BadgeCount[packageName] = number
    }

    fun getWhatsappBadgeCount(packageName: String): Int {
        return BadgeCount[packageName] ?: 0
    }
}