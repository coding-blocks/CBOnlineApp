package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.widgets.ViewPagerCustomDuration
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.view
import org.jetbrains.anko.wrapContent

class HomeFragmentUi<T> : AnkoComponent<T> {

    inline fun ViewManager.customViewPager(theme: Int = 0, init: ViewPagerCustomDuration.() -> Unit) = ankoView({
        ViewPagerCustomDuration(it)
    }, theme, init)

    lateinit var rvCourses: RecyclerView
    lateinit var allcourseText: TextView
    lateinit var titleText: TextView
    lateinit var viewPager: ViewPagerCustomDuration
    lateinit var tabLayout: TabLayout
    lateinit var homeImg: ImageView

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun createView(ui: AnkoContext<T>): View = with(ui) {
        swipeRefreshLayout {
            swipeRefreshLayout = this
            nestedScrollView {
                constraintLayout {
                    titleText = textView {
                        text = context.resources.getString(R.string.learn_to_code_interactively)
                        id = View.generateViewId()
                        textSize = 26f
                        val typefaceValue = ResourcesCompat.getFont(ctx, R.font.nunitosans_bold)
                        typeface = typefaceValue
                        textAlignment = TEXT_ALIGNMENT_CENTER
                        textColor = context.resources.getColor(R.color.salmon)
                    }.lparams(width = matchParent, height = wrapContent) {
                        topMargin = dip(16)
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        horizontalBias = 0.0f
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    viewPager = customViewPager {
                        id = View.generateViewId()
                    }.lparams(width = matchParent, height = dip(155)) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(8)
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        horizontalBias = 0.0f
                        topToBottom = titleText.id
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    homeImg = imageView(R.drawable.ic_home) {
                        id = View.generateViewId()
                        adjustViewBounds = true
                    }.lparams(width = matchParent, height = dip(200)) {
                        marginStart = dip(16)
                        marginEnd = dip(16)
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        horizontalBias = 0.0f
                        topToBottom = titleText.id
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    allcourseText = textView {
                        text = context.resources.getString(R.string.recommended_courses)
                        id = View.generateViewId()
                        textSize = 24f
                        textColor = Color.BLACK
                        typeface = Typeface.createFromAsset(context.assets, "fonts/NunitoSans-Regular.ttf")
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        topToBottom = viewPager.id
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        horizontalBias = 0.0f
                    }
                    val view = view {
                        id = View.generateViewId()
                        backgroundColor = Color.BLACK
                    }.lparams(width = 0, height = dip(2)) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        marginEnd = dip(20)
                        endToEnd = allcourseText.id
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = allcourseText.id
                    }

                    rvCourses = recyclerView {
                        id = View.generateViewId()
                        overScrollMode = View.OVER_SCROLL_NEVER
                    }.lparams(width = matchParent, height = 0) {
                        topMargin = dip(8)
                        marginStart = dip(4)
                        marginEnd = dip(4)
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
        }
    }
}
