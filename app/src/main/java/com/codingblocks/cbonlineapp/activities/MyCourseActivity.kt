package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.fragments.AnnouncementsFragment
import com.codingblocks.cbonlineapp.fragments.CourseContentFragment
import com.codingblocks.cbonlineapp.fragments.OverviewFragment
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlin.concurrent.thread


class MyCourseActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var attemptId: String
    private lateinit var courseId: String
    private lateinit var database: AppDatabase

    companion object {
        const val YOUTUBE_API_KEY = "AIzaSyAqdhonCxTsQ5oQ-tyNaSgDJWjEM7UaEt4"
    }

    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        courseId = intent.getStringExtra("course_id")
        title = intent.getStringExtra("courseName")
        attemptId = intent.getStringExtra("attempt_id")
        database = AppDatabase.getInstance(this)

        val runDao = database.courseRunDao()
        val sectionDao = database.sectionDao()
        val contentDao = database.contentDao()
        val courseDao = database.courseDao()
        setupViewPager()
//
//        courseDao.getMyCourse(attemptId).observe(this, Observer<Course> {
//            youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
//                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
//                }
//
//                override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, youtubePlayerInstance: YouTubePlayer?, p2: Boolean) {
//                    if (!p2) {
//                        it?.let {
//                            youtubePlayerInstance?.cueVideo(MediaUtils.getYotubeVideoId(it.promoVideo))
//                        }
//                    }
//                }
//            }
//            val youTubePlayerSupportFragment = supportFragmentManager.findFragmentById(R.id.displayYoutubeVideo) as YouTubePlayerSupportFragment?
//            youTubePlayerSupportFragment!!.initialize(YOUTUBE_API_KEY, youtubePlayerInit)
//        })


        Clients.onlineV2JsonApi.enrolledCourseById(attemptId).enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { it ->
                val run = it.run?.run {
                    CourseRun(
                            id.toString(),
                            attemptId,
                            name.toString(),
                            description.toString(),
                            start.toString(),
                            end.toString(),
                            price.toString(),
                            mrp.toString(),
                            courseId.toString(),
                            updatedAt.toString()
                    )
                }

                thread {
                    runDao.insert(run!!)
                    //Course Sections List
                    for (section in it.run?.sections!!) {
                        sectionDao.insert(CourseSection(section.id.toString(), section.name.toString(),
                                section.order!!, section.premium!!, section.status.toString(),
                                section.run_id.toString(), attemptId, section.updatedAt.toString()))

                        //Section Contents List
                        val contents: ArrayList<CourseContent> = ArrayList()
                        for (content in section.contents!!) {
                            var contentDocument = ContentDocument()
                            var contentLecture = ContentLecture()
                            var contentVideo = ContentVideo()

                            when {
                                content.contentable.equals("lecture") -> content.lecture?.let {
                                    contentLecture = ContentLecture(it.id.toString(),
                                            it.name.toString(),
                                            it.duration!!,
                                            it.video_url.toString(),
                                            content.section_content?.id.toString(),
                                            it.updatedAt.toString())
                                }
                                content.contentable.equals("document") -> content.document?.let {
                                    contentDocument = ContentDocument(it.id.toString(),
                                            it.name.toString(),
                                            it.pdf_link.toString(),
                                            content.section_content?.id.toString(),
                                            it.updatedAt.toString())
                                }
                                content.contentable.equals("video") -> content.video?.let {
                                    contentVideo = ContentVideo(it.id.toString(),
                                            it.name.toString(),
                                            it.duration!!,
                                            it.description.toString(),
                                            it.url.toString(),
                                            content.section_content?.id.toString(),
                                            it.updatedAt.toString())
                                }
                            }
                            var progressId = ""
                            var status: String
                            if (content.progress != null) {
                                status = content.progress?.status.toString()
                                progressId = content.progress?.id.toString()
                            } else {
                                status = "UNDONE"
                            }
                            contents.add(CourseContent(
                                    content.id.toString(), status, progressId,
                                    content.title.toString(), content.duration!!,
                                    content.contentable.toString(), content.section_content?.order!!,
                                    content.section_content?.sectionId.toString(), attemptId,
                                    content.section_content?.updatedAt.toString(), contentLecture,
                                    contentDocument, contentVideo))
                        }

                        contentDao.insertAll(contents)
                    }
                }

            }
            info { "error ${throwable?.localizedMessage}" }

        })

    }


    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(OverviewFragment.newInstance(attemptId), "Overview")
        adapter.add(AnnouncementsFragment.newInstance(courseId), "About")
        adapter.add(CourseContentFragment.newInstance(attemptId), "Course Content")
        htab_viewpager.adapter = adapter
        htab_tabs.setupWithViewPager(htab_viewpager)
        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_menu)
        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_docs)
        htab_tabs.getTabAt(2)?.select()
        htab_viewpager.offscreenPageLimit = 3

    }


}
