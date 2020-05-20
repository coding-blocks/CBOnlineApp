package com.codingblocks.cbonlineapp.mycourse.player

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
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
import com.codingblocks.cbonlineapp.course.batches.RUNTIERS
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.library.EditNoteClickListener
import com.codingblocks.cbonlineapp.mycourse.player.doubts.VideoDoubtFragment
import com.codingblocks.cbonlineapp.mycourse.player.notes.VideoNotesFragment
import com.codingblocks.cbonlineapp.util.Animations
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DownloadWorker
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils.deleteRecursive
import com.codingblocks.cbonlineapp.util.MediaUtils.getYoutubeVideoId
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.TITLE
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.getDistinct
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.secToTime
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.widgets.ProgressDialog
import com.codingblocks.cbonlineapp.util.widgets.VdoPlayerControls
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost.VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import java.io.File
import java.util.Objects
import java.util.concurrent.TimeUnit
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
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class VideoPlayerActivity : BaseCBActivity(), EditNoteClickListener, AnkoLogger,
    VdoPlayer.InitializationListener, VdoPlayerControls.FullscreenActionListener,
    VdoPlayerControls.ControllerVisibilityListener, YouTubePlayerFullScreenListener {

    private val vm: VideoPlayerViewModel by stateViewModel()

    private val animationUtils by lazy { Animations(this) }
    private val progressDialog by lazy { ProgressDialog.progressDialog(this) }
    private val dialog: BottomSheetDialog by lazy { BottomSheetDialog(this) }
    private val sheetDialog: View by lazy { layoutInflater.inflate(R.layout.bottom_sheet_note, null) }
    val tracker = YouTubePlayerTracker()

    private lateinit var playerFragment: VdoPlayerSupportFragment
    private lateinit var videoPlayer: VdoPlayer
    private lateinit var youtubePlayer: YouTubePlayer
    private val sectionItemsAdapter = PlaylistAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        if (savedInstanceState == null) {
            vm.currentContentId = intent.getStringExtra(CONTENT_ID)
            vm.sectionId = intent.getStringExtra(SECTION_ID)
            vm.position = intent.getLongExtra(VIDEO_POSITION, 0)
        }
        setUpBottomSheet()
        setupViewPager()
        setupUI()
    }

    private fun setupUI() {

        vm.offlineSnackbar.observer(this) {
            rootLayout.showSnackbar(it, Snackbar.LENGTH_SHORT, action = false)
        }

        contentRv.setRv(this, sectionItemsAdapter)
        vm.contentList.observer(this) {
            sectionItemsAdapter.submitList(it.contents.filter { it.contentable == VIDEO || it.contentable == LECTURE }.sortedBy { it.order }, vm.currentContentId!!)
        }
        sectionItemsAdapter.onItemClick = {
            startActivity(createVideoPlayerActivityIntent(this, it.ccid, vm.sectionId ?: ""))
        }

        rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        vm.currentOrientation = resources.configuration.orientation
        playerControlView.vdo_back.setOnClickListener {
            onBackPressed()
        }
        contentListContainer.setOnClickListener {
            contentListView.isVisible = !contentListView.isVisible
            videoFab.isVisible = !contentListView.isVisible
        }

        videoFab.setOnClickListener {
            with(noteFabTv.isVisible) {
                noteFabTv.isVisible = !this
                doubtFabTv.isVisible = !this

                if (this) {
                    doubtFab.startAnimation(animationUtils.close)
                    noteFab.startAnimation(animationUtils.close)
                    videoFab.startAnimation(animationUtils.anticlock)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fabMenu.setBackgroundColor(getColor(R.color.white_transparent))
                    } else {
                        fabMenu.setBackgroundColor(resources.getColor(R.color.white_transparent))
                    }
                } else {
                    doubtFab.startAnimation(animationUtils.open)
                    noteFab.startAnimation(animationUtils.open)
                    videoFab.startAnimation(animationUtils.clock)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fabMenu.setBackgroundColor(getColor(R.color.black_95))
                    } else {
                        fabMenu.setBackgroundColor(resources.getColor(R.color.black_95))
                    }
                }
            }
        }
        doubtFab.setOnClickListener {
            vm.runAttempts.observer(this) {
                if (it.premium && RUNTIERS.LITE.name != it.runTier)
                    updateSheet("DOUBT")
                else {
                    toast("Doubt Support is only available for PREMIUM+ Runs.")
                    openChrome(BuildConfig.DISCUSS_URL + contentTitle.text.toString().replace(" ", "-"))
                }
            }
        }

        noteFab.setOnClickListener {
            updateSheet("")
        }
        bookmarkBtn.setOnClickListener { view ->
            if (bookmarkBtn.isActivated)
                vm.removeBookmark()
            else {
                vm.markBookmark()
            }
        }
        downloadBtn.setOnClickListener {
            if (vm.isDownloaded)
                showDeleteDialog()
            else
                startDownloadWorker()
        }

        vm.content.getDistinct().observe(this) {
            //            vm.contentLength = it.contentLecture.lectureDuration
            sectionItemsAdapter.updateSelectedItem(it.ccid)
            vm.attemptId.value = it.attempt_id
            sectionTitle.text = getString(R.string.section_name, it.sectionTitle)
            contentTitle.text = it.title
            if (it.contentable == LECTURE) {
                vm.currentContentProgress = it.progress
                vm.isDownloaded = it.contentLecture.isDownloaded
                downloadBtn.isVisible = true
                downloadBtn.isActivated = vm.isDownloaded
                vm.currentVideoId.value = it.contentLecture.lectureId
                youtubePlayerView.isVisible = false
                videoContainer.visibility = View.VISIBLE
                initializePlayer()
                if (vm.isDownloaded) {
                    vm.getOtpProgress.postValue(true)
                } else {
                    vm.getOtpProgress.postValue(null)
                    vm.getOtp()
                }
            } else if (it.contentable == VIDEO) {
                downloadBtn.isVisible = false
                with(youtubePlayerView) {
                    lifecycle.addObserver(this)
                    isVisible = true
                    videoContainer.visibility = View.GONE
                    addFullScreenListener(this@VideoPlayerActivity)
                }
                setYoutubePlayer(it.contentVideo.videoUrl)
            } else {
                finish()
            }
            vm.bookmark.observe(this) {
                // Don't Remove
                bookmarkBtn.isActivated = if (it == null) false else it.bookmarkUid.isNotEmpty()
            }
        }
    }

    private fun showDeleteDialog() {
        showDialog(
            type = "Delete",
            image = R.drawable.ic_info,
            cancelable = false,
            primaryText = R.string.confirmation,
            secondaryText = R.string.delete_video_desc,
            primaryButtonText = R.string.confirm,
            secondaryButtonText = R.string.cancel,
            callback = { confirmed ->
                if (confirmed) {
                    vm.currentVideoId.value?.let { deleteFolder(it) }
                }
            }
        )
    }

    private fun startDownloadWorker() {
        val constraints = if (vm.prefs.SP_WIFI)
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
        else
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val videoData = workDataOf(
            VIDEO_ID to vm.currentVideoId.value,
            TITLE to contentTitle.text.toString(),
            SECTION_ID to vm.sectionId,
            RUN_ATTEMPT_ID to vm.attemptId.value,
            CONTENT_ID to vm.currentContentId
        )

        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .setInputData(videoData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance().enqueue(request)
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

    private fun initializePlayer() {
        playerFragment =
            supportFragmentManager.findFragmentById(R.id.videoView) as VdoPlayerSupportFragment
        playerFragment.videoStretchMode = VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
        playerFragment.initialize(this)
        showControls(false)
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
            setFullscreenActionListener(this@VideoPlayerActivity)
            setControllerVisibilityListener(this@VideoPlayerActivity)
        }
        showControls(true)
        vm.getOtpProgress.observer(this) {
            if (it && ::videoPlayer.isInitialized) {
                videoPlayer.load(getVdoParams())
            } else
                toast("there was some error with starting feed, try again")
        }
    }

    /**Function to generate new /Reload Video for opt and videoId*/
    private fun getVdoParams(): VdoPlayer.VdoInitParams? {
        return if (vm.isDownloaded) {
            VdoPlayer.VdoInitParams.createParamsForOffline(vm.currentVideoId.value)
        } else {
            VdoPlayer.VdoInitParams.Builder()
                .setOtp(vm.mOtp)
                .setPlaybackInfo(vm.mPlaybackInfo)
                .setPreferredCaptionsLanguage("en")
                .build()
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
            videoPlayer.playWhenReady = true
            videoPlayer.playbackSpeed = vm.prefs.SP_PLAYBACK_SPEED
            vm.position?.let { videoPlayer.seekTo(it) }
        }

        override fun onBufferUpdate(p0: Long) {
        }

        override fun onProgress(progress: Long) {
            checkProgress(progress, playerFragment.player.duration)
        }

        override fun onPlaybackSpeedChanged(speed: Float) {
            vm.prefs.SP_PLAYBACK_SPEED = speed
        }

        override fun onLoadError(p0: VdoPlayer.VdoInitParams, p1: ErrorDescription) {
            FirebaseCrashlytics.getInstance().log(
                "Error Message: ${p1.errorMsg}, " +
                    "Error Code: ${p1.errorCode} , ${p1.httpStatusCode}"
            )
            when (p1.errorCode) {
                5110 -> {
                    rootLayout.snackbar("Seems like your download was corrupted.Please Download Again")
                    deleteFolder(vm.currentContentId ?: "")
                }
                in (2010..2020) -> {
                    vm.getOtp()
                }
                6120 -> {
                }
            }
        }

        override fun onMediaEnded(p0: VdoPlayer.VdoInitParams?) {
        }

        override fun onError(p0: VdoPlayer.VdoInitParams?, p1: ErrorDescription?) {
            FirebaseCrashlytics.getInstance().log(
                "Error Message: ${p1?.errorMsg}," +
                    " Error Code: ${p1?.errorCode} , ${p1?.httpStatusCode}"
            )
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        }
    }

    private fun checkProgress(progress: Long, duration: Long) {
        val per = duration * 0.9
        if (progress > per && vm.currentContentProgress != "DONE") {
            vm.currentContentProgress = "DONE"
            vm.updateProgress()
        }
        /**Remove [PlayerState] After 95%*/

        val completion = duration * 0.95
        if (progress > completion) {
            vm.deletePlayerState()
        }
    }

    private fun deleteFolder(contentId: String) {
        val dir = File(getExternalFilesDir(Environment.getDataDirectory().absolutePath), contentId)
        GlobalScope.launch(Dispatchers.Main) {
            progressDialog.show()
            withContext(Dispatchers.IO) { deleteRecursive(dir) }
            delay(3000)
            vm.updateDownload(0, contentId)
            progressDialog.dismiss()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val newOrientation = newConfig.orientation
        val oldOrientation = vm.currentOrientation
        vm.currentOrientation = newOrientation
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
        if (::youtubePlayer.isInitialized) {
            val id = getYoutubeVideoId(youtubeUrl)
            youtubePlayer.loadVideo(id, 0f)
        }
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                checkProgress(second.toLong(), tracker.videoDuration.toLong())
            }

            override fun onReady(player: YouTubePlayer) {
                youtubePlayer = player
                youtubePlayer.addListener(tracker)
                val id = getYoutubeVideoId(youtubeUrl)
                player.loadVideo(id, vm.position?.toFloat()?.div(1000) ?: 0f)
            }
        })
    }

    override fun onBackPressed() {
        if (vm.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
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
                        vm.updateNote(notes.apply {
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
                            vm.createDoubt(sheetDialog.doubtTitleTv.text.toString(), sheetDialog.bottoSheetDescTv.text.toString()) {
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
                        if (::youtubePlayer.isInitialized)
                            "${contentTitle.text} | ${tracker.currentSecond.toDouble().secToTime()}"
                        else
                            "${contentTitle.text} | ${playerFragment.player.currentTime.toDouble().secToTime()}"

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
                                tracker.currentSecond.toDouble()
                            else
                                (videoPlayer.currentTime.div(1000)).toDouble()
                        setOnClickListener {
                            val desc = sheetDialog.bottoSheetDescTv.text.toString()
                            if (desc.isEmpty()) {
                                toast("Note cannot be empty!!")
                            } else {
                                val note = Note(
                                    notePos
                                        ?: 0.0,
                                    desc,
                                    RunAttempts(
                                        vm.attemptId.value
                                            ?: ""
                                    ),
                                    LectureContent(vm.currentContentId ?: "")
                                )
                                vm.createNote(note)
                                dialog.dismiss()
                            }
                        }
                        text = "Save"
                    }
                }
            }
        }
        dialog.show()
    }

    override fun onStop() {
        if (::playerFragment.isInitialized) {
            vm.position = playerFragment.player.currentTime
            val duration = playerFragment.player.duration
            val time = playerFragment.player.currentTime
            if (time < duration * 0.95)
                vm.savePlayerState(time, true)
        } else if (::youtubePlayer.isInitialized) {

            val duration = (tracker.videoDuration * 1000).toLong()
            val time = (tracker.currentSecond * 1000).toLong()
            if (time < duration * 0.95)
                vm.savePlayerState(time, false)
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (::playerFragment.isInitialized) {
            playerFragment.player?.release()
        }
        super.onDestroy()
    }

    override fun onControllerVisibilityChange(visibility: Int) {
        if (vm.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (visibility != View.VISIBLE) {
                showSystemUi(false)
            }
        }
    }

    override fun onFullscreenAction(enterFullscreen: Boolean): Boolean {
        showFullScreen(enterFullscreen)
        return true
    }

    override fun onYouTubePlayerEnterFullScreen() {
        youtubePlayerView.enterFullScreen()
        showFullScreen(true)
    }

    override fun onYouTubePlayerExitFullScreen() {
        youtubePlayerView.exitFullScreen()
        showFullScreen(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        vm.currentContentId = intent.getStringExtra(CONTENT_ID) ?: ""
        vm.sectionId = intent.getStringExtra(SECTION_ID) ?: ""
        vm.position = 0L
        if (::playerFragment.isInitialized) {
            playerFragment.player.stop()
        }
        if (::youtubePlayer.isInitialized) {
            youtubePlayer.pause()
        }
    }

    companion object {

        fun createVideoPlayerActivityIntent(context: Context, contentId: String, sectionId: String, position: Long = 0): Intent {
            return context.intentFor<VideoPlayerActivity>(
                CONTENT_ID to contentId,
                VIDEO_POSITION to position,
                SECTION_ID to sectionId).singleTop()
        }
    }
}
