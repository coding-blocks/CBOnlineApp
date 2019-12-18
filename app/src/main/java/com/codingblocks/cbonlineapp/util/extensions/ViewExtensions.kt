package com.codingblocks.cbonlineapp.util.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.DividerItemDecorator
import com.facebook.shimmer.ShimmerFrameLayout

fun View.applyDim(dimAmount: Float) {
    val dim = ColorDrawable(Color.BLACK)
    dim.setBounds(0, 0, this.width, this.height)
    dim.alpha = (255 * dimAmount).toInt()

    val overlay = this.overlay
    overlay.add(dim)
}

fun View.clearDim() {
    val overlay = this.overlay
    overlay.clear()
}

fun ShimmerFrameLayout.showAndStart() {
    isVisible = true
    startShimmer()
}

fun Fragment.changeViewState(recyclerView: RecyclerView, internetView: LinearLayout, emptyView: LinearLayout, shimmerView: ShimmerFrameLayout, boolean: Boolean) {
    internetView.isVisible = false
    emptyView.isVisible = boolean
    recyclerView.isVisible = !boolean
    shimmerView.hideAndStop()
}

fun Fragment.showShimmer(internetView: LinearLayout, emptyView: LinearLayout, shimmerView: ShimmerFrameLayout) {
    internetView.isVisible = false
    emptyView.isVisible = false
    shimmerView.showAndStart()
}

fun Fragment.showEmptyView(internetView: LinearLayout, emptyView: LinearLayout, shimmerView: ShimmerFrameLayout) {
    internetView.isVisible = true
    emptyView.isVisible = false
    shimmerView.hideAndStop()
}

fun ShimmerFrameLayout.hideAndStop() {
    isVisible = false
    stopShimmer()
}

/**
 * Method to replace the fragment. The [fragment] is added to the container view with id
 * [containerViewId] and a [tag]. The operation is performed by the supportFragmentManager.
 */
fun AppCompatActivity.replaceFragmentSafely(
    fragment: Fragment,
    tag: String = "",
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    @AnimRes enterAnimation: Int = 0,
    @AnimRes exitAnimation: Int = 0,
    @AnimRes popEnterAnimation: Int = 0,
    @AnimRes popExitAnimation: Int = 0
) {
    val ft = supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
        .replace(containerViewId, fragment, tag)
    if (!supportFragmentManager.isStateSaved) {
        ft.commit()
    } else if (allowStateLoss) {
        ft.commitAllowingStateLoss()
    }
}

fun RecyclerView.setRv(activity: Context, setDivider: Boolean = false, type: String = "") {
    val dividerItemDecoration = if (type == "thick")
        DividerItemDecorator(ContextCompat.getDrawable(activity, R.drawable.dividerthick)!!)
    else DividerItemDecorator(ContextCompat.getDrawable(activity, R.drawable.divider)!!)

    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    if (setDivider) addItemDecoration(dividerItemDecoration)
}
