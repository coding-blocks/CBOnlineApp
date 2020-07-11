package com.codingblocks.cbonlineapp.util.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.codingblocks.cbonlineapp.R

class GradientTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    var startColor: Int = R.color.pastel_red
    var endColor: Int = R.color.dusty_orange


    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView)
        this.startColor = typedArray.getResourceId(R.styleable.GradientTextView_startColor, DEFAULT_START_COLOR)
        this.endColor = typedArray.getResourceId(R.styleable.GradientTextView_endColor, DEFAULT_END_COLOR)
        typedArray.recycle()

    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Setting the gradient if lay
        // out is changed
        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                ContextCompat.getColor(context, startColor),
                ContextCompat.getColor(context, endColor),
                Shader.TileMode.CLAMP
            )
        }
    }

    companion object {
        private const val DEFAULT_START_COLOR = R.color.pastel_red
        private const val DEFAULT_END_COLOR = R.color.dusty_orange
    }
}
