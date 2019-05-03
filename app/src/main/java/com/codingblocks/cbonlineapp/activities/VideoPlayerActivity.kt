package com.codingblocks.cbonlineapp.activities

import android.animation.LayoutTransition
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.fragments.VideoDoubtFragment
import com.codingblocks.cbonlineapp.fragments.VideoNotesFragment
import com.codingblocks.cbonlineapp.util.OnItemClickListener
import com.codingblocks.cbonlineapp.util.VdoPlayerControlView
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.RunAttemptsModel
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayerFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_video_player.displayYoutubeVideo
import kotlinx.android.synthetic.main.activity_video_player.pagerFrame
import kotlinx.android.synthetic.main.activity_video_player.player_tabs
import kotlinx.android.synthetic.main.activity_video_player.player_viewpager
import kotlinx.android.synthetic.main.activity_video_player.rootLayout
import kotlinx.android.synthetic.main.activity_video_player.videoContainer
import kotlinx.android.synthetic.main.activity_video_player.videoFab
import kotlinx.android.synthetic.main.doubt_dialog.view.cancelBtn
import kotlinx.android.synthetic.main.doubt_dialog.view.descriptionLayout
import kotlinx.android.synthetic.main.doubt_dialog.view.okBtn
import kotlinx.android.synthetic.main.doubt_dialog.view.title
import kotlinx.android.synthetic.main.doubt_dialog.view.titleLayout
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlin.concurrent.thread
import com.vdocipher.aegis.media.Track



