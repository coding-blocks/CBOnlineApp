package com.codingblocks.cbonlineapp.fragments


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.Adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.onlineapi.Clients
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.nestedScrollView


class AllCourseFragment : Fragment(), AnkoLogger {

    lateinit var rvCourses: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return UI {
            nestedScrollView {
                constraintLayout {
                    val sessionStart = textView("All Courses") {
                        id = View.generateViewId()
                        textSize = 24f
                        textColor = Color.BLACK
                        typeface = Typeface.createFromAsset(context.assets, "fonts/Cabin-Medium.ttf")
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        horizontalBias = 0.0f
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

                    }

                    val view = view {
                        id = View.generateViewId()
                        backgroundColor = Color.BLACK
                    }.lparams(width = 0, height = dip(2)) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        marginEnd = dip(20)
                        endToEnd = sessionStart.id
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = sessionStart.id

                    }


                    rvCourses = recyclerView {
                        id = View.generateViewId()
                        overScrollMode = View.OVER_SCROLL_NEVER
                    }.lparams(width = matchParent, height = 0) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        marginEnd = dip(16)
                        horizontalBias = 0.0f
                        matchConstraintDefaultHeight = ConstraintWidget.MATCH_CONSTRAINT_WRAP
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = view.id

                    }


                }.lparams(width = matchParent, height = matchParent) {
                    isFocusableInTouchMode = true
                    descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                }
            }
        }.view()
    }


    private lateinit var courseDataAdapter: CourseDataAdapter
    lateinit var skeletonScreen: SkeletonScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseDataAdapter = CourseDataAdapter(ArrayList())

        rvCourses.layoutManager = LinearLayoutManager(ctx)
        rvCourses.adapter = courseDataAdapter

        skeletonScreen = Skeleton.bind(rvCourses)
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
