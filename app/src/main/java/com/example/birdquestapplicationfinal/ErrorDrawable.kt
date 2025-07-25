package com.example.birdquestapplicationfinal

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class ErrorDrawable (context: Context) : Drawable() {
    private val drawable: Drawable

    init {
        // Initialize the drawable
        drawable = ContextCompat.getDrawable(context, R.drawable.custom_error)!!
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        val desiredWidth = 65
        val desiredHeight = 65

        // Calculate the scale factor and then scale
        val scaleFactor = (desiredWidth.toFloat() / drawable.intrinsicWidth).coerceAtMost(desiredHeight.toFloat() / drawable.intrinsicHeight)
        val scaledWidth = (drawable.intrinsicWidth * scaleFactor).toInt()
        val scaledHeight = (drawable.intrinsicHeight * scaleFactor).toInt()

        drawable.setBounds(-130, -50, -130 + scaledWidth, scaledHeight - 50)
    }

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        drawable.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        drawable.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}