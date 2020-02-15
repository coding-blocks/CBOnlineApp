package com.codingblocks.cbonlineapp.mycourse.player

import android.animation.LayoutTransition
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.library.EditNoteClickListener
import com.codingblocks.cbonlineapp.mycourse.player.doubts.VideoDoubtFragment
import com.codingblocks.cbonlineapp.mycourse.player.notes.VideoNotesFragment
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DownloadWorker
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.MediaUtils.deleteRecursive
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.TITLE
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.observeOnce
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.secToTime
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.widgets.ProgressDialog
import com.codingblocks.cbonlineapp.util.widgets.VdoPlayerControls
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost.VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.bottom_sheet_note.view.*
import kotlinx.android.synthetic.main.my_fab_menu.*
import kotlinx.android.synthetic.main.vdo_control_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Objects
import java.util.concurrent.TimeUnit

class VideoPlayerActivity : BaseCBActivity(), EditNoteClickListener, AnkoLogger,
    VdoPlayer.InitializationListener {
    private var isDownloaded = false
    private val viewModel by viewModel<VideoPlayerViewModel>()
    private val fullscreenToggleListener = object : VdoPlayerControls.FullscreenActionListener {
        override fun onFullscreenAction(enterFullscreen: Boolean): Boolean {
            showFullScreen(enterFullscreen)
            return true
        }
    }
    private val prefs by inject<PreferenceHelper>()

    private val visibilityListener = object : VdoPlayerControls.ControllerVisibilityListener {
        override fun onControllerVisibilityChange(visibility: Int) {
            if (viewModel.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (visibility != View.VISIBLE) {
                    showSystemUi(false)
                }
            }
        }
    }
    val progressDialog by lazy {
        ProgressDialog.progressDialog(this)
    }
    private val fab_open by lazy {
        AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
    }
    private val fab_close by lazy {
        AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
    }
    private val fab_clock by lazy {
        AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_clock)
    }
    private val fab_anticlock by lazy {
        AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)
    }

    private val dialog by lazy { BottomSheetDialog(this) }
    private val sheetDialog: View by lazy {
        layoutInflater.inflate(
            R.layout.bottom_sheet_note,
            null
        )
    }
    private lateinit var playerFragment: VdoPlayerSupportFragment
    private var videoPlayer: VdoPlayer? = null
    private var youtubePlayer: YouTubePlayer? = null
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        Clients.authJwt = prefs.SP_JWT_TOKEN_KEY
        Clients.refreshToken = prefs.SP_JWT_REFRESH_TOKEN

        viewModel.contentId = intent.getStringExtra(CONTENT_ID) ?: ""
        viewModel.sectionId = intent.getStringExtra(SECTION_ID) ?: ""

        setupUI()
        viewModel.offlineSnackbar.observer(this) {
            rootLayout.showSnackbar(it, Snackbar.LENGTH_SHORT, action = false)
        }
    }

    private fun setupUI() {
        setUpBottomSheet()

        rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        viewModel.currentOrientation = resources.configuration.orientation
        viewModel.content.observeOnce {
            viewModel.contentLength = it.contentLecture.lectureDuration
            viewModel.attemptId.value = it.attempt_id
            sectionTitle.text = "Section ${it.sectionTitle}"
            contentTitle.text = it.title
//            bookmarkBtn.isActivated = it.bookmark.bookmarkUid.isNotEmpty()
            if (it.contentable == LECTURE) {
                isDownloaded = it.contentLecture.isDownloaded
                downloadBtn.isActivated = isDownloaded
                viewModel.videoId = it.contentLecture.lectureId
                youtubePlayerView.view?.visibility = View.GONE
                videoContainer.visibility = View.VISIBLE
                playerFragment =
                    supportFragmentManager.findFragmentById(R.id.videoView) as VdoPlayerSupportFragment
                playerFragment.videoStretchMode = VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
                showControls(false)
                if (isDownloaded) {
                    initializePlayer()
                } else {
                    setupVideoView()
                }
            } else if (it.contentable == VIDEO) {
                youtubePlayerView.view?.visibility = View.VISIBLE
                setYoutubePlayer(it.contentVideo.videoUrl)
            } else {
                finish()
            }
            viewModel.bookmark.observe(this) {
                // Dont Remove
                bookmarkBtn.isActivated = if (it == null) false else it.bookmarkUid.isNotEmpty()
            }

            bookmarkBtn.setOnClickListener { view ->
                if (bookmarkBtn.isActivated)
                    viewModel.removeBookmark()
                else {
                    viewModel.markBookmark()
                }
            }
        }
        playerControlView.vdo_back.setOnClickListener {
            onBackPressed()
        }

        videoFab.setOnClickListener {
            with(noteFabTv.isVisible) {
                noteFabTv.isVisible = !this
                doubtFabTv.isVisible = !this

                if (this) {
                    doubtFab.startAnimation(fab_close)
                    noteFab.startAnimation(fab_close)
                    videoFab.startAnimation(fab_anticlock)
                    fabMenu.setBackgroundColor(getColor(R.color.white_transparent))
                } else {
                    doubtFab.startAnimation(fab_open)
                    noteFab.startAnimation(fab_open)
                    videoFab.startAnimation(fab_clock)
                    fabMenu.setBackgroundColor(getColor(R.color.black_95))
                }
            }
        }
        doubtFab.setOnClickListener {
            updateSheet("DOUBT")
        }

        noteFab.setOnClickListener {
            updateSheet("")
        }
        downloadBtn.setOnClickListener {
            if (isDownloaded)
                deleteFolder(viewModel.videoId)
            else
                startDownloadWorker()
        }

        setupViewPager()
    }

    private fun startDownloadWorker() {
        val constraints = if (prefs.SP_WIFI)
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
        else
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val videoData = workDataOf(
            VIDEO_ID to viewModel.videoId,
            TITLE to contentTitle.text.toString(),
            SECTION_ID to viewModel.sectionId,
            RUN_ATTEMPT_ID to viewModel.attemptId.value,
            CONTENT_ID to viewModel.contentId
        )

        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .setInputData(videoData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
//        startService(intentFor<DownloadService>(VIDEO_ID to viewModel.videoId,
//            TITLE to contentTitle.text.toString(),
//            SECTION_ID to viewModel.sectionId,
//            RUN_ATTEMPT_ID to viewModel.attemptId.value,
//            CONTENT_ID to viewModel.contentId))
        rootLayout.showSnackbar("Download Video In Progress", Snackbar.LENGTH_LONG, action = false)
    }

    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(VideoDoubtFragment(), "Doubts")
        adapter.add(VideoNotesFragment(), "Notes")

        playerViewPager.adapter = adapter
        playerTabs.setupWithViewPager(playerViewPager)
        playerViewPager.offscreenPageLimit = 2
    }

    private fun setupVideoView() {
        viewModel.getOtpProgress.observer(this) {
            if (it) {
                initializePlayer()
            } else
                toast("there was some error with starting feed, try again")
        }
        viewModel.getOtp()
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
        playerHost: VdoPlayer.PlayerHost,
        player: VdoPlayer,
        wasRestored: Boolean
    ) {
        videoPlayer = player
        player.addPlaybackEventListener(playbackListener)
        playerControlView.apply {
            setPlayer(player)
            setFullscreenActionListener(fullscreenToggleListener)
            setControllerVisibilityListener(visibilityListener)
        }
        showControls(true)

        // load a media to the player
        val vdoParams: VdoPlayer.VdoInitParams? = if (isDownloaded) {
            VdoPlayer.VdoInitParams.createParamsForOffline(viewModel.videoId)
        } else {
            VdoPlayer.VdoInitParams.Builder()
                .setOtp(viewModel.mOtp)
                .setPlaybackInfo(viewModel.mPlaybackInfo)
                .setPreferredCaptionsLanguage("en")
                .build()
        }
        player.apply {
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

//    override fun onItemClick(position: Int, id: String) {
//        if (viewModel.contentId == id) {
//            if (youtubePlayerView.isVisible)
//                youtubePlayer.seekTo((position * 1000).toFloat())
//            else
//                videoPlayer?.seekTo(position.toLong() * 1000)
//        }
//    }

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
            videoPlayer?.playbackSpeed = prefs.SP_PLAYBACK_SPEED
        }

        override fun onBufferUpdate(p0: Long) {
        }

        override fun onProgress(progress: Long) {
            val per = (viewModel.contentLength / 100) * 90
            if (progress > per) {
                viewModel.updateProgress()
            }
        }

        override fun onPlaybackSpeedChanged(speed: Float) {
            prefs.SP_PLAYBACK_SPEED = speed
        }

        override fun onLoadError(p0: VdoPlayer.VdoInitParams, p1: ErrorDescription) {
            info { p0 }
            info { p1 }
            Crashlytics.log(
                "Error Message: ${p1.errorMsg}, " +
                    "Error Code: ${p1.errorCode} , ${p1.httpStatusCode}"
            )
            if (p1.errorCode == 5110) {
                rootLayout.snackbar("Seems like your download was corrupted.Please Download Again")
                deleteFolder(viewModel.videoId)
            } else if (p1.errorCode in (2010..2020)) {
                viewModel.getOtp()
            }
        }

        override fun onMediaEnded(p0: VdoPlayer.VdoInitParams?) {
        }

        override fun onError(p0: VdoPlayer.VdoInitParams?, p1: ErrorDescription?) {
            info { p0 }
            info { p1 }
            Crashlytics.log(
                "Error Message: ${p1?.errorMsg}," +
                    " Error Code: ${p1?.errorCode} , ${p1?.httpStatusCode}"
            )
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            viewModel.playWhenReady = playWhenReady
        }
    }

    private fun deleteFolder(contentId: String) {
        val dir = File(getExternalFilesDir(Environment.getDataDirectory().absolutePath), contentId)
        GlobalScope.launch(Dispatchers.Main) {
            progressDialog.show()
            withContext(Dispatchers.IO) { deleteRecursive(dir) }
            delay(3000)
            progressDialog.dismiss()
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
                videoContentContainer.isVisible = false
                fabMenu.isVisible = false
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
                videoContentContainer.isVisible = true
                fabMenu.isVisible = true
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

    private fun setUpBottomSheet() {
        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    private fun setYoutubePlayer(youtubeUrl: String) {
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
            supportFragmentManager.findFragmentById(R.id.youtubePlayerView) as YouTubePlayerSupportFragment?
        youTubePlayerSupportFragment?.initialize(BuildConfig.YOUTUBE_KEY, youtubePlayerInit)
    }

    override fun onBackPressed() {
        if (viewModel.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            showFullScreen(false)
            playerControlView.setFullscreenState(false)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(note: NotesModel) {
        updateSheet("EDIT", note)
    }

    private fun updateSheet(type: String, model: Any? = null) {
        when (type) {
            "EDIT" -> {
                val notes = model as NotesModel
                sheetDialog.apply {
                    bottomSheetTitleTv.text = getString(R.string.edit_note)
                    doubtTitleTv.isVisible = false

                    bottoSheetDescTv.setText(notes.text)
                    bottomSheetInfoTv.text = "${notes.contentTitle} | ${notes.duration.secToTime()}"
                    bottomSheetCancelBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    bottomSheetSaveBtn.setOnClickListener {
                        viewModel.updateNote(notes.apply {
                            text = sheetDialog.bottoSheetDescTv.text.toString()
                        })
                        dialog.dismiss()
                    }
                }
            }
            "DOUBT" -> {
                sheetDialog.apply {
                    bottomSheetTitleTv.text = getString(R.string.ask_doubt)
                    doubtTitleTv.isVisible = true
                    bottoSheetDescTv.apply {
                        setText("")
                        hint = "Description of Doubt"
                    }
                    bottomSheetInfoTv.text = "${contentTitle.text}"
                    bottomSheetCancelBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    bottomSheetSaveBtn.apply {
                        setOnClickListener {
                            viewModel.createDoubt(sheetDialog.doubtTitleTv.text.toString(), sheetDialog.bottoSheetDescTv.text.toString()) {
                                runOnUiThread {
                                    if (it.isEmpty())
                                        dialog.dismiss()
                                    else
                                        toast(it)
                                }
                            }
                        }
                        text = "Post"
                    }
                }
            }
            else -> {
                sheetDialog.apply {
                    bottomSheetTitleTv.text = getString(R.string.add_note)
                    doubtTitleTv.isVisible = false
                    bottoSheetDescTv.setText("")
                    bottomSheetInfoTv.text =
                        "${contentTitle.text} | ${videoPlayer?.currentTime?.toDouble()?.secToTime()}"
                    bottomSheetCancelBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    bottoSheetDescTv.apply {
                        setText("")
                        hint = "Add a note here"
                    }
                    bottomSheetSaveBtn.apply {
                        val notePos: Double? =
                            if (youtubePlayerView.isVisible)
                                (youtubePlayer?.currentTimeMillis?.div(1000))?.toDouble()
                            else
                                (videoPlayer?.currentTime?.div(1000))?.toDouble()
                        setOnClickListener {
                            val note = Note(
                                notePos
                                    ?: 0.0,
                                sheetDialog.bottoSheetDescTv.text.toString(),
                                RunAttempts(
                                    viewModel.attemptId.value
                                        ?: ""
                                ),
                                LectureContent(viewModel.contentId)
                            )
                            viewModel.createNote(note)
                            dialog.dismiss()
                        }
                        text = "Save"
                    }
                }
            }
        }
        dialog.show()
    }

    override fun onDestroy() {
        videoPlayer?.release()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.playWhenReady = true
    }
}
