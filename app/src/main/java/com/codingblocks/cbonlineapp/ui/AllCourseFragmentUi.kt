package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class AllCourseFragmentUi<T> : AnkoComponent<T> {
    lateinit var rvCourses: RecyclerView
    lateinit var titleText: TextView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun createView(ui: AnkoContext<T>): View = with(ui) {
        swipeRefreshLayout {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            swipeRefreshLayout = this
            linearLayout {
                orientation = LinearLayout.VERTICAL
                titleText = textView("All Courses") {
                    id = View.generateViewId()
                    textSize = 24f
                    textColor = Color.BLACK
                    typeface = Typeface.createFromAsset(context.assets, "fonts/Cabin-Medium.ttf")
                }.lparams(width = wrapContent, height = wrapContent) {
                    topMargin = dip(8)
                    marginStart = dip(16)
                }
                view {
                    id = View.generateViewId()
                    backgroundColor = Color.BLACK
                }.lparams(width = matchParent, height = dip(2)) {
                    topMargin = dip(8)
                    marginStart = dip(16)
                    marginEnd = dip(200)
                }
                rvCourses = recyclerView {
                    id = View.generateViewId()
                    overScrollMode = View.OVER_SCROLL_NEVER
                }.lparams(width = matchParent, height = wrapContent) {
                    topMargin = dip(8)
                    marginStart = dip(4)
                    marginEnd = dip(4)

                }

            }
        }
    }


}