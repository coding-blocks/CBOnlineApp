package com.codingblocks.cbonlineapp.extensions

import android.view.ViewManager
import android.widget.RatingBar
import androidx.annotation.StyleRes
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.styledRatingBar(@StyleRes style: Int, init: RatingBar.() -> Unit = {}): RatingBar = ankoView({ RatingBar(it, null, 0, style) }, 0, init = init)
