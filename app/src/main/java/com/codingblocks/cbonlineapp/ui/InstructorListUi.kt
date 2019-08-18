package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.MATCH_CONSTRAINT_WRAP
import com.codingblocks.cbonlineapp.extensions.circleImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.textView
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent
import org.jetbrains.anko.constraint.layout.constraintLayout

class InstructorListUi : AnkoComponent<ViewGroup> {

    lateinit var instructorTitle: TextView
    lateinit var instructorDescription: TextView
    lateinit var instructorImgView: CircleImageView
    lateinit var instructorEmail: TextView
    lateinit var instructorTextView: TextView
    lateinit var font: Typeface

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        constraintLayout {
            font = Typeface.createFromAsset(context.assets, "fonts/NunitoSans-Regular.ttf")
            // write this before using its id on another layout
            instructorImgView = circleImageView {
                id = View.generateViewId()
            }.lparams(width = dip(150), height = dip(150)) {
                marginStart = dip(8)
                topMargin = dip(8)
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            instructorTextView = textView {
                id = View.generateViewId()
                textSize = 18f
                typeface = font
                textColor = Color.parseColor("#ff6666")
            }.lparams(width = 0, height = wrapContent) {
                marginStart = dip(16)
                marginEnd = dip(8)
                startToEnd = instructorImgView.id
                topToTop = instructorImgView.id
                bottomToBottom = instructorImgView.id
                matchConstraintDefaultWidth = MATCH_CONSTRAINT_WRAP
            }
            instructorTitle = textView {
                id = View.generateViewId()
                textColor = Color.parseColor("#000000")
                textSize = 22f
                typeface = font
            }.lparams(width = dip(0), height = dip(wrapContent)) {
                marginStart = dip(16)
                marginEnd = dip(8)
                bottomToTop = instructorTextView.id
                startToEnd = instructorImgView.id
                matchConstraintDefaultWidth = MATCH_CONSTRAINT_WRAP
            }
            instructorEmail = textView {
                id = View.generateViewId()
                textColor = Color.parseColor("#000000")
                textSize = 14f
                typeface = font
            }.lparams(width = 0, height = wrapContent) {
                marginStart = dip(16)
                marginEnd = dip(8)
                startToEnd = instructorImgView.id
                topToBottom = instructorTextView.id
                matchConstraintDefaultWidth = MATCH_CONSTRAINT_WRAP
            }
            instructorDescription = textView {
                id = View.generateViewId()
                textColor = Color.parseColor("#000000")
                textSize = 14f
                typeface = font
            }.lparams(width = dip(0), height = dip(wrapContent)) {
                marginStart = dip(16)
                topMargin = dip(8)
                marginEnd = dip(8)
                topToBottom = instructorImgView.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                matchConstraintDefaultWidth = MATCH_CONSTRAINT_WRAP
            }
        }
    }
}
