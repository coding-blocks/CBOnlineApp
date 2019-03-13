package com.codingblocks.cbonlineapp.ui

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class BatchesCardUi : AnkoComponent<ViewGroup> {
    lateinit var runTitle: TextView
    lateinit var startTv: TextView
    lateinit var endTv: TextView
    lateinit var enrollmentTv: TextView
    lateinit var coursePrice: TextView
    lateinit var courseMrp: TextView
    lateinit var trialBtn: Button
    lateinit var enrollBtn: Button
    var font: Typeface? = null


    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        frameLayout {
            font = ResourcesCompat.getFont(ctx, R.font.cabin_medium)
            cardView {
                cardElevation = dip(4).toFloat()
                radius = dip(15).toFloat()
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    runTitle = textView {
                        textSize = 20f
                        textColor = context.resources.getColor(R.color.black)
                        typeface = font
                    }
                    linearLayout {
                        verticalLayout {
                            textView("Batch Starts") {
                                textColor = context.resources.getColor(R.color.grey850)
                                typeface = font
                            }
                            startTv = textView {
                                textColor = context.resources.getColor(R.color.black)
                                typeface = font

                            }
                        }
                        view {
                            backgroundColor = context.resources.getColor(R.color.black)
                        }.lparams(width = dip(2), height = matchParent) {
                            topMargin = dip(8)
                            bottomMargin = dip(8)
                            marginEnd = dip(8)
                            marginStart = dip(8)
                        }
                        verticalLayout {
                            textView("Batch Ends") {
                                textColor = context.resources.getColor(R.color.grey850)
                                typeface = font

                            }
                            endTv = textView {
                                textColor = context.resources.getColor(R.color.black)
                                typeface = font

                            }
                        }
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

                        verticalLayout {
                            textView("HURRY UP!") {
                                textColor = context.resources.getColor(R.color.black)
                                typeface = font

                            }
                            enrollmentTv = textView {
                                textColor = context.resources.getColor(R.color.salmon)
                                typeface = font

                            }

                        }.lparams {
                            weight = 1f
                        }
                        enrollBtn = button("Enroll Now") {
                            textColor = resources.getColor(R.color.white)
                            typeface = font
                            background = resources.getDrawable(R.drawable.button_background)
                        }.lparams{
                            marginStart = dip(4)
                            bottomMargin = dip(4)
                        }
                    }.lparams(width = matchParent, height = wrapContent)
                    view {
                        backgroundColor = Color.parseColor("#e8e8e8")
                    }.lparams(width = matchParent, height = dip(2)) {
                        topMargin = dip(8)
                        bottomMargin = dip(8)
                        marginEnd = dip(12)
                        marginStart = dip(12)
                    }
                    linearLayout {
                        gravity = Gravity.CENTER_VERTICAL
                        textView("You may also try our \n free lectures:") {
                            textColor = context.resources.getColor(R.color.black)
                            textAlignment = View.TEXT_ALIGNMENT_CENTER
                            typeface = font
                        }.lparams {
                            weight = 1f
                        }
                        trialBtn = button("Start Free Trial") {
                            textColor = resources.getColor(R.color.salmon)
                            typeface = font
                            background = resources.getDrawable(R.drawable.button_background_outline)
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    margin = dip(8)
                }
            }.lparams(matchParent, wrapContent) {
                margin = dip(6)
            }
        }

    }
}
