package com.codingblocks.fabnavigation

import android.app.Activity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import java.util.*

class FabNavigationAdapter
/**
 * Constructor
 *
 * @param activity
 * @param menuRes
 */
(activity: Activity, @MenuRes menuRes: Int) {

    private var mMenu: Menu
    private var navigationItems: MutableList<FabNavigationItem>? = null

    init {
        val popupMenu = PopupMenu(activity, View(activity.applicationContext))
        mMenu = popupMenu.menu
        activity.menuInflater.inflate(menuRes, mMenu)
    }

    /**
     * Setup bottom navigation (with colors)
     *
     * @param ahBottomNavigation AHBottomNavigation: Bottom navigation
     * @param colors             int[]: Colors of the item
     */
    @JvmOverloads
    fun setupWithBottomNavigation(ahBottomNavigation: FabNavigation, @ColorInt colors: IntArray? = null) {
        if (navigationItems == null) {
            navigationItems = ArrayList<FabNavigationItem>()
        } else {
            navigationItems!!.clear()
        }

        if (mMenu != null) {
            for (i in 0 until mMenu.size()) {
                val item = mMenu.getItem(i)
                if (colors != null && colors.size >= mMenu.size() && colors[i] != 0) {
                    val navigationItem = FabNavigationItem(item.title.toString(), item.icon, colors[i])
                    navigationItems!!.add(navigationItem)
                } else {
                    val navigationItem = FabNavigationItem(item.title.toString(), item.icon)
                    navigationItems!!.add(navigationItem)
                }
            }
            ahBottomNavigation.removeAllItems()
            ahBottomNavigation.addItems(navigationItems!!)
        }
    }

    /**
     * Get Menu Item
     *
     * @param index
     * @return
     */
    fun getMenuItem(index: Int): MenuItem {
        return mMenu.getItem(index)
    }

    /**
     * Get Navigation Item
     *
     * @param index
     * @return
     */
    fun getNavigationItem(index: Int): FabNavigationItem {
        return navigationItems?.get(index)!!
    }

    /**
     * Get position by menu id
     *
     * @param menuId
     * @return
     */
    fun getPositionByMenuId(menuId: Int): Int? {
        for (i in 0 until mMenu.size()) {
            if (mMenu.getItem(i).itemId == menuId)
                return i
        }
        return null
    }
}
/**
 * Setup bottom navigation
 *
 * @param ahBottomNavigation AHBottomNavigation: Bottom navigation
 */
