package com.example.sunnycandy.utils

import android.content.res.Resources

object layoutUtils {
    fun getWidth(
        num : Int
    ):Int {
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels

        val scaleWidth = screenWidth / num
        return scaleWidth
    }
}