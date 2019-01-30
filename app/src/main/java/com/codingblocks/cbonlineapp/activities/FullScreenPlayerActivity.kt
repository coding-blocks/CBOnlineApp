package com.codingblocks.cbonlineapp.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.MyVideoControls
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import kotlinx.android.synthetic.main.activity_full_screen_player.*
import kotlinx.android.synthetic.main.exomedia_default_controls_mobile.view.*


class FullScreenPlayerActivity : AppCompatActivity(), OnPreparedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_full_screen_player)

        rootLayout.layoutTransition
                .enableTransitionType(LayoutTransition.CHANGING)

        val controls = MyVideoControls(this)
        videoView.setControls(controls)

        (videoView.videoControls as MyVideoControls).let {
            it.updateFullScreenButtonDrawable()
            it.fullscreenBtn.setOnClickListener {
                onBackPressed()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        val uri = intent.getStringExtra("FOLDER_NAME")
        val currentPosition = intent.getLongExtra("CURRENT_POSITION", 0)

        setupVideoView(uri, currentPosition)
    }

    private fun setupVideoView(uri: String, currentPosition: Long) {
        videoView.setOnPreparedListener(this)
        videoView.setVideoURI(Uri.parse(uri))
        videoView.seekTo(currentPosition)
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

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("CURRENT_POSITION", videoView.currentPosition)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

}