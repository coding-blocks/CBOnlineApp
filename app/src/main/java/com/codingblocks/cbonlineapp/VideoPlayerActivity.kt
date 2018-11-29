package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.devbrackets.android.exomedia.util.MediaUtil
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import kotlinx.android.synthetic.main.activity_video_player.*

class VideoPlayerActivity : AppCompatActivity(), View.OnClickListener, OnPreparedListener {

    companion object {
        private const val KEY_PLAY_WHEN_READY = "play_when_ready"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_ID = "APKAIX3JJRW7RHDSNHGA"
        private const val POLICY_STRING = "eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9kMXFmMG96c3M0OTR4di5jbG91ZGZyb250Lm5ldC8wODg1ZGMzNi0xODNlLTQzMjUtYWY3Ni05NDlmNmI4NzMwZjAwMU9LSHR0cEludHJvbXA0LyoiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE1NDE3ODk0ODN9fX1dfQ=="
        private const val SIGNATURE = "NY1N6wkOWxDxGui2eCzHV9JswFU7PHnjHoXl8Dhz1Fv7a5sJxYzHe5tmNx9Iu0jGUotAki6+HtcsKEGycT0G3tBkp5JCCXo20KHx4ZWvQCcUGscKGmutcshHl8WtJ2gTHQ+LeNyCbpYeXWKsb64PaRYnWO2dg9GCxqAB8saZeSeDV6k6d3WnMH9HfUxCXntdcWJG1Mmtiu/UpfI6iSzwzG4YTMIwe8OvuzTwqjcXPoWnlelSIa798SOX+acWNoSQgvIfwx+mydcH7+y6EVeH9RHN5jamvj+WWh83szN+dd75J0CJNZ+xvQ5FmrUeEG+IZYYlHU/YXmwdljdcXQpkvA=="
    }


    private var shouldAutoPlay: Boolean = true
    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

    private var playWhenReady: Boolean = false
    private var currentWindow: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_video_player)
    }

    override fun onStart() {
        super.onStart()
        //TODO : Pass the url from the course content activity to this activity
        val url = intent.getStringExtra("FOLDER_NAME")
        setupVideoView(url)
    }

    private fun setupVideoView(url: String) {
        videoView.setOnPreparedListener(this)
        videoView.setVideoURI(MediaUtils.getCourseVideoUri(url, this))
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