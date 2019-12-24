package com.codingblocks.cbonlineapp.mycourse.player

import android.animation.LayoutTransition
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.OnItemClickListener
import com.codingblocks.cbonlineapp.commons.SheetAdapter
import com.codingblocks.cbonlineapp.commons.SheetItem
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.player.doubts.VideoDoubtFragment
import com.codingblocks.cbonlineapp.mycourse.player.notes.VideoNotesFragment
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOWNLOADED
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.widgets.VdoPlayerControlView
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost.VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.bottom_sheet_mycourses.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : AppCompatActivity(), OnItemClickListener, AnkoLogger, VdoPlayer.InitializationListener {

    private val viewModel by viewModel<VideoPlayerViewModel>()

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
    private val dialog by lazy { BottomSheetDialog(this) }


    private var youtubePlayer: YouTubePlayer? = null
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private lateinit var playerControlView: VdoPlayerControlView
    private lateinit var playerFragment: VdoPlayerSupportFragment
    private var videoPlayer: VdoPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        setUpBottomSheet()
        rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        viewModel.currentOrientation = resources.configuration.orientation
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        setupUI()
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_note, null)
        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
    }

    private fun setupUI() {
        val youtubeUrl = intent.getStringExtra("videoUrl")

        if (youtubeUrl != null) {
            displayYoutubeVideo.view?.visibility = View.VISIBLE
            setupYoutubePlayer(youtubeUrl)
        } else {
            displayYoutubeVideo.view?.visibility = View.GONE
            videoContainer.visibility = View.VISIBLE
            playerFragment = supportFragmentManager.findFragmentById(R.id.videoView) as VdoPlayerSupportFragment
            playerFragment.videoStretchMode = VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
            playerControlView = findViewById(R.id.player_control_view)
            showControls(false)

            if (download) {
                initializePlayer()
            } else {
                setupVideoView()
            }
        }
        videoFab.setOnClickListener {
            dialog.show()
        }
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(VideoDoubtFragment(), "Doubts")
        adapter.add(VideoNotesFragment(), "Notes")

        playerViewPager.adapter = adapter
        playerTabs.setupWithViewPager(playerViewPager)
        playerViewPager.offscreenPageLimit = 2
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
                    val url = if (youtubeUrl.split("=").size == 2) youtubeUrl.split("=")[1]
                    else {
                        MediaUtils.getYotubeVideoId(youtubeUrl)
                    }
                    youtubePlayerInstance?.loadVideo(url)
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

//        viewModel.getOtp(videoId, sectionId, attemptId)
    }

    private fun initializePlayer() {
        playerFragment.initialize(this)
    }

    override fun onStart() {
        super.onStart()
        val data = this.intent.data
        if (data != null && data.isHierarchical) {
            setupUI()
        }
    }

    override fun onInitializationSuccess(
        playerHost: VdoPlayer.PlayerHost?,
        player: VdoPlayer?,
        wasRestored: Boolean
    ) {
        videoPlayer = player
        player?.addPlaybackEventListener(playbackListener)
        playerControlView.apply {
            setPlayer(player)
            setFullscreenActionListener(fullscreenToggleListener)
            setControllerVisibilityListener(visibilityListener)
//            playNextButton.setOnClickListener {
//                countDownTimer.cancel()
//                viewModel.getNextVideo(contentId, sectionId, attemptId).observer(this@VideoPlayerActivity) {
//                    when (it.contentable) {
//                        LECTURE -> {
//                            startActivity(
//                                intentFor<VideoPlayerActivity>(
//                                    VIDEO_ID to it.contentLecture.lectureId,
//                                    RUN_ATTEMPT_ID to it.attempt_id,
//                                    CONTENT_ID to it.ccid,
//                                    SECTION_ID to sectionId,
//                                    DOWNLOADED to it.contentLecture.isDownloaded
//                                ))
//                            finish()
//                        }
//                        VIDEO -> {
//                            startActivity(intentFor<VideoPlayerActivity>(
//                                VIDEO_URL to it.contentVideo.videoUrl,
//                                RUN_ATTEMPT_ID to it.attempt_id,
//                                CONTENT_ID to it.ccid
//                            ).singleTop())
//                            finish()
//                        }
//                    }
//                }
//            }
        }
        showControls(true)

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
        player?.apply {
            load(vdoParams)
        }
    }

    override fun onInitializationFailure(p0: VdoPlayer.PlayerHost?, p1: ErrorDescription?) {
        toast(p1?.errorMsg + "")
    }

    private fun showControls(show: Boolean) {
        if (show) {
            playerControlView.show()
        } else {
            playerControlView.hide()
        }
    }

    override fun onItemClick(position: Int, id: String) {
        if (contentId == id) {
            if (displayYoutubeVideo.view?.visibility == View.VISIBLE)
                youtubePlayer?.seekToMillis(position * 1000)
            else
                videoPlayer?.seekTo(position.toLong() * 1000)
        }
    }

