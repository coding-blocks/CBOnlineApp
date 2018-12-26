package com.codingblocks.cbonlineapp.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class SkeletonCardUi : AnkoComponent<ViewGroup> {
    //custom views using anko
    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        cardView {
            backgroundResource = R.drawable.bck_rounded
            //app:cardCornerRadius = 12dp //not support attribute
            linearLayout {
                setPaddingRelative(dip(16), paddingTop, paddingEnd, paddingBottom)
                setPaddingRelative(paddingStart, paddingTop, dip(16), paddingBottom)
                topPadding = dip(12)
                bottomPadding = dip(12)
                weightSum = 3.0f
                linearLayout {
                    gravity = Gravity.CENTER
                    orientation = LinearLayout.VERTICAL
                    view {
                        backgroundResource = R.drawable.skeleton_bg_rounded
                    }.lparams(width = dip(88), height = dip(88))
                    view {
                        backgroundResource = R.drawable.skeleton_bg_rounded
                    }.lparams(width = matchParent, height = dip(15)) {
                        marginStart = dip(10)
                        topMargin = dip(8)
                        marginEnd = dip(10)
                    }
                    view {
                        backgroundResource = R.drawable.skeleton_bg_rounded
                    }.lparams(width = matchParent, height = dip(15)) {
                        topMargin = dip(4)
                    }
                    view {
                        backgroundResource = R.drawable.skeleton_bg_rounded
                    }.lparams(width = matchParent, height = dip(20)) {
                        marginStart = dip(5)
                        topMargin = dip(12)
                        marginEnd = dip(5)
                    }
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
        }
    }
}