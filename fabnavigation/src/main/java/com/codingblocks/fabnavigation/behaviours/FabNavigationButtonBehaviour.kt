package com.codingblocks.fabnavigation.behaviours

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.codingblocks.fabnavigation.FabNavigation
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar

class FabNavigationButtonBehaviour(navigationBarHeight: Int) : CoordinatorLayout.Behavior<View>() {

    private var navigationBarHeight = 0
    private var lastSnackbarUpdate: Long = 0

    init {
        this.navigationBarHeight = navigationBarHeight
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency != null && dependency is Snackbar.SnackbarLayout) {
            return true
        } else if (dependency != null && dependency is FabNavigation) {
            return true
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        updateFloatingActionButton(child, dependency)
        return super.onDependentViewChanged(parent, child, dependency)
    }

    /**
     * Update floating action button bottom margin
     */
    private fun updateFloatingActionButton(child: View?, dependency: View?) {
        if (child != null && dependency != null && dependency is Snackbar.SnackbarLayout) {
            lastSnackbarUpdate = System.currentTimeMillis()
            val p = child.layoutParams as ViewGroup.MarginLayoutParams
            val fabDefaultBottomMargin = p.bottomMargin
            child.y = dependency.y - fabDefaultBottomMargin
        } else if (child != null && dependency != null && dependency is FabNavigation) {
            // Hack to avoid moving the FAB when the AHBottomNavigation is moving (showing or hiding animation)
            if (System.currentTimeMillis() - lastSnackbarUpdate < 30) {
                return
            }
            val p = child.layoutParams as ViewGroup.MarginLayoutParams
            val fabDefaultBottomMargin = p.bottomMargin
            child.y = dependency.y - fabDefaultBottomMargin
        }
    }

}
