package com.codingblocks.cbonlineapp.utils

import android.content.Context
import android.support.annotation.IntRange
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import com.codingblocks.cbonlineapp.R
import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile
import com.devbrackets.android.exomedia.util.TimeFormatUtil


class MyVideoControls @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VideoControlsMobile(context, attrs, defStyleAttr) {
    override fun retrieveViews() {
        val imageButton = findViewById<ImageButton>(R.id.doubtBtn)
    }

    override fun getLayoutResource(): Int {
        return com.codingblocks.cbonlineapp.R.layout.exomedia_default_controls_mobile
    }

    override fun setPosition(@IntRange(from = 0) position: Long) {
        currentTimeTextView.text = TimeFormatUtil.formatMs(position)
        seekBar.progress = position.toInt()
    }

    override fun setDuration(@IntRange(from = 0) duration: Long) {
        if (duration != seekBar.max.toLong()) {
            endTimeTextView.text = TimeFormatUtil.formatMs(duration)
            seekBar.max = duration.toInt()
        }
    }

    override fun setup(context: Context) {
        View.inflate(context, layoutResource, this)
        retrieveViews()


    }
}