//    private fun createDoubt() {
//        viewModel.getRunByAtemptId(attemptId).observer(this) {
//            val categoryId = viewModel.getCourseById(it.crCourseId).categoryId
//            val doubtDialog = AlertDialog.Builder(this).create()
//            val doubtView = layoutInflater.inflate(R.layout.doubt_dialog, null)
//
//            if (!it.premium) {
//                val cannotCreateDialog = AlertDialog.Builder(this).create()
//                val cannotCreateView = layoutInflater.inflate(R.layout.cannot_create_doubt_dialog, null)
//                cannotCreateView.okBtn.setOnClickListener {
//                    cannotCreateDialog.dismiss()
//                }
//                cannotCreateDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//                cannotCreateDialog.setView(cannotCreateView)
//                cannotCreateDialog.setCancelable(false)
//                cannotCreateDialog.show()
//            } else {
//                doubtView.cancelBtn.setOnClickListener {
//                    doubtDialog.dismiss()
//                }
//                doubtView.okBtn.setOnClickListener {
//                    if (doubtView.titleLayout.editText?.text.toString().length < 15 || doubtView.titleLayout.editText?.text.toString().isEmpty()) {
//                        doubtView.titleLayout.error = getString(R.string.doubt_title_error)
//                        return@setOnClickListener
//                    } else if (doubtView.descriptionLayout.editText?.text.toString().length < 20 || doubtView.descriptionLayout.editText?.text.toString().isEmpty()) {
//                        doubtView.descriptionLayout.error = getString(R.string.doubt_description_error)
//                        doubtView.titleLayout.error = ""
//                    } else {
////                        doubtView.descriptionLayout.error = ""
////                        val doubt = Doubts(
////                            doubtView.descriptionLayout.editText?.text.toString(),
////                            doubtView.titleLayout.editText?.text.toString(),
////                            "PENDING",
////                            categoryId = categoryId,
////                            )
////                        doubt.postrunAttempt = RunAttemptsId(attemptId)
////                        doubt.contents = ContentsId(contentId)
////                        viewModel.createDoubtProgress.observer(this) { progress ->
////                            if (progress)
////                                doubtDialog.dismiss()
////                            else {
////                                doubtDialog.dismiss()
////                                toast("there was some error please try again")
////                            }
////                        }
////                        viewModel.createDoubt(doubt, attemptId)
//                    }
//                }
//
//                doubtDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//                doubtDialog.setView(doubtView)
//                doubtDialog.setCancelable(false)
//                doubtDialog.show()
//            }
//        }
//    }

