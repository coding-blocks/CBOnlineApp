package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.Course
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.MyCourse
import com.crashlytics.android.core.CrashlyticsCore
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.ctx
import java.util.*
import kotlin.concurrent.thread


class MyCoursesFragment : Fragment(), AnkoLogger {

    val ui = HomeFragmentUi<Fragment>()
    private lateinit var courseDataAdapter: CourseDataAdapter
    private lateinit var skeletonScreen: SkeletonScreen
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context!!)
    }

    private val courseDao by lazy {
        database.courseDao()
    }
    private val courseWithInstructorDao by lazy {
        database.courseWithInstructorDao()
    }
    private val instructorDao by lazy {
        database.instructorDao()
    }

    private val runDao by lazy {
        database.courseRunDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = ui.createView(AnkoContext.create(ctx, this))


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "MyCourses")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        ui.allcourseText.text = getString(R.string.my_courses)
        ui.titleText.visibility = View.GONE
        ui.homeImg.visibility = View.GONE
        courseDataAdapter =
            CourseDataAdapter(ArrayList(), activity!!, courseWithInstructorDao, "myCourses")
        setHasOptionsMenu(true)


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
        displayCourses()

        ui.swipeRefreshLayout.setOnRefreshListener {
            fetchAllCourses()
        }
        fetchAllCourses()
    }


    private fun displayCourses(searchQuery: String = "") {
        runDao.getMyRuns().observer(this) {

            GlobalScope.launch(Dispatchers.Main) {

                val list = withContext(Dispatchers.Default) {
                    (it.filter { c ->
                        (c.crEnd.toLong() * 1000) > System.currentTimeMillis() &&
                            c.title.contains(searchQuery, true)
                    } as ArrayList<CourseRun>)
                }

                courseDataAdapter.setData(list)

            }
        }
    }

    private fun fetchAllCourses() {

        Clients.onlineV2JsonApi.getMyCourses().enqueue(retrofitCallback { t, resp ->
            skeletonScreen.hide()
            resp?.body()?.let {
                for (myCourses in it) {
                    //Add Course Progress to Course Object
                    Clients.api.getMyCourseProgress(myCourses.runAttempts?.get(0)?.id.toString())
                        .enqueue(retrofitCallback { t, progressResponse ->
                            progressResponse?.body().let { map ->
                                val progress: Double = try {
                                    map!!["percent"] as Double
                                } catch (e: Exception) {
                                    0.0
                                }
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
                                        updated_at = updatedAt,
                                        categoryId = categoryId
                                    )
                                }
                                val courseRun =
                                    CourseRun(
                                        myCourses.id ?: "",
                                        myCourses.runAttempts?.get(0)?.id ?: "",
                                        myCourses.name ?: "",
                                        myCourses.description ?: "",
                                        myCourses.start ?: "",
                                        myCourses.runAttempts!![0].end ?: "",
                                        myCourses.start ?: "",
                                        myCourses.runAttempts!![0].end ?: "",
                                        myCourses.price ?: "",
                                        myCourses.mrp ?: "",
                                        myCourses.course?.id ?: "",
                                        myCourses.updatedAt ?: "",
                                        progress = progress,
                                        title = myCourses.course?.title ?: "",
                                        premium = myCourses.runAttempts?.get(0)?.premium!!
                                    )

                                doAsync {
                                    val updateRun = runDao.getRunById(myCourses.id ?: "")
                                    if (updateRun == null) {
                                        courseDao.insert(course!!)
                                        runDao.insert(courseRun)
                                    } else if (updateRun.progress != progress) {
                                        courseRun.hits = updateRun.hits
                                        info { myCourses.course?.title + "updateCourse is happening" + progress + "  " + updateRun.progress }
                                        courseDao.update(course!!)
                                        runDao.update(courseRun)
                                    }

                                    if (ui.swipeRefreshLayout.isRefreshing) {
                                        ui.swipeRefreshLayout.isRefreshing = false
                                    }
                                    //fetch CourseInstructors
                                    myCourses.course?.instructors?.forEachIndexed { _, it ->
                                        Clients.onlineV2JsonApi.instructorsById(it.id!!)
                                            .enqueue(retrofitCallback { _, response ->

                                                response?.body().let { instructor ->
                                                    thread {
                                                        instructorDao.insert(
                                                            Instructor(
                                                                instructor?.id
                                                                    ?: "",
                                                                instructor?.name ?: "",
                                                                instructor?.description
                                                                    ?: "",
                                                                instructor?.photo ?: "",
                                                                "",
                                                                myCourses.runAttempts!![0].id!!,
                                                                myCourses.course!!.id
                                                            )
                                                        )
                                                        Log.e(
                                                            "TAG",
                                                            "ID : ${instructor?.id}  Name : ${instructor?.name}"
                                                        )

                                                        myCourses.course?.let { c ->
                                                            instructor?.let { i ->
                                                                insertCourseAndInstructor(c, i)
                                                            }
                                                                ?: CrashlyticsCore.getInstance().apply {
                                                                    setString("course", c.id)
                                                                    log("Instructor is NULL")
                                                                }
                                                        }
                                                    }
                                                }
                                            })
                                    }
                                }

                            }
                        })
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnCloseListener {
            displayCourses()
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                displayCourses(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun insertCourseAndInstructor(
        course: MyCourse,
        instructor: com.codingblocks.onlineapi.models.Instructor
    ) {

        thread {
            try {
                courseWithInstructorDao.insert(
                    CourseWithInstructor(
                        course.id!!,
                        instructor.id!!
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CRASH", "COURSE ID : ${course.id.toString()}")
                Log.e("CRASH", "INSTRUCTOR ID : ${instructor.id.toString()}")
            }
        }
    }
}
