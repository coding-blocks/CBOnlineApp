package com.codingblocks.cbonlineapp.ui

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.codingblocks.cbonlineapp.R
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class NotificationCardUi : AnkoComponent<ViewGroup> {
    lateinit var title: TextView
    lateinit var body: TextView
    var font: Typeface? = null
    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        frameLayout {
            font = ResourcesCompat.getFont(ctx, R.font.nunitosans_semibold)
            cardView {
                cardElevation = dip(4).toFloat()
                radius = dip(15).toFloat()
                verticalLayout {
                    title = textView {
                        textSize = 20f
                        textColor = context.resources.getColor(R.color.black)
                        typeface = font
                    }
                    body = textView {
                        textSize = 16f
                        textColor = context.resources.getColor(R.color.black)
                        typeface = font
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
