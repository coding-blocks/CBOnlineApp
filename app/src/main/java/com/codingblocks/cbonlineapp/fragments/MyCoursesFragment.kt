package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.MyCoursesDataAdapter
import com.codingblocks.cbonlineapp.prefs
import com.codingblocks.cbonlineapp.ui.AllCourseFragmentUi
import com.codingblocks.onlineapi.Clients
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.ctx


class MyCoursesFragment : Fragment(), AnkoLogger {

    val ui = AllCourseFragmentUi<Fragment>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ui.createView(AnkoContext.create(ctx, this))
    }

    private lateinit var courseDataAdapter: MyCoursesDataAdapter
    lateinit var skeletonScreen: SkeletonScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui.titleText.text = "My Courses"
        courseDataAdapter = MyCoursesDataAdapter(ArrayList(), activity!!)

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

        fetchAllCourses()

    }

    private fun fetchAllCourses() {

        Clients.onlineV2JsonApi.getMyCourses().enqueue(retrofitCallback { t, resp ->
            resp?.body()?.let {
                info { it.toString() }
                courseDataAdapter.setData(it)
                skeletonScreen.hide()
            }
        })
    }


}
