package com.codingblocks.cbonlineapp.util.extensions

import android.animation.Animator
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

infix fun Boolean.then(block: () -> Unit) = if (this) block() else null

infix fun Boolean.otherwise(block: () -> Unit) = if (this) null else block()

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any, K : Any> T.sameAndEqual(n: K): Boolean = ((this.javaClass == n.javaClass) && (this == n))

fun View.animateVisibility(visible: Int) {
    if (visible == View.VISIBLE) {
        visibility = View.VISIBLE
        alpha = 0f
        scaleX = 0f
        scaleY = 0f
    }
    val value = if (visible == View.VISIBLE) 1f else 0f
    animate()
        .alpha(value)
        .scaleX(value)
        .scaleY(value)
        .setDuration(300)
        .setInterpolator(OvershootInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                if (visible == View.GONE)
                    visibility = View.GONE
                else
                    animate()
                        .setInterpolator(LinearOutSlowInInterpolator())
                        .start()
            }

            override fun onAnimationCancel(animation: Animator) {
                if (visible == View.GONE) {
                    visibility = View.GONE
                }
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        .start()
}
