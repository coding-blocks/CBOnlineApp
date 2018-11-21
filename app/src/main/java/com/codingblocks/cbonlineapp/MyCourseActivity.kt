package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
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

    lateinit var courseId: String
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("courseName")
        courseId = intent.getStringExtra("run_id")
        database = AppDatabase.getInstance(this)

        val runDao = database.courseRunDao()
        val sectionDao = database.setionDao()
        val contentDao = database.contentDao()
        val courseDao = database.courseDao()
        val instructorDao = database.instructorDao()



        runDao.getCourse(courseId).observe(this, Observer<CourseRun> {
            info {
                "course$it"
            }
        })
        sectionDao.getSections().observe(this, Observer<List<CourseSection>> {
            info {
                "sections$it"
            }
        })
        contentDao.getContent().observe(this, Observer<List<CourseContent>> {
            info {
                "content$it"
            }
        })
        courseDao.getCourses().observe(this, Observer<List<Course>> {
            info {
                "course$it"
            }
        })
        instructorDao.getInstructors().observe(this, Observer<List<Instructor>> {
            info {
                "instructor$it"
            }
        })

        setupViewPager()


        Clients.onlineV2PublicClient.enrolledCourseById("JWT " + prefs.SP_JWT_TOKEN_KEY, courseId).enqueue(retrofitcallback { throwable, response ->
            response?.body().let {

                val course = Course(it?.run?.course?.id!!, it.run?.course?.title!!,
                        it.run?.course?.subtitle!!, it.run?.course?.logo!!,
                        it.run?.course?.summary!!, it.run?.course?.promoVideo!!,
                        it.run?.course?.difficulty!!, it.run?.course?.reviewCount!!,
                        it.run?.course?.rating!!, it.run?.course?.slug!!,
                        it.run?.course?.coverImage!!, it.run?.course?.updatedAt!!)

                val run = CourseRun(it.run?.id!!, it.id!!, it.run?.name!!,
                        it.run?.description!!, it.run?.start!!,
                        it.run?.end!!, it.run?.price!!,
                        it.run?.mrp!!, it.run?.courseId!!, it.run?.updatedAt!!)
                thread {
                    courseDao.insert(course)
                    runDao.insert(run)

                    //Course Instructors List
                    for (instructor in it.run?.course!!.instructors!!) {
                        instructorDao.insert(Instructor(instructor.id!!, instructor.name!!,
                                instructor.description!!, instructor.photo!!,
                                instructor.updatedAt!!, instructor.instructorCourse?.courseId!!))
                    }

                    //Course Sections List
                    for (section in it.run?.sections!!) {
                        sectionDao.insert(CourseSection(section.id!!, section.name!!,
                                section.order!!, section.premium!!, section.status!!,
                                section.run_id!!, section.updatedAt!!))

                        //Section Contents List
                        val contents: ArrayList<CourseContent> = ArrayList()
                        for (content in section.contents!!) {
                            contents.add(CourseContent(
                                    content.id!!, "UNDONE",
                                    content.title!!, content.duration!!,
                                    content.contentable!!, content.section_content?.order!!,
                                    content.section_content?.sectionId!!, content.section_content?.updatedAt!!
                            ))
                        }
                        contentDao.insertAll(contents)
                    }
                }

            }
        })

    }


    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(OverviewFragment(), "")
        adapter.add(AnnouncementsFragment(), "")
        adapter.add(CourseContentFragment(), "")
        adapter.add(DoubtsFragment(), "")
        htab_viewpager.adapter = adapter
        htab_tabs.setupWithViewPager(htab_viewpager)
        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_menu)
        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_docs)
        htab_tabs.getTabAt(3)?.setIcon(R.drawable.ic_support)

    }

}
