package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.codingblocks.cbonlineapp.R
import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile


class MyVideoControls @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VideoControlsMobile(context, attrs, defStyleAttr) {

    private lateinit var rewind: ImageButton
    private lateinit var fastfwd: ImageButton
    private lateinit var fullScreen: ImageButton
    private lateinit var playback: ImageView


    override fun setup(context: Context) {
        super.setup(context)
        retrieveViews()
    }

    override fun retrieveViews() {
        super.retrieveViews()
        rewind = findViewById(R.id.exomedia_controls_rewind)
        fastfwd = findViewById(R.id.exomedia_controls_fast_forward)
        playback = findViewById(R.id.playback_speed)
        fullScreen = findViewById(R.id.fullscreenBtn)

    }

    override fun registerListeners() {
        super.registerListeners()
        rewind.setOnClickListener {
            videoView!!.seekTo(videoView!!.currentPosition - 10000)

        }
        fastfwd.setOnClickListener {
            videoView!!.seekTo(videoView!!.currentPosition + 10000)
        }
        playback.setOnClickListener {
            showSpeedDialog()
        }
    }

    fun updateFullScreenButtonDrawable(){
        fullScreen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_fullscreen_exit_black_24dp))
    }

    private val playbackSpeeds = floatArrayOf(0.5f, 0.75f, 1f, 1.5f, 2f)
    private val speedString = arrayOf("0.5x", "0.75x", "1x", "1.5x", "2x")

    private fun showSpeedDialog() {
        AlertDialog.Builder(context)
                .setTitle("Select Playback speed")
                .setSingleChoiceItems(speedString, playbackSpeeds.indexOf(videoView!!.playbackSpeed)) { it, which ->
                    videoView!!.playbackSpeed = playbackSpeeds[which]
                    it.cancel()
                }
                .show()
    }


}
