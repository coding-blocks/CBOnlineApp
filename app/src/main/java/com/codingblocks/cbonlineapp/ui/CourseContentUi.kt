package com.codingblocks.cbonlineapp.ui

import android.view.Gravity
import android.view.View
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.themedContentLoadingProgressBar

/**
 * Generate with Plugin
 * @plugin Kotlin Anko Converter For Xml
 * @version 1.3.4
 */
class CourseContentUi<T> : AnkoComponent<T> {
    lateinit var rvSection: RecyclerView
    lateinit var sectionProgressBar: ContentLoadingProgressBar
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun createView(ui: AnkoContext<T>): View = with(ui) {
        swipeRefreshLayout {
            swipeRefreshLayout = this
            frameLayout {
                sectionProgressBar =
                    themedContentLoadingProgressBar(R.style.AppTheme_SalmonAccent) {
                        id = View.generateViewId()
                        visibility = View.VISIBLE
                    }.lparams {
                        gravity = Gravity.CENTER_HORIZONTAL
                        topMargin = dip(10)
                    }
                rvSection = recyclerView {
                    id = View.generateViewId()
                    overScrollMode = View.OVER_SCROLL_NEVER
                }.lparams(width = matchParent) {
                    marginStart = dip(8)
                    topMargin = dip(10)
                    marginEnd = dip(8)
                }
            }
        }
    }
}
