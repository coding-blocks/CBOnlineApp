package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.fragments.AnnouncementsFragment
import com.codingblocks.cbonlineapp.fragments.CourseContentFragment
import com.codingblocks.cbonlineapp.fragments.DoubtsFragment
import com.codingblocks.cbonlineapp.fragments.OverviewFragment
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlin.concurrent.thread


class MyCourseActivity : AppCompatActivity(), AnkoLogger {

    lateinit var attempt_Id: String
    private lateinit var database: AppDatabase
    var writtenToDisk = false
    var writtenToDiskVideo = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("courseName")
        attempt_Id = intent.getStringExtra("attempt_id")
        database = AppDatabase.getInstance(this)

        val runDao = database.courseRunDao()
        val sectionDao = database.setionDao()
        val contentDao = database.contentDao()
        val courseDao = database.courseDao()
        val instructorDao = database.instructorDao()
        setupViewPager()


        Clients.onlineV2JsonApi.enrolledCourseById( attempt_Id).enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let {

                val course = it.run?.course?.run {
                    Course(
                            id!!,
                            title!!,
                            subtitle!!,
                            logo!!,
                            summary!!,
                            promoVideo!!,
                            difficulty!!,
                            reviewCount!!,
                            rating!!,
                            slug!!,
                            coverImage!!,
                            attempt_Id,
                            updatedAt!!
                    )
                }

                val run = it.run?.run {
                    CourseRun(
                            id!!,
                            attempt_Id,
                            name!!,
                            description!!,
                            start!!,
                            end!!,
                            price!!,
                            mrp!!,
                            courseId!!,
                            updatedAt!!
                    )
                }

                thread {
                    courseDao.insert(course!!)
                    runDao.insert(run!!)

                    //Course Instructors List
                    for (instructor in it.run?.course!!.instructors!!) {
                        instructorDao.insert(Instructor(instructor.id!!, instructor.name!!,
                                instructor.description!!, instructor.photo!!,
                                instructor.updatedAt!!, attempt_Id, instructor.instructorCourse?.courseId!!))
                    }

                    //Course Sections List
                    for (section in it.run?.sections!!) {
                        sectionDao.insert(CourseSection(section.id!!, section.name!!,
                                section.order!!, section.premium!!, section.status!!,
                                section.run_id!!, attempt_Id, section.updatedAt!!))

                        //Section Contents List
                        val contents: ArrayList<CourseContent> = ArrayList()
                        for (content in section.contents!!) {
                            if (content.contentable.equals("lecture")) {
                                val contentLecture = ContentLecture(content.lecture?.id!!, content.lecture?.name!!, content.lecture?.duration!!, content.lecture?.video_url!!, content.section_content?.id!!, content.updatedAt!!)
//                                val contentVideo = ContentVideo(content.lecture?.id!!, content.lecture?.name!!, content.lecture?.duration!!, content.lecture?.video_url!!, content.section_content?.id!!, content.updatedAt!!)
//                                val contentCodeChallanege = ContentCodeChallanege(content.lecture?.id!!, content.lecture?.name!!, content.lecture?.duration!!, content.lecture?.video_url!!, content.section_content?.id!!, content.updatedAt!!)
//                                val contentDocument = ContentDocument(content.lecture?.id!!, content.lecture?.name!!, content.lecture?.duration!!, content.lecture?.video_url!!, content.section_content?.id!!, content.updatedAt!!)
//                                val contentQna = ContentQna(content.lecture?.id!!, content.lecture?.name!!, content.lecture?.duration!!, content.lecture?.video_url!!, content.section_content?.id!!, content.updatedAt!!)

                                contents.add(CourseContent(
                                        content.id!!, "UNDONE",
                                        content.title!!, content.duration!!,
                                        content.contentable!!, content.section_content?.order!!,
                                        content.section_content?.sectionId!!, attempt_Id, content.section_content?.updatedAt!!, contentLecture))
                            }
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
        adapter.add(OverviewFragment(), "")
        adapter.add(AnnouncementsFragment(), "")
        adapter.add(CourseContentFragment.newInstance(attempt_Id), "")
        adapter.add(DoubtsFragment(), "")
        htab_viewpager.adapter = adapter
        htab_tabs.setupWithViewPager(htab_viewpager)
        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_menu)
        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_docs)
        htab_tabs.getTabAt(3)?.setIcon(R.drawable.ic_support)

    }


}
