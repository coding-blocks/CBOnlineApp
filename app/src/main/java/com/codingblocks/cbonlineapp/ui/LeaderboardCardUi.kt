package com.codingblocks.cbonlineapp.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent
import org.jetbrains.anko.padding
import org.jetbrains.anko.dip
import org.jetbrains.anko.textView

class LeaderboardCardUi : AnkoComponent<ViewGroup> {

    lateinit var leaderboardsno: TextView
    lateinit var leaderboarduser: TextView
    lateinit var leaderboardcollege: TextView
    lateinit var leaderboardsscore: TextView

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        linearLayout {
            layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
            padding = dip(8)
            weightSum = 6.0f
            leaderboardsno = textView {
                gravity = Gravity.CENTER
            }.lparams(width = 0, height = matchParent) {
                weight = 1.0f
            }
            leaderboarduser = textView {
                gravity = Gravity.CENTER
            }.lparams(width = 0, height = matchParent) {
                weight = 2.0f
            }
            leaderboardcollege = textView {
                gravity = Gravity.CENTER
            }.lparams(width = 0, height = matchParent) {
                weight = 2.0f
            }
            leaderboardsscore = textView {
                gravity = Gravity.CENTER
            }.lparams(width = 0, height = matchParent) {
                weight = 1.0f
            }
        }
    }
}
