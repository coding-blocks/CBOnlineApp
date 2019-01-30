package com.codingblocks.cbonlineapp.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.cbonlineapp.utils.MyVideoControls
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.exomedia_default_controls_mobile.view.*
import org.jetbrains.anko.AnkoLogger


class VideoPlayerActivity : AppCompatActivity(), OnPreparedListener, AnkoLogger {

    private var pos: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(com.codingblocks.cbonlineapp.R.layout.activity_video_player)

        rootLayout.layoutTransition
                .enableTransitionType(LayoutTransition.CHANGING)

        val controls = MyVideoControls(this)
        videoView.setControls(controls)
        (videoView.videoControls as MyVideoControls).let {
            it.fullscreenBtn.setOnClickListener {
                val i = Intent(this, VideoPlayerFullScreenActivity::class.java)
                i.putExtra("FOLDER_NAME", videoView.videoUri.toString())
                i.putExtra("CURRENT_POSITION", videoView.currentPosition)
                startActivityForResult(i, 1)
            }

        }

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
        videoView.seekTo(pos?:0)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == -1) {
           pos =  data?.getLongExtra("CURRENT_POSITION", 0)
        }
    }

}