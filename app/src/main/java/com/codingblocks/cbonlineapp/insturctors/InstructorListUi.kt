package com.codingblocks.cbonlineapp.insturctors

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.MATCH_CONSTRAINT_WRAP
import com.codingblocks.cbonlineapp.util.extensions.circleImageView
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

class InstructorListUi : AnkoComponent<ViewGroup> {

    lateinit var instructorTitle: TextView
    lateinit var instructorDescription: TextView
    lateinit var instructorImgView: CircleImageView
    lateinit var instructorEmail: TextView
    lateinit var instructorTextView: TextView

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        constraintLayout {
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
            }.lparams(width = dip(0), height = dip(wrapContent)) {
                topMargin = dip(8)
                marginEnd = dip(8)
                topToBottom = instructorImgView.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                matchConstraintDefaultWidth = MATCH_CONSTRAINT_WRAP
            }
        }
    }
}
