package com.codingblocks.cbonlineapp.mycourse.player

import android.animation.LayoutTransition
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.library.EditNoteClickListener
import com.codingblocks.cbonlineapp.mycourse.player.doubts.VideoDoubtFragment
import com.codingblocks.cbonlineapp.mycourse.player.notes.VideoNotesFragment
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DownloadService
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.TITLE
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.observeOnce
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.secToTime
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.widgets.VdoPlayerControls
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost.VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.bottom_sheet_note.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : AppCompatActivity(), EditNoteClickListener, AnkoLogger, VdoPlayer.InitializationListener {
    private var download = false
    private val viewModel by viewModel<VideoPlayerViewModel>()
    private val fullscreenToggleListener = object : VdoPlayerControls.FullscreenActionListener {
        override fun onFullscreenAction(enterFullscreen: Boolean): Boolean {
            showFullScreen(enterFullscreen)
            return true
        }
    }

    private val visibilityListener = object : VdoPlayerControls.ControllerVisibilityListener {
        override fun onControllerVisibilityChange(visibility: Int) {
            if (viewModel.currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (visibility != View.VISIBLE) {
                    showSystemUi(false)
                }
            }
        }
    }
    private val dialog by lazy { BottomSheetDialog(this) }
    private val sheetDialog: View by lazy { layoutInflater.inflate(R.layout.bottom_sheet_note, null) }
    private lateinit var youtubePlayer: YouTubePlayer
    private lateinit var playerFragment: VdoPlayerSupportFragment
    private var videoPlayer: VdoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

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
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        viewModel.currentOrientation = resources.configuration.orientation
        viewModel.content.observeOnce {

            viewModel.attemptId.value = it.attempt_id
            sectionTitle.text = "Section ${it.sectionTitle}"
            contentTitle.text = it.title
            bookmarkBtn.isActivated = it.bookmark.bookmarkUid.isEmpty()
            if (it.contentable == LECTURE) {
                download = it.contentLecture.isDownloaded
                if (download) {
                    initializePlayer()
                }
                viewModel.videoId = it.contentLecture.lectureId
                youtubePlayerView.isVisible = false
                videoContainer.visibility = View.VISIBLE
                playerFragment = supportFragmentManager.findFragmentById(R.id.videoView) as VdoPlayerSupportFragment
                playerFragment.videoStretchMode = VIDEO_STRETCH_MODE_MAINTAIN_ASPECT_RATIO
                showControls(false)
                if (it.contentLecture.isDownloaded) {
                    initializePlayer()
                } else {
                    setupVideoView()
                }
            } else if (it.contentable == VIDEO) {
                lifecycle.addObserver(youtubePlayerView)
                youtubePlayerView.isVisible = true
                setYoutubePlayer(it.contentVideo.videoUrl)
            } else {
                finish()
            }

            bookmarkBtn.setOnClickListener { view ->
                if (it.bookmark.bookmarkUid.isEmpty())
                    viewModel.markBookmark()
                else {
                    viewModel.removeBookmark(it.bookmark.bookmarkUid)
                }
            }
        }

        videoFab.setOnClickListener {
            updateSheet("DOUBT")
        }
        downloadBtn.setOnClickListener {
            startDownloadWorker()
        }

        setupViewPager()
    }

    private fun startDownloadWorker() {
        startService(intentFor<DownloadService>(VIDEO_ID to viewModel.videoId,
            TITLE to contentTitle.text.toString(),
            SECTION_ID to viewModel.sectionId,
            RUN_ATTEMPT_ID to viewModel.attemptId.value,
            CONTENT_ID to viewModel.contentId))
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

    override fun onInitializationSuccess(playerHost: VdoPlayer.PlayerHost, player: VdoPlayer, wasRestored: Boolean) {
        videoPlayer = player
        player.addPlaybackEventListener(playbackListener)
        playerControlView.apply {
            setPlayer(player)
            setFullscreenActionListener(fullscreenToggleListener)
            setControllerVisibilityListener(visibilityListener)
        }
        showControls(true)

        // load a media to the player
        val vdoParams: VdoPlayer.VdoInitParams? = if (download) {
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
                "Error Code: ${p1.errorCode} , ${p1.httpStatusCode}")
            if (p1.errorCode == 4101 || p1.errorCode == 5110) {
                rootLayout.snackbar("Seems like your download was corrupted.Please Download Again")
//                viewModel.deleteVideo(contentId)
            } else if (p1.errorCode in (2010..2020)) {
                viewModel.getOtp()
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
    }

    private fun setYoutubePlayer(promoVideo: String) {
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                youTubePlayer.cueVideo(promoVideo.substring(33), 0F)
            }
        })
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
                            viewModel.createDoubt(sheetDialog.doubtTitleTv.text.toString(),
                                sheetDialog.bottoSheetDescTv.text.toString())
                            dialog.dismiss()
                        }
                        text = "Post"
                    }
                }
            }
            else -> {
                sheetDialog.apply {
                    bottomSheetTitleTv.text = getString(R.string.add_note)
                    bottoSheetDescTv.setText("")
                    bottomSheetInfoTv.text = "${contentTitle.text} | 10:00"
                    bottomSheetCancelBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    bottomSheetSaveBtn.setOnClickListener {
                        val note = Note(10.45000, sheetDialog.bottoSheetDescTv.text.toString(), RunAttempts(viewModel.attemptId.value
                            ?: ""), LectureContent(viewModel.contentId))
                        viewModel.createNote(note)
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }

    override fun onDestroy() {
        youtubePlayerView.release()
        videoPlayer?.release()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.playWhenReady = true
    }
}
