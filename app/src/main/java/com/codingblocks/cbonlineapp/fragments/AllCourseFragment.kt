package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.utils.getPrefs
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Runs
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import kotlin.concurrent.thread


class AllCourseFragment : Fragment(), AnkoLogger {

    val ui = HomeFragmentUi<Fragment>()
    private lateinit var courseDataAdapter: CourseDataAdapter
    lateinit var skeletonScreen: SkeletonScreen
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View = ui.createView(AnkoContext.create(ctx, this))


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "AllCourses")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        //it is important to make oncreateoptions menu work
        setHasOptionsMenu(true)

        courseDataAdapter = CourseDataAdapter(ArrayList(), view.context, courseWithInstructorDao, "allCourses")

        ui.allcourseText.text = "All Courses"
        ui.titleText.visibility = View.GONE
        ui.homeImg.visibility = View.GONE

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
            // Your code here
            fetchAllCourses()
        }
        fetchAllCourses()

    }

    private fun displayCourses(searchQuery: String = "") {
        runDao.getAllRuns().observe(this, Observer<List<CourseRun>> {
            courseDataAdapter.setData(it.filter { c ->
                c.title.contains(searchQuery, true)
            } as ArrayList<CourseRun>)
        })

    }

    private fun fetchAllCourses() {


        Clients.onlineV2JsonApi.getAllCourses().enqueue(retrofitCallback { t, resp ->
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
                                categoryId = categoryId)

                    }
                    val courseRun = CourseRun(currentRuns[0].id ?: "", "",
                            currentRuns[0].name ?: "", currentRuns[0].description ?: "",
                            currentRuns[0].enrollmentStart ?: "",
                            currentRuns[0].enrollmentEnd ?: "",
                            currentRuns[0].start ?: "", currentRuns[0].end ?: "",
                            currentRuns[0].price ?: "", currentRuns[0].mrp ?: "",
                            myCourses.id ?: "", currentRuns[0].updatedAt ?: "",
                            title = myCourses.title ?: "")

                    thread {
                        val updatedCourse = courseDao.getCourse(course.id)
                        courseDao.insert(course)
                        runDao.insert(courseRun)
                        if (ui.swipeRefreshLayout.isRefreshing) {
                            ui.swipeRefreshLayout.isRefreshing = false
                        }
                        //Add CourseInstructors
                        for (i in myCourses.instructors!!) {
                            instructorDao.insert(Instructor(i.id ?: "", i.name ?: "",
                                    i.description ?: "", i.photo ?: "",
                                    "", "", myCourses.id))
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
                courseWithInstructorDao.insert(CourseWithInstructor(course.id!!, instructor.id!!))
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CRASH", "COURSE ID : ${course.id.toString()}")
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
