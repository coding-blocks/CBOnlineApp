package com.codingblocks.cbonlineapp.util

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import com.codingblocks.cbonlineapp.R

class Animations(val context: FragmentActivity) {
    val open: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_open)
    }
    val close: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_close)
    }
    val clock: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_rotate_clock)
    }
    val anticlock: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_rotate_anticlock)
    }
}
