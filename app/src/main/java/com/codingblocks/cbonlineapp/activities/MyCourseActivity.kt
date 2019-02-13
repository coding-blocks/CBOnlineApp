package com.codingblocks.cbonlineapp.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.fragments.AnnouncementsFragment
import com.codingblocks.cbonlineapp.fragments.CourseContentFragment
import com.codingblocks.cbonlineapp.fragments.DoubtsFragment
import com.codingblocks.cbonlineapp.fragments.OverviewFragment
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import kotlin.concurrent.thread


class MyCourseActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var attemptId: String
    private lateinit var courseId: String
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    private val courseDao by lazy {
        database.courseDao()
    }
    private val runDao by lazy {
        database.courseRunDao()
    }
    private val sectionWithContentsDao by lazy {
        database.sectionWithContentsDao()
    }


    companion object {
        const val YOUTUBE_API_KEY = "AIzaSyAqdhonCxTsQ5oQ-tyNaSgDJWjEM7UaEt4"
    }

    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        courseId = intent.getStringExtra("course_id")
        title = intent.getStringExtra("courseName")
        attemptId = intent.getStringExtra("attempt_id")

        val sectionDao = database.sectionDao()
        val contentDao = database.contentDao()
        runDao.getRunByAtemptId(attemptId).observe(this, Observer<CourseRun> {
            setupViewPager(it.crUid, it.crCourseId)
        })
        //update hits
        runDao.updateHit(attemptId)


        courseDao.getMyCourse(courseId).observe(this, Observer<Course> {
            youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {

                override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                }

                override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, youtubePlayerInstance: YouTubePlayer?, p2: Boolean) {
                    if (!p2) {
                        it?.let {
                            youtubePlayerInstance?.cueVideo(MediaUtils.getYotubeVideoId(it.promoVideo))
                        }
                    }
                }
            }
            val youTubePlayerSupportFragment = supportFragmentManager.findFragmentById(R.id.displayYoutubeVideo) as YouTubePlayerSupportFragment?
            youTubePlayerSupportFragment!!.initialize(YOUTUBE_API_KEY, youtubePlayerInit)
        })


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
                runDao.insert(run!!)

                doAsync {

                    thread {

                        //Course Sections List
                        for (section in it.run?.sections!!) {
                            sectionDao.insert(CourseSection(section.id ?: "", section.name ?: "",
                                    section.order!!, section.premium!!, section.status ?: "",
                                    section.run_id ?: "", attemptId, section.updatedAt ?: ""))
                            Clients.onlineV2JsonApi.getSectionContents(section.courseContentLinks!!.related.href.substring(7)).enqueue(retrofitCallback { throwable, response ->
                                response?.body().let {
                                    section.courseContent = it
                                    //Section Contents List
                                    section.courseContent?.forEach { content ->
                                        var contentDocument = ContentDocument()
                                        var contentLecture = ContentLecture()
                                        var contentVideo = ContentVideo()
                                        var contentQna = ContentQna()
                                        var contentCodeChallenge = ContentCodeChallenge()



                                        when {
                                            content.contentable.equals("lecture") -> content.lecture?.let {
                                                contentLecture = ContentLecture(it.id ?: "",
                                                        it.name ?: "",
                                                        it.duration!!,
                                                        it.video_url ?: "",
                                                        content.section_content?.id ?: "",
                                                        it.updatedAt ?: "")
                                            }
                                            content.contentable.equals("document") -> content.document?.let {
                                                contentDocument = ContentDocument(it.id
                                                        ?: "",
                                                        it.name ?: "",
                                                        it.pdf_link ?: "",
                                                        content.section_content?.id ?: "",
                                                        it.updatedAt ?: "")
                                            }
                                            content.contentable.equals("video") -> content.video?.let {
                                                contentVideo = ContentVideo(it.id ?: "",
                                                        it.name ?: "",
                                                        it.duration!!,
                                                        it.description ?: "",
                                                        it.url ?: "",
                                                        content.section_content?.id ?: "",
                                                        it.updatedAt ?: "")
                                            }
                                            content.contentable.equals("qna") -> content.qna?.let {
                                                contentQna = ContentQna(it.id ?: "",
                                                        it.name ?: "",
                                                        it.q_id ?: 0,
                                                        content.section_content?.id ?: "",
                                                        it.updatedAt ?: "")
                                            }
                                            content.contentable.equals("code_challenge") -> content.code_challenge?.let {
                                                contentCodeChallenge = ContentCodeChallenge(it.id
                                                        ?: "",
                                                        it.name ?: "",
                                                        it.hb_problem_id ?: 0,
                                                        it.hb_contest_id ?: 0,
                                                        content.section_content?.id ?: "",
                                                        it.updatedAt ?: "")
                                            }
                                        }
                                        var progressId = ""
                                        val status: String
                                        if (content.progress != null) {
                                            status = content.progress?.status ?: ""
                                            progressId = content.progress?.id ?: ""
                                        } else {
                                            status = "UNDONE"
                                        }
                                        val updateContent = contentDao.getContentWithId(attemptId, content.id
                                                ?: "")
                                        if (updateContent == null) {
                                            contentDao.insert(CourseContent(
                                                    content.id ?: "", status, progressId,
                                                    content.title ?: "", content.duration!!,
                                                    content.contentable
                                                            ?: "", content.section_content?.order!!,
                                                    content.section_content?.sectionId
                                                            ?: "", attemptId,
                                                    section.premium!!,
                                                    content.section_content?.updatedAt
                                                            ?: "",
                                                    contentLecture,
                                                    contentDocument,
                                                    contentVideo,
                                                    contentQna,
                                                    contentCodeChallenge))
                                            insertSectionWithContent(section.id
                                                    ?: "", content.id ?: "")
                                        } else if (updateContent.progress != status || updateContent.title != content.title) {
                                            info { "content is updating" }
                                            contentDao.update(CourseContent(
                                                    content.id ?: "", status, progressId,
                                                    content.title ?: "", content.duration!!,
                                                    content.contentable
                                                            ?: "", content.section_content?.order!!,
                                                    content.section_content?.sectionId
                                                            ?: "", attemptId,
                                                    section.premium!!,
                                                    content.section_content?.updatedAt
                                                            ?: "",
                                                    contentLecture,
                                                    contentDocument,
                                                    contentVideo,
                                                    contentQna,
                                                    contentCodeChallenge))
                                        }
                                    }
                                }
                                info { throwable?.localizedMessage }
                            })
                        }
                    }
                }

            }
            info { "error ${throwable?.localizedMessage}" }

        })

    }

    private fun insertSectionWithContent(sectionId: String, contentId: String) {
        thread {
            try {
                sectionWithContentsDao.insert(SectionWithContent(sectionId, contentId))
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CRASH", "COURSE ID : $sectionId")
                Log.e("CRASH", "INSTRUCTOR ID : $contentId")
            }
        }
    }


    private fun setupViewPager(crUid: String, crCourseId: String) {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(OverviewFragment.newInstance(attemptId, crUid), "Overview")
        adapter.add(AnnouncementsFragment.newInstance(courseId), "About")
        adapter.add(CourseContentFragment.newInstance(attemptId), "Course Content")
        adapter.add(DoubtsFragment.newInstance(attemptId, crCourseId), "Doubts")

        htab_viewpager.adapter = adapter
        htab_tabs.setupWithViewPager(htab_viewpager)
        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_menu)
        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_docs)
        htab_tabs.getTabAt(3)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.select()
        htab_viewpager.offscreenPageLimit = 4

    }


}
