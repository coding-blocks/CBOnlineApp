package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.onlineapi.Clients
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.ctx

class HomeFragment : Fragment() {

    private lateinit var courseDataAdapter: CourseDataAdapter
    lateinit var skeletonScreen: SkeletonScreen

    val ui = HomeFragmentUi<Fragment>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ui.createView(AnkoContext.create(ctx, this))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseDataAdapter = CourseDataAdapter(ArrayList(), activity!!)

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

        fetchRecommendedCourses()

    }

    private fun fetchRecommendedCourses() {


        Clients.onlineV2PublicClient.getRecommendedCourses().enqueue(retrofitcallback { t, resp ->
            resp?.body()?.let {
                courseDataAdapter.setData(it)
                skeletonScreen.hide()
            }
        })
    }


}
