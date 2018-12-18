package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.nestedScrollView


class HomeFragmentUi<T> : AnkoComponent<T> {
    lateinit var rvCourses: RecyclerView

    override fun createView(ui: AnkoContext<T>): View = with(ui) {
        nestedScrollView {
            constraintLayout {

                val titleText = textView {
                    text = context.resources.getString(R.string.learn_to_code_interactively)
                    id = View.generateViewId()
                    textSize = 26f
                    textColor = Color.BLACK
                    val typefaceValue = ResourcesCompat.getFont(ctx, R.font.cabin_semibold)
                    typeface = typefaceValue
                    textColor = context.resources.getColor(R.color.salmon)
                }.lparams(width = wrapContent, height = wrapContent) {
                    topMargin = dip(16)
                    marginStart = dip(43)
                    marginEnd = dip(43)
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    horizontalBias = 0.0f
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

                }
                val homeImg = imageView(R.drawable.ic_home) {
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
                val allcourseText = textView {
                    text = context.resources.getString(R.string.recommended_courses)
                    id = View.generateViewId()
                    textSize = 24f
                    textColor = Color.BLACK
                    typeface = Typeface.createFromAsset(context.assets, "fonts/Cabin-Medium.ttf")
                }.lparams(width = wrapContent, height = wrapContent) {
                    topMargin = dip(8)
                    marginStart = dip(16)
                    topToBottom = homeImg.id
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