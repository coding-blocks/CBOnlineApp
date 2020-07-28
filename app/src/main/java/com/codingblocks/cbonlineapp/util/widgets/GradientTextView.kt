package com.codingblocks.cbonlineapp.util.widgets

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.codingblocks.cbonlineapp.R

class GradientTextView : androidx.appcompat.widget.AppCompatTextView {

    var startColor = R.color.pastel_red
    var endColor = R.color.dusty_orange

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Setting the gradient if layout is changed
        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                ContextCompat.getColor(context, startColor),
                ContextCompat.getColor(context, endColor),
                Shader.TileMode.CLAMP
            )
        }
    }
}
