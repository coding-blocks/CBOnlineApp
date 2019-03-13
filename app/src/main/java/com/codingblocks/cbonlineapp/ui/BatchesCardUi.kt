package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class BatchesCardUi : AnkoComponent<ViewGroup> {
    lateinit var courseTitle: TextView
    lateinit var courseRun: TextView
    lateinit var enrollment: TextView
    lateinit var coursePrice: TextView
    lateinit var courseMrp: TextView
    lateinit var courseRatingTv: TextView
    var font: Typeface? = null

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        frameLayout {
            cardView {
                cardElevation = dip(4).toFloat()
                radius = dip(15).toFloat()
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    textView {
                        textSize = 18f
                        textColor = context.resources.getColor(R.color.black)

                    }
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
                    }
                    linearLayout {
                        button("Enroll Now") {
                            textColor = resources.getColor(R.color.white)
                            background = resources.getDrawable(R.drawable.button_background)
                        }
                    }
                    view {
                        backgroundColor = Color.parseColor("#e8e8e8")
                    }
                            .lparams(width = matchParent, height = dip(2)) {
                                topMargin = dip(8)
                                marginEnd = dip(12)
                            }
                    linearLayout {
                        textView("You may also try our free lectures:") {
                            textColor = context.resources.getColor(R.color.black)
                            typeface = font

                        }
                        button("Free Trial") {
                            textColor = resources.getColor(R.color.salmon)
                            background = resources.getDrawable(R.drawable.button_background_outline)
                        }
                    }.lparams(width = matchParent, height = wrapContent)
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams(matchParent, matchParent) {
                margin = dip(6)
            }
        }

    }
}