//    private fun createNote(notePos: Double) {
//        val noteDialog = AlertDialog.Builder(this).create()
//        val noteView = layoutInflater.inflate(R.layout.doubt_dialog, null)
//        noteView.descriptionLayout.visibility = View.GONE
//        noteView.title.text = resources.getString(R.string.create_a_note)
//        noteView.okBtn.text = resources.getString(R.string.create_note)
//
//        noteView.cancelBtn.setOnClickListener {
//            noteDialog.dismiss()
//        }
//        noteView.okBtn.setOnClickListener {
//            if (noteView.titleLayout.editText?.text.toString().isEmpty()) {
//                noteView.titleLayout.error = "Note Cannot Be Empty."
//                return@setOnClickListener
//            } else {
//                noteView.descriptionLayout.error = ""
//                val note = Notes()
//                note.text = noteView.titleLayout.editText?.text.toString()
//                note.duration = notePos
//                note.content = ContentsId(contentId)
//                note.runAttempt = RunAttemptsId(attemptId)
//
//                viewModel.createNoteProgress.observer(this) {
//                    if (it) noteDialog.dismiss()
//                    else toast("there was some errror with creating notes, try again")
//                }
//                viewModel.createNote(note, attemptId)
//            }
//        }
//
//        noteDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        noteDialog.setView(noteView)
//        noteDialog.setCancelable(false)
//        noteDialog.show()
//    }

    override fun onBackPressed() {
        if (viewModel.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            showFullScreen(false)
            playerControlView.setFullscreenState(false)
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
            videoPlayer?.playWhenReady = true
            videoPlayer?.playbackSpeed = getPrefs().SP_PLAYBACK_SPEED
        }

        override fun onBufferUpdate(p0: Long) {
        }

        override fun onProgress(p0: Long) {
        }

        override fun onPlaybackSpeedChanged(speed: Float) {
            getPrefs().SP_PLAYBACK_SPEED = speed
        }

        override fun onLoadError(p0: VdoPlayer.VdoInitParams, p1: ErrorDescription) {
            Crashlytics.log("Error Message: ${p1.errorMsg}, " +
                "Error Code: ${p1?.errorCode} , ${p1?.httpStatusCode}")
            if (p1.errorCode == 4101 || p1.errorCode == 5110) {
                rootLayout.snackbar("Seems like your download was corrupted.Please Download Again")
//                viewModel.deleteVideo(contentId)
            } else if (p1.errorCode in (2010..2020)) {
//                viewModel.getOtp(videoId, sectionId, attemptId)
            }
        }

        override fun onMediaEnded(p0: VdoPlayer.VdoInitParams?) {

        }

        override fun onError(p0: VdoPlayer.VdoInitParams?, p1: ErrorDescription?) {
            Crashlytics.log("Error Message: ${p1?.errorMsg}," +
                " Error Code: ${p1?.errorCode} , ${p1?.httpStatusCode}")
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
                playerTabs.isVisible = false
                pagerFrame.isVisible = false
                playerControlView.fitsSystemWindows = true

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
                playerTabs.isVisible = true
                pagerFrame.isVisible = true

                playerControlView.fitsSystemWindows = false

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

//    override fun onUserLeaveHint() {
//        super.onUserLeaveHint()
//        if (getPrefs().SP_PIP) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                activatePIPMode()
//        }
//    }

//    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
//
//        if (isInPictureInPictureMode) {
//            player_tabs.visibility = View.GONE
//            pagerFrame.visibility = View.GONE
//
//            playerControlView?.fitsSystemWindows = true
//
//            if (::playerFragment.isInitialized) {
//                val paramsFragment: RelativeLayout.LayoutParams =
//                    playerFragment.view?.layoutParams as RelativeLayout.LayoutParams
//                paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//                paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_START)
//                paramsFragment.addRule(RelativeLayout.ALIGN_PARENT_END)
//            }
//
//            // hide system windows
//            showControls(false)
//        } else {
//            player_tabs.visibility = View.VISIBLE
//            pagerFrame.visibility = View.VISIBLE
//
//            if (::playerFragment.isInitialized) {
//                val paramsFragment: RelativeLayout.LayoutParams =
//                    playerFragment.view?.layoutParams as RelativeLayout.LayoutParams
//                paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
//                paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_START)
//                paramsFragment.removeRule(RelativeLayout.ALIGN_PARENT_END)
//            }
//            playerControlView?.setPadding(0, 0, 0, 0)
//        }
//
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
//    }

//    @TargetApi(Build.VERSION_CODES.O)
//    fun activatePIPMode() {
//
//        val display = windowManager.defaultDisplay
//        val size = Point()
//        display.getSize(size)
//        val width = size.x
//        val height = size.y
//
//        val aspectRatio = Rational(width, height)
//        val mPIPParams = PictureInPictureParams.Builder()
//        mPIPParams.setAspectRatio(aspectRatio)
//        enterPictureInPictureMode(mPIPParams.build())
//    }
}
