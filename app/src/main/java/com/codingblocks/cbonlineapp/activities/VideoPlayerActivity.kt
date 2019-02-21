package com.codingblocks.cbonlineapp.activities

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.CourseRun
import com.codingblocks.cbonlineapp.database.DoubtsModel
import com.codingblocks.cbonlineapp.fragments.VideoDoubtFragment
import com.codingblocks.cbonlineapp.fragments.VideoNotesFragment
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.cbonlineapp.utils.MyVideoControls
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.cbonlineapp.utils.pageChangeCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.RunAttemptsModel
import com.devbrackets.android.exomedia.listener.OnPreparedListener
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.doubt_dialog.view.*
import kotlinx.android.synthetic.main.exomedia_default_controls_mobile.view.*
import kotlin.concurrent.thread


class VideoPlayerActivity : AppCompatActivity(),
        OnPreparedListener,
        OnItemClickListener {

    private var youtubePlayer: YouTubePlayer? = null
    private var pos: Long? = 0
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private lateinit var attemptId: String
    private lateinit var contentId: String

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    private val doubtsDao by lazy {
        database.doubtsDao()
    }

    private val courseDao by lazy {
        database.courseDao()
    }

    private val runDao by lazy {
        database.courseRunDao()
    }

    override fun onItemClick(position: Int, id: String) {
        if(contentId == id) {
            if (displayYoutubeVideo.view?.visibility == View.VISIBLE)
                youtubePlayer?.seekToMillis(position * 1000)
            else
                videoView.seekTo(position.toLong() * 1000)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val url = intent.getStringExtra("FOLDER_NAME")
        val youtubeUrl = intent.getStringExtra("videoUrl")
        attemptId = intent.getStringExtra("attemptId")
        contentId = intent.getStringExtra("contentId")


        if (youtubeUrl != null) {
            displayYoutubeVideo.view?.visibility = View.VISIBLE
            setupYoutubePlayer(youtubeUrl)
        } else {
            displayYoutubeVideo.view?.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            setupVideoView(url)
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
        player_viewpager.addOnPageChangeListener(pageChangeCallback(fnSelected = { position ->
            when (position) {
                0 -> {
                    videoFab.setOnClickListener {
                        showDialog()
                    }
                }
                1 -> {
                    videoFab.setOnClickListener {

                    }
                }
            }
        }, fnState = {}, fnScrolled = { _: Int, _: Float, _: Int ->
        }))
    }


    override fun onStart() {
        super.onStart()

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
        youTubePlayerSupportFragment!!.initialize(MyCourseActivity.YOUTUBE_API_KEY, youtubePlayerInit)


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
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == -1) {
            pos = data?.getLongExtra("CURRENT_POSITION", 0)
            videoView.seekTo(pos ?: 0)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private fun showDialog() {

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
                                doubtsDao.insert(DoubtsModel(it!!.id
                                        ?: "", it.title, it.body, it.content?.id
                                        ?: "", it.status, it.runAttempt?.id ?: ""
                                ))
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


}