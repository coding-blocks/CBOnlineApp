package com.codingblocks.cbonlineapp.mycourse.player

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.Environment
import android.util.Rational
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
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
import com.codingblocks.cbonlineapp.util.PreferenceHelper.Companion.getPrefs
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
import com.codingblocks.cbonlineapp.util.extensions.*
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
import kotlinx.android.synthetic.main.activity_video_player.youtubePlayerView
import kotlinx.android.synthetic.main.bottom_sheet_note.view.*
import kotlinx.android.synthetic.main.my_fab_menu.*
import kotlinx.android.synthetic.main.vdo_control_view.*
import kotlinx.android.synthetic.main.vdo_control_view.view.*
import kotlinx.android.synthetic.main.vdo_control_view.view.vdo_loader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
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
    private val ACTION_MEDIA_CONTROL = "media_control"
    private val EXTRA_CONTROL_TYPE = "control_type"
    private val CONTROL_TYPE_PLAY = 1
    private val CONTROL_TYPE_PAUSE = 2
    private val REQUEST_PLAY = 1
    private val REQUEST_PAUSE = 2
    private var hasBeenIntoPIP: Boolean = false
    private var isCallingFromFinish: Boolean = false
    private var isYoutubeVideoReady: Boolean = false
    private var contentable: String = ""
    private lateinit var mPIPParams: PictureInPictureParams.Builder

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

        registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
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
            contentable = it.contentable
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                if (isInPictureInPictureMode) {
                    playerControlView.isVisible = false
                }
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

    private val mReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent!!.action != ACTION_MEDIA_CONTROL) {
                return
            }

            val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
            when (controlType) {
                CONTROL_TYPE_PLAY -> playVideo()
                CONTROL_TYPE_PAUSE -> pauseVideo()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playVideo() {
        if (contentable == LECTURE) {
            videoPlayer.playWhenReady = true
        } else {
            if (isYoutubeVideoReady)
                youtubePlayer.play()
            else
                return
        }
        updatePictureInPictureActions(R.drawable.ic_pause, "Pause",
            CONTROL_TYPE_PAUSE, REQUEST_PAUSE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pauseVideo() {
        if (contentable == LECTURE) {
            videoPlayer.playWhenReady = false
        } else {
            if (isYoutubeVideoReady)
                youtubePlayer.pause()
            else
                return
        }
        updatePictureInPictureActions(R.drawable.ic_play, "Play",
            CONTROL_TYPE_PLAY, REQUEST_PLAY)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (getPrefs().SP_PIP and !isCallingFromFinish) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE))
                    activatePIPMode()
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {

        if (isInPictureInPictureMode) {
            showControls(false)
            hasBeenIntoPIP = true
            playerControlView.isVisible = false
        } else {
            showControls(true)
            playerControlView.isVisible = true
        }

        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun updatePictureInPictureActions(@DrawableRes iconId: Int, title: String,
                                               controlType: Int, requestCode: Int) {
        val actions = ArrayList<RemoteAction>()

        val intent = PendingIntent.getBroadcast(this@VideoPlayerActivity,
            requestCode, Intent(ACTION_MEDIA_CONTROL)
            .putExtra(EXTRA_CONTROL_TYPE, controlType), 0)
        val icon = Icon.createWithResource(this@VideoPlayerActivity, iconId)
        actions.add(RemoteAction(icon, title, title, intent))

        mPIPParams.setActions(actions)
        setPictureInPictureParams(mPIPParams.build())
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun activatePIPMode() {

        val width: Int
        val height: Int
        when (contentable) {
            LECTURE -> {
                width = playerFragment.requireView().width
                height = playerFragment.requireView().height
            }
            VIDEO -> {
                width = youtubePlayerView.width
                height = youtubePlayerView.height
            }
            else -> return
        }

        val aspectRatio = Rational(width, height)
        mPIPParams = PictureInPictureParams.Builder()
        mPIPParams.setAspectRatio(aspectRatio)

        updatePictureInPictureActions(R.drawable.ic_pause, "Pause",
            CONTROL_TYPE_PAUSE, REQUEST_PAUSE)

        enterPictureInPictureMode(mPIPParams.build())
    }

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

    fun navToLauncherTask(appContext: Context) {
        val activityManager: ActivityManager = (appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)!!
        val appTasks: List<ActivityManager.AppTask> = activityManager.getAppTasks()
        for (task in appTasks) {
            val baseIntent: Intent = task.getTaskInfo().baseIntent
            val categories = baseIntent.categories
            if (categories != null && categories.contains(Intent.CATEGORY_LAUNCHER)) {
                task.moveToFront()
                return
            }
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
                isYoutubeVideoReady = true
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
                                    if (it.isEmpty()) {
                                        hideVideoFab()
                                        dialog.dismiss()
                                    }
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
                            "${contentTitle.text} | ${(tracker.currentSecond.div(1000)).toDouble().secToTime()}"
                        else
                            "${contentTitle.text} | ${(playerFragment.player.currentTime.div(1000)).toDouble().secToTime()}"

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
                                (tracker.currentSecond.div(1000)).toDouble()
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
                                hideVideoFab()
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
        if (::playerFragment.isInitialized && videoContainer.isVisible) {
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
        unregisterReceiver(mReceiver)
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

    override fun finish() {
        isCallingFromFinish = true
        if (hasBeenIntoPIP) {
            navToLauncherTask(applicationContext)
        }
        super.finish()
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

    fun hideVideoFab(){
        noteFabTv.isVisible = false
        doubtFabTv.isVisible = false
        doubtFab.startAnimation(animationUtils.close)
        noteFab.startAnimation(animationUtils.close)
        videoFab.startAnimation(animationUtils.anticlock)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fabMenu.setBackgroundColor(getColor(R.color.white_transparent))
        } else {
            fabMenu.setBackgroundColor(resources.getColor(R.color.white_transparent))
        }
    }

    companion object {

        fun createVideoPlayerActivityIntent(context: Context, contentId: String, sectionId: String, position: Long = 0): Intent {
            return context.intentFor<VideoPlayerActivity>(
                CONTENT_ID to contentId,
                VIDEO_POSITION to position,
                SECTION_ID to sectionId).singleTop().apply { if (getPrefs(context).SP_PIP) excludeFromRecents() }
        }
    }
}
