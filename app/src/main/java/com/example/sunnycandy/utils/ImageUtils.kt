package com.example.sunnycandy.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
object ImageUtils {
    fun scaleDrawable(
        drawable: Drawable,
        targetWidth: Int? = null,
        targetHeight: Int? = null
    ): Drawable {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val width = bitmap.width
            val height = bitmap.height

            val scaleWidth = targetWidth?.toFloat()?.div(width)
            val scaleHeight = targetHeight?.toFloat()?.div(height)

            // 计算缩放比例
            val scale = scaleWidth ?: scaleHeight ?: 1.0f

            val matrix = Matrix()
            matrix.postScale(scale, scale)

            val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            return BitmapDrawable(Resources.getSystem(), scaledBitmap)
        }
        return drawable
    }
    fun scaleHomeIcon(
        drawable: Drawable,
    ): Drawable {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val width = bitmap.width
            val height = bitmap.height
            val displayMetrics = DisplayMetrics()
//            val screenWidth = displayMetrics.widthPixels
            val scaleWidth = displayMetrics.widthPixels/2
//            val scaleHeight = targetHeight?.toFloat()?.div(height)

            // 计算缩放比例
            val scale = scaleWidth.toFloat()

            val matrix = Matrix()
            matrix.postScale(scale, scale)

            val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            return BitmapDrawable(Resources.getSystem(), scaledBitmap)
        }
        return drawable
    }
}
