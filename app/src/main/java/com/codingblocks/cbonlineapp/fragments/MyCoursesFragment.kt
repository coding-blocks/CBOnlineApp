package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.adapters.MyCoursesDataAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
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
        courseDataAdapter = MyCoursesDataAdapter(ArrayList())

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


        Clients.onlineV2PublicClient.getMyCourses("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTAtMTJUMTA6MDY6NTMuNjE0WiIsImNsaWVudElkIjoiYjI4NDFlNGQtZmY0Yi00OTI5LWJhOGUtMmQxZmM0ZTYwMTFmIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTM5NTY1NzE4LCJleHAiOjE1Mzk1NjcyMTh9.U7JmDSg4L_5bBmMBcFSkpQN_t3lYb_himb88eBJqqUBD2e2xS9PGcB6dFTHbiwHj7qhzcOC85x7Lklbi7oWdHrW7fL25LOxg52JT10GnDX41hxamo1fnvvnJ3HI0hx1gvUElaAmia4Kyg1VVgLp7EiH9rphMRV_lhTLz0nF2usz92eGh01P0V9XYqYiiVWH3H_1-vqktHA0yLWHw27taKqruZPdGAWjBnN7aO7lmk3IhfU0fvQkgumFxtS_Jmy_cPL-kJglDq3sEoDUtuOjpt4H25loy_GMufBQeogevpZfWPkcNqYpSzEAqWb5Rh6oMXd84SnAyUkbr4ytqoE4ZhA").enqueue(retrofitcallback { t, resp ->
            resp?.body()?.let {
                info { it.toString() }
                courseDataAdapter.setData(it)
                skeletonScreen.hide()
            }
        })
    }


}
