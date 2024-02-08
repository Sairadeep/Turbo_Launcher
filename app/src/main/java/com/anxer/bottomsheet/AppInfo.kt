package com.anxer.bottomsheet

import android.graphics.Bitmap

data class AppInfo(
    val appPackage: String,
    val appName: String,
    val appIcon: Bitmap
) {
}