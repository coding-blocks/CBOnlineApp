package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.MyCoursesDataAdapter
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.ui.AllCourseFragmentUi
import com.codingblocks.onlineapi.Clients
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.ctx
import kotlin.concurrent.thread


class MyCoursesFragment : Fragment(), AnkoLogger {

    val ui = AllCourseFragmentUi<Fragment>()
    private lateinit var database: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ui.createView(AnkoContext.create(ctx, this))
    }

    private lateinit var courseDataAdapter: MyCoursesDataAdapter
    lateinit var skeletonScreen: SkeletonScreen
    lateinit var courseDao: CourseDao
    lateinit var courseWithInstructorDao: CourseWithInstructorDao
    lateinit var instructorDao: InstructorDao


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui.titleText.text = "My Courses"
        courseDataAdapter = MyCoursesDataAdapter(ArrayList(), activity!!)
        database = AppDatabase.getInstance(context!!)

        courseDao = database.courseDao()
        instructorDao = database.instructorDao()
        courseWithInstructorDao = database.courseWithInstructorDao()


        ui.rvCourses.layoutManager = LinearLayoutManager(ctx)
        ui.rvCourses.adapter = courseDataAdapter

        skeletonScreen = Skeleton.bind(ui.rvCourses)
                .adapter(courseDataAdapter)
                .shimmer(true)
                .angle(20)
                .frozen(true)
                .duration(1200)
                .count(4)
                .load(R.layout.item_skeleton_course_card)
                .show()

        courseWithInstructorDao.courseWithInstructors.observe(this, Observer<List<CourseWithInstructor>> {
            if (it.isNotEmpty()) {
                skeletonScreen.hide()
            }
            courseDataAdapter.setData(it as ArrayList<CourseWithInstructor>)
        })


        fetchAllCourses()

    }

    private fun fetchAllCourses() {

        Clients.onlineV2JsonApi.getMyCourses().enqueue(retrofitCallback { t, resp ->
            resp?.body()?.let {
                info { it.toString() }
                for (myCourses in it) {

                    //Add Course Progress to Course Object
                    Clients.api.getMyCourseProgress(myCourses.run_attempts?.get(0)?.id.toString()).enqueue(retrofitCallback { t, progressResponse ->
                        progressResponse?.body().let { map ->
                            val progress = map!!["percent"] as Double
                            val course = myCourses.course?.run {
                                Course(
                                        id ?: "",
                                        title ?: "",
                                        subtitle ?: "",
                                        logo ?: "",
                                        summary ?: "",
                                        promoVideo ?: "",
                                        difficulty ?: "",
                                        reviewCount ?: 0,
                                        rating ?: 0f,
                                        slug ?: "",
                                        coverImage ?: "",
                                        myCourses.run_attempts?.get(0)?.id ?: "",
                                        updatedAt,
                                        progress,
                                        myCourses.description ?: ""
                                )
                            }
                            thread {
                                courseDao.insert(course!!)
                            }
                        }
                    })

                    //fetch CourseInstructors
                    for (i in 0 until myCourses.course!!.instructors?.size!!) {

                        Clients.onlineV2JsonApi.instructorsById(myCourses.course!!.instructors!![i].id!!).enqueue(retrofitCallback { throwable, response ->
                            response?.body().let {
                                thread {
                                    instructorDao.insert(Instructor(it?.id!!, it.name!!,
                                            it.description!!, it.photo!!,
                                            "", myCourses.run_attempts!![0].id!!, myCourses.course!!.id))
                                }

                            }
                        })
                    }
                }
            }
        })
    }


}
