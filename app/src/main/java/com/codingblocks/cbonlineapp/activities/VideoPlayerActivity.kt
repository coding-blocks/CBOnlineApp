package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : AppCompatActivity(), OnPreparedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_video_player)

        playback_speed.setOnClickListener {
            showSpeedDialog()
        }

        fastFw.setOnClickListener {
            videoView.seekTo(videoView.currentPosition + 10000)
        }

        rewind.setOnClickListener {
            videoView.seekTo(videoView.currentPosition - 10000)
        }

    }

    private val playbackSpeeds = floatArrayOf(0.5f, 0.75f, 1f, 1.5f, 2f)
    private val speedString = arrayOf("0.5x", "0.75x", "1x", "1.5x", "2x")

    private fun showSpeedDialog() {
        AlertDialog.Builder(this)
                .setTitle("Select Playback speed")
                .setSingleChoiceItems(speedString, playbackSpeeds.indexOf(videoView.playbackSpeed)) { it, which ->
                    videoView.playbackSpeed = playbackSpeeds[which]
                    it.cancel()
                }
                .show()
    }

    override fun onStart() {
        super.onStart()
        val url = intent.getStringExtra("FOLDER_NAME")
        setupVideoView(url)
    }

    private fun setupVideoView(url: String) {
        videoView.setOnPreparedListener(this)
        videoView.setVideoURI(MediaUtils.getCourseVideoUri(url, this))
        videoView.setOnCompletionListener {
            finish()
        }
    }

    override fun onPrepared() {
        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
    }

}