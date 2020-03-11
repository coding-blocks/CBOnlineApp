package com.codingblocks.cbonlineapp.util

import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import com.codingblocks.cbonlineapp.R

class Animations(val context: FragmentActivity) {
    val open by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_open)
    }
    val close by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_close)
    }
    val clock by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_rotate_clock)
    }
    val anticlock by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_rotate_anticlock)
    }
}