class VideoPlayerActivity : AppCompatActivity(),
    OnItemClickListener, AnkoLogger {
    private var youtubePlayer: YouTubePlayer? = null
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private lateinit var videoPlayerPlayerInit: VdoPlayer.InitializationListener
    private var playerControlView: VdoPlayerControlView? = null
    private var playerFragment: VdoPlayerFragment? = null
    private var videoPlayer: VdoPlayer? = null
    private lateinit var attemptId: String
    private lateinit var contentId: String
    private var pos: Long? = 0
    private var playWhenReady = false
    private var currentOrientation: Int = 0
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
    private val doubtsDao by lazy {
        database.doubtsDao()
    }
    private val notesDao by lazy {
        database.notesDao()
    }
    private val courseDao by lazy {
        database.courseDao()
    }
    private val runDao by lazy {
        database.courseRunDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.codingblocks.cbonlineapp.R.layout.activity_video_player)
        rootLayout.layoutTransition
            .enableTransitionType(LayoutTransition.CHANGING)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.setOnSystemUiVisibilityChangeListener(uiVisibilityListener)

        currentOrientation = resources.configuration.orientation
        val url = intent.getStringExtra("FOLDER_NAME")
        val youtubeUrl = intent.getStringExtra("videoUrl")
        attemptId = intent.getStringExtra("attemptId")
        contentId = intent.getStringExtra("contentId")
        val downloaded = intent.getBooleanExtra("downloaded", false)

        if (youtubeUrl != null) {
            displayYoutubeVideo.view?.visibility = View.VISIBLE
            setupYoutubePlayer(youtubeUrl)
        } else {
            displayYoutubeVideo.view?.visibility = View.GONE
            videoContainer.visibility = View.VISIBLE
            setupVideoView(url, downloaded)
            playerFragment = supportFragmentManager.findFragmentById(R.id.videoView) as VdoPlayerFragment?
            playerControlView = findViewById(R.id.player_control_view)
        }

        setupViewPager(attemptId)
    }

    private fun setupViewPager(attemptId: String) {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(VideoDoubtFragment.newInstance(attemptId), "Doubts")
        adapter.add(VideoNotesFragment.newInstance(attemptId), "Notes")

        player_viewpager.adapter = adapter
        player_tabs.setupWithViewPager(player_viewpager)
        player_viewpager.offscreenPageLimit = 2
        player_viewpager.addOnPageChangeListener(
            pageChangeCallback(
                fnSelected = { position ->
                    when (position) {
                        0 -> {
                            videoFab.setOnClickListener {
                                createDoubt()
                            }
                        }
                        1 -> {
                            videoFab.setOnClickListener {
                                val notePos: Double =
                                    if (displayYoutubeVideo.view?.visibility == View.VISIBLE)
                                        (youtubePlayer?.currentTimeMillis!! / 1000).toDouble()
                                    else
                                        (videoPlayer?.currentTime!! / 1000).toDouble()
                                createNote(notePos)
                            }
                        }
                    }
                },
                fnState = {},
                fnScrolled = { _: Int, _: Float, _: Int ->
                })
        )
    }

    private fun setupYoutubePlayer(youtubeUrl: String) {
        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
            }

            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, youtubePlayerInstance: YouTubePlayer?, p2: Boolean) {
                if (!p2) {
                    youtubePlayer = youtubePlayerInstance
                    youtubePlayerInstance?.loadVideo(youtubeUrl.substring(32))
                }
            }
        }
        val youTubePlayerSupportFragment = supportFragmentManager.findFragmentById(R.id.displayYoutubeVideo) as YouTubePlayerSupportFragment?
        youTubePlayerSupportFragment!!.initialize(BuildConfig.YOUTUBE_KEY, youtubePlayerInit)
    }

    private fun setupVideoView(videoId: String, downloaded: Boolean) {
        showControls(false)
        Clients.api.getVideoDownloadKey(videoId, contentId, attemptId).enqueue(retrofitCallback { throwable, response ->
            response?.let {
                if (it.isSuccessful) {
                    val otp = it.body()?.get("otp") as String
                    val playbackInfo = it.body()?.get("playbackInfo") as String
                    initializePlayer(otp, playbackInfo)
                }
            }
        })
    }

    private fun initializePlayer(mOtp: String, mPlaybackInfo: String) {
        videoPlayerPlayerInit = object : VdoPlayer.InitializationListener {
            override fun onInitializationSuccess(playerHost: VdoPlayer.PlayerHost?, player: VdoPlayer?, wasRestored: Boolean) {
                videoPlayer = player
                player?.addPlaybackEventListener(playbackListener)
                playerControlView?.setPlayer(player)
                showControls(true)

                playerControlView?.setFullscreenActionListener(fullscreenToggleListener)
                playerControlView?.setControllerVisibilityListener(visibilityListener)
                // load a media to the player
                val vdoParams = VdoPlayer.VdoInitParams.Builder()
                    .setOtp(mOtp)
                    .setPlaybackInfo(mPlaybackInfo)
                    .setPreferredCaptionsLanguage("en")
                    .build()
                player?.load(vdoParams)
            }

            override fun onInitializationFailure(p0: VdoPlayer.PlayerHost?, p1: ErrorDescription?) {
            }
        }

        playerFragment?.initialize(videoPlayerPlayerInit)
    }

    private fun showControls(show: Boolean) {
        if (show) {
            playerControlView?.show()
        } else {
            playerControlView?.hide()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onItemClick(position: Int, id: String) {
        if (contentId == id) {
            if (displayYoutubeVideo.view?.visibility == View.VISIBLE)
                youtubePlayer?.seekToMillis(position * 1000)
            else
                videoPlayer?.seekTo(position.toLong() * 1000)
        }
    }

    private fun createDoubt() {
        runDao.getRunByAtemptId(attemptId).observe(this, Observer<CourseRun> {
            val categoryId = courseDao.getCourse(it?.crCourseId!!).categoryId
            val doubtDialog = AlertDialog.Builder(this).create()
            val doubtView = layoutInflater.inflate(R.layout.doubt_dialog, null)
            doubtView.cancelBtn.setOnClickListener {
                doubtDialog.dismiss()
            }
            doubtView.okBtn.setOnClickListener {
                if (doubtView.titleLayout.editText!!.text.length < 15 || doubtView.titleLayout.editText!!.text.isEmpty()) {
                    doubtView.titleLayout.error = "Title length must be atleast 15 characters."
                    return@setOnClickListener
                } else if (doubtView.descriptionLayout.editText!!.text.length < 20 || doubtView.descriptionLayout.editText!!.text.isEmpty()) {
                    doubtView.descriptionLayout.error = "Description length must be atleast 20 characters."
                    doubtView.titleLayout.error = ""
                } else {
                    doubtView.descriptionLayout.error = ""
                    val doubt = DoubtsJsonApi()
                    doubt.body = doubtView.descriptionLayout.editText!!.text.toString()
                    doubt.title = doubtView.titleLayout.editText!!.text.toString()
                    doubt.category = categoryId
                    val runAttempts = RunAttemptsModel() // type run-attempts
                    val contents = Contents() // type contents
                    runAttempts.id = attemptId
                    contents.id = contentId
                    doubt.status = "PENDING"
                    doubt.postrunAttempt = runAttempts
                    doubt.content = contents
                    Clients.onlineV2JsonApi.createDoubt(doubt).enqueue(retrofitCallback { throwable, response ->
                        response?.body().let {
                            doubtDialog.dismiss()
                            thread {
                                doubtsDao.insert(
                                    DoubtsModel(
                                        it!!.id
                                            ?: "", it.title, it.body, it.content?.id
                                        ?: "", it.status, it.runAttempt?.id ?: ""
                                    )
                                )
                            }
                        }
                    })
                }
            }

            doubtDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            doubtDialog.setView(doubtView)
            doubtDialog.setCancelable(false)
            doubtDialog.show()
        })
    }

    private fun createNote(notePos: Double) {
        val noteDialog = AlertDialog.Builder(this).create()
        val noteView = layoutInflater.inflate(R.layout.doubt_dialog, null)
        noteView.descriptionLayout.visibility = View.GONE
        noteView.title.text = "Create A Note"
        noteView.okBtn.text = "Create Note"


        noteView.cancelBtn.setOnClickListener {
            noteDialog.dismiss()
        }
        noteView.okBtn.setOnClickListener {
            if (noteView.titleLayout.editText!!.text.isEmpty()) {
                noteView.titleLayout.error = "Note Cannot Be Empty."
                return@setOnClickListener
            } else {
                noteView.descriptionLayout.error = ""
                val note = Notes()
                note.text = noteView.titleLayout.editText!!.text.toString()
                note.duration = notePos
                val runAttempts = RunAttemptsModel() // type run_attempts
                val contents = Contents() // type contents
                runAttempts.id = attemptId
                contents.id = contentId
                note.runAttempt = runAttempts
                note.content = contents
                Clients.onlineV2JsonApi.createNote(note).enqueue(retrofitCallback { throwable, response ->
                    response?.body().let {
                        noteDialog.dismiss()
                        if (response?.isSuccessful!!)
                            try {
                                notesDao.insert(
                                    NotesModel(
                                        it!!.id
                                            ?: "", it.duration ?: 0.0, it.text ?: "", it.content?.id
                                        ?: "", attemptId, it.createdAt
                                        ?: "", it.deletedAt
                                        ?: ""
                                    )
                                )
                            } catch (e: Exception) {
                                info { "error" + e.localizedMessage }
                            }
                    }
                })
            }
        }

        noteDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        noteDialog.setView(noteView)
        noteDialog.setCancelable(false)
        noteDialog.show()
    }


    override fun onBackPressed() {
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            showFullScreen(false)
            playerControlView?.setFullscreenState(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun showFullScreen(show: Boolean) {
        requestedOrientation = if (show) {
            // go to landscape orientation for fullscreen mode
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            // go to portrait orientation
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    private fun showSystemUi(show: Boolean) {
        if (!show) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
    private val playbackListener = object : VdoPlayer.PlaybackEventListener {
        override fun onTracksChanged(p0: Array<out Track>?, p1: Array<out Track>?) {
        }
        override fun onSeekTo(p0: Long) {
        }
        override fun onLoading(p0: VdoPlayer.VdoInitParams?) {
        }
        override fun onLoaded(p0: VdoPlayer.VdoInitParams?) {
        }
        override fun onBufferUpdate(p0: Long) {
        }
        override fun onProgress(p0: Long) {
        }
        override fun onPlaybackSpeedChanged(p0: Float) {
        }
        override fun onLoadError(p0: VdoPlayer.VdoInitParams?, p1: ErrorDescription?) {
        }
        override fun onMediaEnded(p0: VdoPlayer.VdoInitParams?) {
        }
        override fun onError(p0: VdoPlayer.VdoInitParams?, p1: ErrorDescription?) {
        }
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            this@VideoPlayerActivity.playWhenReady = playWhenReady
        }


    }
    private val uiVisibilityListener = View.OnSystemUiVisibilityChangeListener { visibility ->
        // show player controls when system ui is showing
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            showControls(true)
        }
    }
    private val fullscreenToggleListener = VdoPlayerControlView.FullscreenActionListener { enterFullscreen ->
        showFullScreen(enterFullscreen)
        true
    }
    private val visibilityListener = VdoPlayerControlView.ControllerVisibilityListener { visibility ->
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (visibility != View.VISIBLE) {
                showSystemUi(false)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val newOrientation = newConfig.orientation
        val oldOrientation = currentOrientation
        currentOrientation = newOrientation
        info {
            "new orientation " + if (newOrientation == Configuration.ORIENTATION_PORTRAIT)
                "PORTRAIT"
            else if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) "LANDSCAPE" else "UNKNOWN"
        }
        super.onConfigurationChanged(newConfig)
        when (newOrientation) {
            oldOrientation -> {
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                // hide other views
                player_tabs.visibility = View.GONE
                pagerFrame.visibility = View.GONE
                findViewById<View>(R.id.videoContainer).layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                playerControlView?.fitsSystemWindows = true
                // hide system windows
                showSystemUi(false)
                showControls(false)
            }
            else -> {
                // show other views
                player_tabs.visibility = View.VISIBLE
                pagerFrame.visibility = View.VISIBLE
                videoContainer.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                playerControlView?.fitsSystemWindows = false
                playerControlView?.setPadding(0, 0, 0, 0)
                // show system windows
                showSystemUi(true)
            }
        }
    }
}
