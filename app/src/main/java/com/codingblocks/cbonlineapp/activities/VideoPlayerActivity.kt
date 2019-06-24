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
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.fragments.VideoDoubtFragment
import com.codingblocks.cbonlineapp.fragments.VideoNotesFragment
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOWNLOADED
import com.codingblocks.cbonlineapp.util.OnItemClickListener
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.VdoPlayerControlView
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.RunAttemptsId
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost.VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
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
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : AppCompatActivity(),
    OnItemClickListener, AnkoLogger,
    VdoPlayer.InitializationListener {

    private val attemptId by lazy {
        intent.getStringExtra(RUN_ATTEMPT_ID)
    }
    private val contentId by lazy {
        intent.getStringExtra(CONTENT_ID)
    }
    private val sectionId by lazy {
        intent.getStringExtra(SECTION_ID) ?: ""
    }
    private val videoId by lazy {
        intent.getStringExtra(VIDEO_ID)
    }
    private val download: Boolean by lazy {
        intent.getBooleanExtra(DOWNLOADED, false)
    }

    private var youtubePlayer: YouTubePlayer? = null
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private var playerControlView: VdoPlayerControlView? = null
    private lateinit var playerFragment: VdoPlayerFragment
    private var videoPlayer: VdoPlayer? = null

    private val viewModel by viewModel<VideoPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        rootLayout.layoutTransition
            .enableTransitionType(LayoutTransition.CHANGING)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        viewModel.currentOrientation = resources.configuration.orientation
        if (savedInstanceState == null) {
            setupUI()
        }
    }

    private fun setupUI() {
        val youtubeUrl = intent.getStringExtra("videoUrl")

        if (youtubeUrl != null) {
            displayYoutubeVideo.view?.visibility = View.VISIBLE
            setupYoutubePlayer(youtubeUrl)
        } else {
            displayYoutubeVideo.view?.visibility = View.GONE
            videoContainer.visibility = View.VISIBLE
            playerFragment = fragmentManager.findFragmentById(R.id.videoView) as VdoPlayerFragment
            playerFragment.videoStretchMode = VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
            playerControlView = findViewById(R.id.player_control_view)
            showControls(false)

            if (download) {
                initializePlayer()
            } else {
                setupVideoView()
            }
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
                                val notePos: Double? =
                                    if (displayYoutubeVideo.view?.visibility == View.VISIBLE)
                                        (youtubePlayer?.currentTimeMillis?.div(1000))?.toDouble()
                                    else
                                        (videoPlayer?.currentTime?.div(1000))?.toDouble()
                                notePos?.let { value -> createNote(value) }
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
            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
            }

            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                youtubePlayerInstance: YouTubePlayer?,
                p2: Boolean
            ) {
                if (!p2) {
                    youtubePlayer = youtubePlayerInstance
                    youtubePlayerInstance?.loadVideo(youtubeUrl.substring(32))
                }
            }
        }
        val youTubePlayerSupportFragment =
            supportFragmentManager.findFragmentById(R.id.displayYoutubeVideo) as YouTubePlayerSupportFragment?
        youTubePlayerSupportFragment?.initialize(BuildConfig.YOUTUBE_KEY, youtubePlayerInit)
    }

    private fun setupVideoView() {
        viewModel.getOtpProgress.observer(this) {
            if (it) {
                initializePlayer()
            } else
                toast("there was some error with starting feed, try again")
        }

        viewModel.getOtp(videoId, sectionId, attemptId)
    }

    private fun initializePlayer() {
        playerFragment.initialize(this)
    }

    override fun onInitializationSuccess(
        playerHost: VdoPlayer.PlayerHost?,
        player: VdoPlayer?,
        wasRestored: Boolean
    ) {
        videoPlayer = player
        player?.addPlaybackEventListener(playbackListener)
        playerControlView?.setPlayer(player)
        showControls(true)

        playerControlView?.setFullscreenActionListener(fullscreenToggleListener)
        playerControlView?.setControllerVisibilityListener(visibilityListener)
        // load a media to the player
        val vdoParams: VdoPlayer.VdoInitParams? = if (download) {
            VdoPlayer.VdoInitParams.createParamsForOffline(videoId)
        } else {
            VdoPlayer.VdoInitParams.Builder()
                .setOtp(viewModel.mOtp)
                .setPlaybackInfo(viewModel.mPlaybackInfo)
                .setPreferredCaptionsLanguage("en")
                .build()
        }
        player?.load(vdoParams)
    }

    override fun onInitializationFailure(p0: VdoPlayer.PlayerHost?, p1: ErrorDescription?) {
        toast(p1?.errorMsg + "")
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
        viewModel.getRunByAtemptId(attemptId).observer(this) {
            val categoryId = viewModel.getCourseById(it.crCourseId).categoryId
            val doubtDialog = AlertDialog.Builder(this).create()
            val doubtView = layoutInflater.inflate(R.layout.doubt_dialog, null)

            if (!it.premium) {
                val cannotCreateDialog = AlertDialog.Builder(this).create()
                val cannotCreateView = layoutInflater.inflate(R.layout.cannot_create_doubt_dialog, null)
                cannotCreateView.okBtn.setOnClickListener {
                    cannotCreateDialog.dismiss()
                }
                cannotCreateDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                cannotCreateDialog.setView(cannotCreateView)
                cannotCreateDialog.setCancelable(false)
                cannotCreateDialog.show()
            } else {
                doubtView.cancelBtn.setOnClickListener {
                    doubtDialog.dismiss()
                }
                doubtView.okBtn.setOnClickListener {
                    if (doubtView.titleLayout.editText?.text.toString().length < 15 || doubtView.titleLayout.editText?.text.toString().isEmpty()) {
                        doubtView.titleLayout.error = getString(R.string.doubt_title_error)
                        return@setOnClickListener
                    } else if (doubtView.descriptionLayout.editText?.text.toString().length < 20 || doubtView.descriptionLayout.editText?.text.toString().isEmpty()) {
                        doubtView.descriptionLayout.error = getString(R.string.doubt_description_error)
                        doubtView.titleLayout.error = ""
                    } else {
                        doubtView.descriptionLayout.error = ""
                        val doubt = DoubtsJsonApi()
                        doubt.body = doubtView.descriptionLayout.editText?.text.toString()
                        doubt.title = doubtView.titleLayout.editText?.text.toString()
                        doubt.category = categoryId
                        doubt.status = "PENDING"
                        doubt.postrunAttempt = RunAttemptsId(attemptId)
                        doubt.contents = ContentsId(contentId)
                        viewModel.createDoubtProgress.observer(this) { progress ->
                            if (progress)
                                doubtDialog.dismiss()
                            else {
                                doubtDialog.dismiss()
                                toast("there was some error please try again")
                            }
                        }
                        viewModel.createDoubt(doubt, attemptId)
                    }
                }

                doubtDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                doubtDialog.setView(doubtView)
                doubtDialog.setCancelable(false)
                doubtDialog.show()
            }
        }
    }

    private fun createNote(notePos: Double) {
        val noteDialog = AlertDialog.Builder(this).create()
        val noteView = layoutInflater.inflate(R.layout.doubt_dialog, null)
        noteView.descriptionLayout.visibility = View.GONE
        noteView.title.text = resources.getString(R.string.create_a_note)
        noteView.okBtn.text = resources.getString(R.string.create_note)

        noteView.cancelBtn.setOnClickListener {
            noteDialog.dismiss()
        }
        noteView.okBtn.setOnClickListener {
            if (noteView.titleLayout.editText?.text.toString().isEmpty()) {
                noteView.titleLayout.error = "Note Cannot Be Empty."
                return@setOnClickListener
            } else {
                noteView.descriptionLayout.error = ""
                val note = Notes()
                note.text = noteView.titleLayout.editText?.text.toString()
                note.duration = notePos
                note.content = ContentsId(contentId)
                note.runAttempt = RunAttemptsId(attemptId)

                viewModel.createNoteProgress.observer(this) {
                    if (it) noteDialog.dismiss()
                    else toast("there was some errror with creating notes, try again")
                }
                viewModel.createNote(note, attemptId)
            }
        }

        noteDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        noteDialog.setView(noteView)
        noteDialog.setCancelable(false)
        noteDialog.show()
    }

    override fun onBackPressed() {
        if (viewModel.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            showFullScreen(false)
            playerControlView?.setFullscreenState(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun showFullScreen(show: Boolean) {
        showSystemUi(show)
        requestedOrientation = if (show) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            // go to landscape orientation for fullscreen mode
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            // go to portrait orientation
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    private fun showSystemUi(show: Boolean) {
        if (show) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
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
            onBackPressed()
        }

        override fun onError(p0: VdoPlayer.VdoInitParams?, p1: ErrorDescription?) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            viewModel.playWhenReady = playWhenReady
        }
    }
    private val fullscreenToggleListener =
        VdoPlayerControlView.FullscreenActionListener { enterFullscreen ->
            showFullScreen(enterFullscreen)
            true
        }
    private val visibilityListener =
        VdoPlayerControlView.ControllerVisibilityListener { visibility ->
            if (viewModel.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (visibility != View.VISIBLE) {
//                    showSystemUi(false)
                }
            }
        }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val newOrientation = newConfig.orientation
        val oldOrientation = viewModel.currentOrientation
        viewModel.currentOrientation = newOrientation
        super.onConfigurationChanged(newConfig)
        when (newOrientation) {
            oldOrientation -> {
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                // hide other views
                player_tabs.visibility = View.GONE
                pagerFrame.visibility = View.GONE
                playerControlView?.fitsSystemWindows = true

                if (::playerFragment.isInitialized) {
                    val paramsFragment: RelativeLayout.LayoutParams =
                        playerFragment.view?.layoutParams as RelativeLayout.LayoutParams
                    paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_START)
                    paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_END)
                }

                // hide system windows
                showControls(false)
            }
            else -> {
                // show other views
                player_tabs.visibility = View.VISIBLE
                pagerFrame.visibility = View.VISIBLE

                playerControlView?.fitsSystemWindows = false

                if (::playerFragment.isInitialized) {
                    val paramsFragment: RelativeLayout.LayoutParams =
                        playerFragment.view?.layoutParams as RelativeLayout.LayoutParams
                    paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
                    paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_START)
                    paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_END)
                }
                playerControlView?.setPadding(0, 0, 0, 0)
                // show system windows
            }
        }
    }
}
