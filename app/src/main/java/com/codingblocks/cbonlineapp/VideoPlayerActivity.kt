package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : AppCompatActivity(), View.OnClickListener, OnPreparedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_video_player)
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

    override fun onClick(v: View?) {

    }

    override fun onStop() {
        super.onStop()
        videoView.release()
    }

}