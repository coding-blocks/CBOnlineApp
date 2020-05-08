package com.codingblocks.cbonlineapp.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsFragment
import com.codingblocks.cbonlineapp.dashboard.explore.DashboardExploreFragment
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeFragment
import com.codingblocks.cbonlineapp.dashboard.library.DashboardLibraryFragment
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesFragment

class ViewPager2Adapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragmentList: MutableList<FragmentName> = mutableListOf()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (fragmentList[position]) {
            FragmentName.EXPLORE -> DashboardExploreFragment()
            FragmentName.COURSES -> DashboardMyCoursesFragment()
            FragmentName.HOME -> DashboardHomeFragment()
            FragmentName.DOUBTS -> DashboardDoubtsFragment()
            FragmentName.LIBRARY -> DashboardLibraryFragment()
        }
    }

    override fun getItemId(position: Int): Long {
        return fragmentList[position].ordinal.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        val fragment = FragmentName.values()[itemId.toInt()]
        return fragmentList.contains(fragment)
    }

    fun add(fragment: FragmentName) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }

    fun add(index: Int, fragment: FragmentName) {
        fragmentList.add(index, fragment)
        notifyDataSetChanged()
    }

    fun remove(index: Int) {
        fragmentList.removeAt(index)
        notifyDataSetChanged()
    }

    fun remove(name: FragmentName) {
        fragmentList.remove(name)
        notifyDataSetChanged()
    }

    enum class FragmentName {
        EXPLORE,
        COURSES,
        HOME,
        DOUBTS,
        LIBRARY
    }
}
