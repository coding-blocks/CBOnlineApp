package com.codingblocks.cbonlineapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.DoubleClickListener
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import kotlinx.android.synthetic.main.activity_video_player.*
import com.codingblocks.cbonlineapp.utils.MyVideoControls



class VideoPlayerActivity : AppCompatActivity(), OnPreparedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(com.codingblocks.cbonlineapp.R.layout.activity_video_player)

        rootLayout.layoutTransition
                .enableTransitionType(LayoutTransition.CHANGING)

        val controls = MyVideoControls(this)
        videoView.videoControlsCore as VideoControls

        videoView.setControls(controls)

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