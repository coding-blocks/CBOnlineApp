package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.Course
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.adapters.CarouselSliderAdapter
import com.codingblocks.cbonlineapp.util.ZoomOutPageTransformer

import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Runs
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*
import kotlin.concurrent.thread

class HomeFragment : Fragment(), AnkoLogger {

    private lateinit var courseDataAdapter: CourseDataAdapter
    private lateinit var skeletonScreen: SkeletonScreen
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val ui = HomeFragmentUi<Fragment>()
    var currentPage = 0


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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? = ui.createView(AnkoContext.create(ctx, this))


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        setHasOptionsMenu(true)

        courseDataAdapter = CourseDataAdapter(ArrayList(), view.context, courseWithInstructorDao, "allCourses")


        ui.rvCourses.layoutManager = LinearLayoutManager(ctx)
        ui.rvCourses.adapter = courseDataAdapter
        ui.homeImg.visibility = View.GONE



        skeletonScreen = Skeleton.bind(ui.rvCourses)
                .adapter(courseDataAdapter)
                .shimmer(true)
                .angle(20)
                .frozen(true)
                .duration(1200)
                .count(4)
                .load(R.layout.item_skeleton_course_card)
                .show()
        ui.swipeRefreshLayout.setOnRefreshListener {
            fetchRecommendedCourses()
        }
        displayCourses()
        fetchRecommendedCourses()
        fetchCards()

    }

    private fun fetchCards() {
        Clients.onlineV2JsonApi.carouselCards.enqueue(retrofitCallback { _, response ->
            response?.body().let {
                val carouselSliderAdapter = CarouselSliderAdapter(it!!, context!!)
                ui.viewPager.adapter = carouselSliderAdapter
                ui.viewPager.currentItem = 0
//                if (categories.size > 1)
//                    view.tabDots.setupWithViewPager(view.imageViewPager, true)
                ui.viewPager.setPageTransformer(true, ZoomOutPageTransformer())
                val handler = Handler()
                val update = Runnable {
                    if (currentPage == it.size) {
                        currentPage = 0
                    }
                    ui.viewPager.setCurrentItem(currentPage++, true)
                }
                val swipeTimer = Timer()
                swipeTimer.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 5000, 5000)
            }
        })
    }

    private fun displayCourses(searchQuery: String = "") {
        runDao.getRecommendedRuns().observer(this) {
            if (!it.isEmpty()) {
                skeletonScreen.hide()
                courseDataAdapter.setData(it.filter { c ->
                    c.title.contains(searchQuery, true)
                } as ArrayList<CourseRun>)
            }

        }
    }

    private fun fetchRecommendedCourses() {


        Clients.onlineV2JsonApi.getRecommendedCourses().enqueue(retrofitCallback { _, resp ->
            skeletonScreen.hide()
            resp?.body()?.let {
                for (myCourses in it) {
                    //calculate top run
                    val unsortedRuns: ArrayList<Runs> = arrayListOf()
                    for (i in 0 until myCourses.runs!!.size) {
                        if (myCourses.runs!![i].enrollmentStart!!.toLong() < (System.currentTimeMillis() / 1000)
                                && myCourses.runs!![i].enrollmentEnd!!.toLong() > (System.currentTimeMillis() / 1000) && !myCourses.runs!![i].unlisted!!)
                            unsortedRuns.add(myCourses.runs!![i])
                    }
                    //for no current runs
                    if (unsortedRuns.size == 0) {
                        unsortedRuns.addAll(myCourses.runs!!)
                    }
                    val currentRuns = unsortedRuns.sortedWith(compareBy { it.price })

                    val course = myCourses.run {
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

                    val courseRun = CourseRun(
                        currentRuns[0].id ?: "",
                        "",
                        currentRuns[0].name ?: "",
                        currentRuns[0].description ?: "",
                        currentRuns[0].enrollmentStart ?: "",
                        currentRuns[0].enrollmentEnd ?: "",
                        currentRuns[0].start ?: "",
                        currentRuns[0].end ?: "",
                        currentRuns[0].price ?: "",
                        currentRuns[0].mrp ?: "",
                        myCourses.id ?: "",
                        currentRuns[0].updatedAt ?: "",
                        title = myCourses.title ?: "",
                        recommended = true
                    )

                    thread {
                        courseDao.insert(course)

                        val oldRun = runDao.getRunById(currentRuns[0].id!!)
                        if (oldRun == null)
                            runDao.insert(courseRun)
                        else if(oldRun.recommended != courseRun.recommended || oldRun.crPrice != courseRun.crPrice){
                            runDao.update(courseRun)
                        }

                        if (ui.swipeRefreshLayout.isRefreshing) {
                            runOnUiThread {
                                ui.swipeRefreshLayout.isRefreshing = false
                            }
                        }
                        //Add CourseInstructors
                        for (i in myCourses.instructors!!) {
                            instructorDao.insert(
                                Instructor(
                                    i.id ?: "", i.name ?: "",
                                    i.description ?: "", i.photo ?: "",
                                    "", "", myCourses.id
                                )
                            )
                            insertCourseAndInstructor(myCourses, i)
                        }
                    }

                }
            }
        })
    }

    private fun insertCourseAndInstructor(course: com.codingblocks.onlineapi.models.Course, instructor: com.codingblocks.onlineapi.models.Instructor) {

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
                Log.e("CRASH", "COURSE ID : ${course.id}")
                Log.e("CRASH", "INSTRUCTOR ID : ${instructor.id.toString()}")
            }
        }
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

}
