package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.codingblocks.cbonlineapp.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.view
import org.jetbrains.anko.wrapContent

class MyCourseCardUi : AnkoComponent<ViewGroup> {
    inline fun ViewManager.circleImageView(theme: Int = 0, init: CircleImageView.() -> Unit) = ankoView({ CircleImageView(it) }, theme, init)
    lateinit var courseTitle: TextView
    lateinit var courseRun: TextView
    lateinit var enrollment: TextView
    lateinit var coursePrice: TextView
    lateinit var courseMrp: TextView
    lateinit var courselogo: CircleImageView
    lateinit var courseRatingBar: RatingBar
    lateinit var courseRatingTv: TextView
    lateinit var courseInstrucImgView1: CircleImageView
    lateinit var courseInstrucImgView2: CircleImageView
    lateinit var courseInstructors: TextView
    lateinit var courseCoverImageView: ImageView
    var font: Typeface? = null
    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        frameLayout {
            font = ResourcesCompat.getFont(ctx, R.font.nunitosans_semibold)
            cardView {
                cardElevation = dip(4).toFloat()
                radius = dip(15).toFloat()
                preventCornerOverlap = false
                linearLayout {
                    weightSum = 3.0f
                    frameLayout {
                        linearLayout {
                            gravity = Gravity.CENTER
                            orientation = LinearLayout.VERTICAL
                            elevation = 5f
                            courselogo = circleImageView {
                                topPadding = dip(16)
                                borderColor = Color.parseColor("#ffffff")
                                borderWidth = dip(2)
                            }.lparams(width = dip(76), height = dip(76))
                            courseTitle = textView {
                                textSize = 20f
                                typeface = font
                                textAlignment = View.TEXT_ALIGNMENT_CENTER
                                textColor = Color.parseColor("#ffffff")
                            }.lparams(matchParent, wrapContent) {
                                marginStart = dip(4)
                                topMargin = dip(8)
                                marginEnd = dip(4)
                                bottomMargin = dip(12)
                            }
//                            linearLayout {
//                                courseRatingBar = ratingBar{
//                                    numStars = 5
//                                    setIsIndicator(true)
//                                }.lparams(wrapContent, dip(40))
//                                courseRatingTv = textView {
//                                    textSize = 12f
//                                    typeface = font
//                                }.lparams(wrapContent, wrapContent) {
//                                    marginStart = dip(14)
//                                }
//                            }.lparams(matchParent, wrapContent) {
//                                marginStart = dip(8)
//                                marginEnd = dip(8)
//
//                            }
                        }.lparams(matchParent, matchParent)

                        courseCoverImageView = imageView {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setImageResource(R.drawable.placeholder_course_cover)
                        }.lparams(width = matchParent, height = matchParent)
                    }.lparams(width = 0, height = matchParent) {
                        weight = 1.4f
                    }
                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        linearLayout {
                            orientation = LinearLayout.HORIZONTAL
                            frameLayout {
                                courseInstrucImgView1 = circleImageView {}.lparams(dip(45), dip(45)) {
                                    topMargin = dip(8)
                                }
                                courseInstrucImgView2 = circleImageView {
                                    elevation = dip(2).toFloat()
                                    visibility = View.GONE
                                }.lparams(dip(45), dip(45)) {
                                    marginStart = dip(35)
                                    topMargin = dip(8)
                                }
                            }.lparams(wrapContent, wrapContent)
                            linearLayout {
                                orientation = LinearLayout.VERTICAL
                                textView("Instructors") {
                                    typeface = font
                                    textSize = 14f
                                    textColor = Color.parseColor("#000000")
                                }
                                courseInstructors = textView {
                                    typeface = font
                                    textSize = 14f
                                    textColor = Color.parseColor("#000000")
                                }
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(8)
                                marginStart = dip(8)
                                marginEnd = dip(12)
                            }
                        }.lparams(matchParent, wrapContent)
                        linearLayout {
                            coursePrice = textView {
                                textSize = 18f
                                textColor = context.resources.getColor(R.color.salmon)
                                typeface = font
                            }
                            courseMrp = textView {
                                textSize = 16f
                                textColor = Color.parseColor("#666666")
                                typeface = font
                            }.lparams {
                                gravity = Gravity.CENTER_VERTICAL
                                marginStart = dip(8)
                            }
                        }.lparams(width = matchParent, height = dip(wrapContent)) {
                            topMargin = dip(12)
                            marginEnd = dip(12)
                        }
                        courseRun = textView {
                            textSize = 12f
                            textColor = Color.parseColor("#666666")
                            typeface = font
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(4)
                            marginEnd = dip(12)
                        }
                        view {
                            backgroundColor = Color.parseColor("#e8e8e8")
                        }.lparams(width = matchParent, height = dip(2)) {
                            topMargin = dip(8)
                            marginEnd = dip(12)
                        }
                        enrollment = textView {
                            textSize = 14f
                            textColor = Color.parseColor("#000000")
                            typeface = font
                            textAlignment = View.TEXT_ALIGNMENT_CENTER
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(4)
                            marginEnd = dip(12)
                        }
                    }.lparams(width = 0, height = matchParent) {
                        weight = 1.6f //not support value
                        marginStart = dip(8)
                    }
                }.lparams(width = matchParent)
            }.lparams(matchParent, matchParent) {
                margin = dip(6)
            }
        }
    }
}
