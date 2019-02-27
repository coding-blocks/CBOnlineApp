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
import com.codingblocks.cbonlineapp.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.custom.style

class MyCourseCardUi : AnkoComponent<ViewGroup> {
    //custom views using anko
    inline fun ViewManager.circleImageView(theme: Int = 0, init: CircleImageView.() -> Unit) = ankoView({ CircleImageView(it) }, theme, init)

    lateinit var courseTitle: TextView
    lateinit var courseDescription: TextView
    lateinit var courselogo: CircleImageView
    lateinit var courseRatingBar: RatingBar
    lateinit var courseRatingTv: TextView
    lateinit var courseInstrucImgView1: CircleImageView
    lateinit var courseInstrucImgView2: CircleImageView
    lateinit var courseInstructors: TextView
    lateinit var courseCoverImageView: ImageView
    lateinit var font: Typeface


    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        frameLayout {
            font = Typeface.createFromAsset(context.assets, "fonts/Cabin-Medium.ttf")
            cardView {
                cardElevation = dip(4).toFloat()
                radius = dip(15).toFloat()
                preventCornerOverlap = false

                //app:cardCornerRadius = 12dp //not support attribute
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
                            }
                            linearLayout {
                                courseRatingBar = themedRatingBar(the) {
                                    numStars = 5
                                    setIsIndicator(true)
                                }.lparams(wrapContent, wrapContent)
                                courseRatingTv = textView {
                                    textSize = 12f
                                    typeface = font
                                }.lparams(wrapContent, wrapContent) {
                                    marginStart = dip(14)
                                }
                            }
                        }.lparams(matchParent, matchParent)

                        courseCoverImageView = imageView {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            background = context.resources.getDrawable(R.drawable.bck_rounded)
                        }.lparams(width = matchParent, height = matchParent)


                    }.lparams(width = 0, height = matchParent) {
                        weight = 1.2f //not support value
                    }
                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        view {
                            backgroundResource = R.drawable.skeleton_bg_rounded
                        }.lparams(width = matchParent, height = dip(20)) {
                            marginStart = dip(12)
                            topMargin = dip(12)
                            marginEnd = dip(12)
                        }
                        view {
                            backgroundResource = R.drawable.skeleton_bg_rounded
                        }.lparams(width = matchParent, height = dip(15)) {
                            marginStart = dip(12)
                            topMargin = dip(4)
                            marginEnd = dip(24)
                        }
                        view {
                            backgroundResource = R.drawable.skeleton_bg_rounded
                        }.lparams(width = matchParent, height = dip(20)) {
                            marginStart = dip(12)
                            topMargin = dip(12)
                            marginEnd = dip(12)
                        }
                        view {
                            backgroundResource = R.drawable.skeleton_bg_rounded
                        }.lparams(width = matchParent, height = dip(15)) {
                            marginStart = dip(12)
                            topMargin = dip(4)
                            marginEnd = dip(24)
                        }
                        view {
                            backgroundResource = R.drawable.skeleton_bg_rounded
                        }.lparams(width = matchParent, height = dip(35)) {
                            marginStart = dip(24)
                            topMargin = dip(24)
                            marginEnd = dip(24)
                        }
                    }.lparams(width = 0, height = matchParent) {
                        weight = 1.8f //not support value
                    }
                }.lparams(width = matchParent)
            }.lparams(matchParent, matchParent) {
                margin = dip(6)
            }
        }

    }
}
