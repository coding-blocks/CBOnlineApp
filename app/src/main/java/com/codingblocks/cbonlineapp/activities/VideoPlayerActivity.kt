package com.codingblocks.cbonlineapp.activities

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.cbonlineapp.utils.MyVideoControls
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import kotlinx.android.synthetic.main.activity_video_player.*


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){

            data?.getLongExtra("CURRENT_POSITION",0)?.let { videoView.seekTo(it) }
        }
    }

}