package com.codingblocks.cbonlineapp.util.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.DividerItemDecorator
import com.codingblocks.cbonlineapp.util.REOPENED
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.fabnavigation.FabNavigation
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog.view.*
import org.jetbrains.anko.layoutInflater
import kotlin.math.hypot

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

fun Fragment.changeViewState(recyclerView: RecyclerView, emptyView: LinearLayout, shimmerView: ShimmerFrameLayout, boolean: Boolean) {
    emptyView.isVisible = boolean
    recyclerView.isVisible = !boolean
    shimmerView.hideAndStop()
}

fun Fragment.showShimmer(internetView: LinearLayout, emptyView: LinearLayout, shimmerView: ShimmerFrameLayout) {
    internetView.isVisible = false
    emptyView.isVisible = false
    shimmerView.showAndStart()
}

fun Fragment.showEmptyView(internetView: LinearLayout? = null, emptyView: LinearLayout, shimmerView: ShimmerFrameLayout) {
    if (internetView == null) {
        emptyView.isVisible = true
    } else {
        internetView.isVisible = true
        emptyView.isVisible = false
    }

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

fun <F : Fragment> F.replaceFragmentSafely(
    fragment: Fragment,
    tag: String = "",
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    @AnimatorRes enterAnimation: Int = R.animator.slide_in_right,
    @AnimatorRes exitAnimation: Int = R.animator.slide_out_left,
    @AnimRes popEnterAnimation: Int = 0,
    @AnimRes popExitAnimation: Int = 0,
    addToStack: Boolean = true
) {
    val ft = fragmentManager!!
        .beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
        .replace(containerViewId, fragment, tag)
    if (addToStack) {
        ft.addToBackStack(tag)
    }
    if (!fragmentManager!!.isStateSaved) {
        ft.commit()
    } else if (allowStateLoss) {
        ft.commitAllowingStateLoss()
    }
}

fun RecyclerView.setRv(activity: Context, listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>, setDivider: Boolean = false, type: String = "", orientation: Int = RecyclerView.VERTICAL) {
    val dividerItemDecoration = if (type == "thick")
        DividerItemDecorator(ContextCompat.getDrawable(activity, R.drawable.dividerthick)!!)
    else DividerItemDecorator(ContextCompat.getDrawable(activity, R.drawable.divider)!!)

    layoutManager = LinearLayoutManager(activity, orientation, false)
    if (setDivider) addItemDecoration(dividerItemDecoration)
    adapter = listAdapter
}

fun View.showSnackbar(message: String, length: Int, anchorView: FabNavigation? = null, action: Boolean = true, actionText: String = "Retry", callback: () -> Unit = { }): Snackbar {
    val snackBarView = Snackbar.make(this, message, length)
    val params = snackBarView.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(params.leftMargin,
        params.topMargin,
        params.rightMargin,
        params.bottomMargin + 100)
    snackBarView.animationMode = Snackbar.ANIMATION_MODE_SLIDE

    snackBarView.view.layoutParams = params
    if (anchorView != null) {
        snackBarView.anchorView = anchorView
    }
    if (action)
        snackBarView.setAction(actionText) {
            callback()
        }
    snackBarView.show()
    return snackBarView
}

fun Context.showDialog(type: String, cancelable: Boolean = false, callback: (state: Boolean) -> Unit = { status: Boolean -> }) {

    val dialog = AlertDialog.Builder(this).create()
    val view = layoutInflater.inflate(R.layout.dialog, null)
    when (type) {
        RESOLVED -> {
            view.run {
                dialogImg.setImageResource(R.drawable.ic_resolve_dialog)
                dialogTitle.startColor = R.color.kiwigreen
                dialogTitle.endColor = R.color.tealgreen
                dialogTitle.text = context.getString(R.string.doubt_resolved_title)
                dialogDesc.text = context.getString(R.string.doubt_resolve_desc)
                primaryBtn.text = context.getString(R.string.view_resolved)
            }
        }
        REOPENED -> {
            view.run {
                dialogImg.setImageResource(R.drawable.ic_reopen)
                dialogTitle.text = context.getString(R.string.doubt_reopen_title)
                dialogDesc.text = context.getString(R.string.doubt_reopen_desc)
                primaryBtn.text = context.getString(R.string.view_live_doubts)
            }
        }
    }
    view.primaryBtn.setOnClickListener {
        callback(true)
        dialog.dismiss()
    }
    dialog.apply {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setView(view)
        setCancelable(cancelable)
        show()
    }
}

fun Context.openChrome(url: String, newTask: Boolean = false) {
    val builder = CustomTabsIntent.Builder()
        .enableUrlBarHiding()
        .setToolbarColor(getColor(R.color.colorPrimaryDark))
        .setShowTitle(true)
        .setSecondaryToolbarColor(getColor(R.color.colorPrimary))
    val customTabsIntent = builder.build()
    if (newTask) {
        customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

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

fun Toolbar.colouriseToolbar(context: Context, @DrawableRes toolbarDrawable: Int, @ColorInt foregroundColor: Int) {
    if (this == null) return
    background = AppCompatResources.getDrawable(context, toolbarDrawable)
    setTitleTextColor(foregroundColor)
    setSubtitleTextColor(foregroundColor)
    val colorFilter = PorterDuffColorFilter(foregroundColor, PorterDuff.Mode.SRC_IN)
    for (i in 0 until childCount) {
        val view: View = getChildAt(i)
        // Back button or drawer open button
        if (view is ImageButton) {
            view.drawable.colorFilter = colorFilter
        }
        if (view is ActionMenuView) {
            for (j in 0 until view.childCount) {
                val innerView: View = view.getChildAt(j)
                // Any ActionMenuViews - icons that are not back button, text or overflow menu
                if (innerView is ActionMenuItemView) {
                    val drawables = innerView.compoundDrawables
                    for (k in drawables.indices) {
                        val drawable = drawables[k]
                        if (drawable != null) {
                            innerView.post { innerView.compoundDrawables[k].colorFilter = colorFilter }
                        }
                    }
                }
            }
        }
    }
}

fun AppCompatActivity.setToolbar(
    toolbar: Toolbar,
    hasUpEnabled: Boolean = true,
    homeButtonEnabled: Boolean = true,
    title: String = "",
    show: Boolean = true
) {
    setSupportActionBar(toolbar)
    if (show) {
        if (title.isNotEmpty())
            supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(hasUpEnabled)
        supportActionBar?.setHomeButtonEnabled(homeButtonEnabled)
        supportActionBar?.show()
    } else supportActionBar?.hide()
}

/**
 * @param event callback function receiving root view, item position and type
 */
fun <T : RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(it, adapterPosition, itemViewType)
    }
    return this
}

fun SharedPreferences.save(key: String, value: Any) {
    with(this.edit()) {
        when (value) {
            is Float -> putFloat(key, value).apply()
            is Boolean -> putBoolean(key, value).apply()
            is Int -> putInt(key, value).apply()
            is String -> putString(key, value).apply()
            else -> UnsupportedOperationException("Unsupported Type to save")
        }
    }
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}

fun View.crossfade(view: View, otherView: View?, type: Int = View.INVISIBLE) {
    this.apply {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        if (otherView == null && !isVisible) {
            val cx = this.width / 2
            val cy = this.height / 2

            // get the final radius for the clipping circle
            val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
            // make the view visible and start the animation
            this.visibility = View.VISIBLE
            anim.start()
        }
    }

    var cx = view.width / 2
    var cy = view.height / 2
    var initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
    var anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.visibility = type
        }
    })
    anim.start()

    otherView?.let {
        cx = it.width / 2
        cy = it.height / 2
        initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        anim = ViewAnimationUtils.createCircularReveal(it, cx, cy, initialRadius, 0f)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                it.visibility = type
            }
        })
        anim.start()
    }
}

fun View.slideUp() {
    let {
        val cx = it.width / 2
        val cy = it.height / 2
        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(it, cx, cy, initialRadius, 0f)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                it.visibility = View.GONE
            }
        })
        anim.start()
    }
}

// slide the view from its current position to below itself
fun View.slideDown() {
    if (!isVisible) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            -height.toFloat(), // fromYDelta
            0f) // toYDelta
        animate.duration = 500
        startAnimation(animate)
    }
}
