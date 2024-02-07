package com.anxer.bottomsheet

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.mutableIntStateOf

class AppNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null) {
            when (sbn.packageName) {
                "com.whatsapp" -> {
                    // Log.d("AppNotified", sbn.notification.number.toString())
                    BadgeCount.setWhatsAppBadgeCount(sbn.notification.number)
                }

                "com.instagram.android" -> {
                    BadgeCount.setInstaBadgeCount(sbn.notification.number)
                }
            }
        }

    }
}

object BadgeCount {
    private val whatsAppBadgeCount = mutableIntStateOf(0)
    private val instaBadgeCount = mutableIntStateOf(0)

    fun setWhatsAppBadgeCount(number: Int) {
        whatsAppBadgeCount.intValue = number
    }

    fun getWhatsappBadgeCount(): Int {
        return whatsAppBadgeCount.intValue
    }

    fun setInstaBadgeCount(number: Int) {
        instaBadgeCount.intValue = number
    }

    fun getInstaBadgeCount(): Int {
        return instaBadgeCount.intValue
    }
}