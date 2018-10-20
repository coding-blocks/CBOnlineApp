package com.codingblocks.cbonlineapp.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.codingblocks.cbonlineapp.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView

class MyCourseCardUi : AnkoComponent<ViewGroup> {
    //custom views using anko
    inline fun ViewManager.circleImageView(theme: Int = 0, init: CircleImageView.() -> Unit) = ankoView({ CircleImageView(it) }, theme, init)

    lateinit var courseTitle: TextView
    lateinit var courseDescription: TextView
    lateinit var courslogo: CircleImageView
    lateinit var courseRatingBar: RatingBar
    lateinit var courseRatingTv: TextView
    lateinit var courseInstrucImgView1: CircleImageView
    lateinit var courseInstrucImgView2: CircleImageView
    lateinit var courseInstructors: TextView
    lateinit var courseCoverImageView: ImageView


    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        relativeLayout {
            cardView {
                radius = 15f
                cardElevation = 8f
                preventCornerOverlap = false
                constraintLayout {
                    //write this before using its id on another layout
                    courseInstrucImgView1 = circleImageView {
                    }.lparams(width = dip(30), height = dip(30)) {
                        marginStart = dip(30)
                        topMargin = dip(12)
                        elevation = 1f
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    courseInstrucImgView2 = circleImageView {
                    }.lparams(width = dip(30), height = dip(30)) {
                        marginStart = dip(55)
                        topMargin = dip(12)
                        elevation = 1f
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    val instructorTextView = textView("Instructors") {
                        id = View.generateViewId()
                        textSize = 10f
                        textColor = Color.parseColor("#000000")
                        val typefaceValue = ResourcesCompat.getFont(ctx, R.font.cabin_medium)
                        typeface = typefaceValue
                    }.lparams(width = wrapContent, height = wrapContent) {
                        marginStart = dip(8)
                        topMargin = dip(12)
                        startToEnd = courseInstrucImgView2.id
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }

                    courseInstructors = textView {
                        textSize = 13f
                        textColor = Color.parseColor("#000000")
                        val typefaceValue = ResourcesCompat.getFont(ctx, R.font.cabin_medium)
                        typeface = typefaceValue
                    }.lparams(width = 0, height = wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        startToEnd = courseInstrucImgView2.id
                        topToBottom = instructorTextView.id
                    }
                    val imageRelative = relativeLayout {
                        id = View.generateViewId()
                        background = resources.getDrawable(R.drawable.bck_rounded)
                        courseCoverImageView = imageView {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }.lparams(width = matchParent, height = matchParent) {
                            alignParentBottom()
                            alignParentTop()
                            alignParentStart()
                            alignParentEnd()
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = dip(8)
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToStart = courseInstrucImgView1.id
                    }
                    courslogo = circleImageView {
                        id = View.generateViewId()
                        borderColor = Color.parseColor("#ffffff")
                        borderWidth = dip(2)
                    }.lparams(width = dip(46), height = dip(46)) {
                        marginStart = dip(8)
                        topMargin = dip(16)
                        marginEnd = dip(8)
                        endToEnd = imageRelative.id
                        startToStart = imageRelative.id
                        topToTop = imageRelative.id
                    }
                    courseTitle = textView {
                        id = View.generateViewId()
                        val typefaceValue = ResourcesCompat.getFont(ctx, R.font.cabin_semibold)
                        typeface = typefaceValue
                        textColor = Color.parseColor("#ffffff")
                        textSize = 14f
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }.lparams(width = dip(0), height = dip(wrapContent)) {
                        marginStart = dip(4)
                        topMargin = dip(4)
                        marginEnd = dip(4)
                        endToEnd = imageRelative.id
                        startToStart = imageRelative.id
                        topToBottom = courslogo.id
                    }
                    courseDescription = textView {
                        id = View.generateViewId()
                        val typefaceValue = ResourcesCompat.getFont(ctx, R.font.myriadpro_regular)
                        typeface = typefaceValue
                        textColor = Color.parseColor("#ffffff")
                        textSize = 12f
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }.lparams(width = dip(0), height = dip(wrapContent)) {
                        marginStart = dip(8)
                        topMargin = dip(4)
                        marginEnd = dip(8)
                        endToEnd = imageRelative.id
                        startToStart = imageRelative.id
                        topToBottom = courseTitle.id
                    }
                    linearLayout {
                        courseRatingBar = ratingBar {
                            setIsIndicator(true)
                            numStars = 5
                            progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.holo_orange_light))
                        }.lparams(width = wrapContent, height = wrapContent) {
                            gravity = Gravity.CENTER
                        }
                        courseRatingTv = textView {
                            textSize = 12f
                            val typefaceValue = ResourcesCompat.getFont(ctx, R.font.cabin_medium)
                            typeface = typefaceValue
                            textColor = Color.parseColor("#ffffff")
                        }.lparams(width = wrapContent, height = wrapContent) {
                            gravity = Gravity.CENTER
                            marginStart = dip(14)
                        }
                    }.lparams(width = wrapContent, height = wrapContent) {
                        marginStart = dip(134)
                        topMargin = dip(4)
                        marginEnd = dip(133)
                        bottomMargin = dip(8)
                        endToEnd = imageRelative.id
                        startToStart = imageRelative.id
                        topToBottom = courseDescription.id
                        bottomToBottom = imageRelative.id
                    }

                }.lparams(width = matchParent, height = matchParent) {
                    isScrollContainer = true
                }
            }.lparams(width = matchParent, height = wrapContent) {
                margin = dip(8)

            }
        }
    }
}