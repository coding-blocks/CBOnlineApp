package com.codingblocks.cbonlineapp.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codingblocks.cbonlineapp.campaign.HomeFragment
import com.codingblocks.cbonlineapp.campaign.LeaderBoardFragment
import com.codingblocks.cbonlineapp.campaign.RulesFragment
import com.codingblocks.cbonlineapp.campaign.WinningsFragment
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsFragment
import com.codingblocks.cbonlineapp.dashboard.explore.DashboardExploreFragment
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeFragment
import com.codingblocks.cbonlineapp.dashboard.library.DashboardLibraryFragment
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesFragment
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.library.CourseLibraryFragment
import com.codingblocks.cbonlineapp.mycourse.misc.CourseMiscFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment

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
            FragmentName.COURSE_OVERVIEW -> OverviewFragment()
            FragmentName.COURSE_CURRICULUM -> CourseContentFragment()
            FragmentName.COURSE_LIBRARY -> CourseLibraryFragment()
            FragmentName.COURSE_MISC -> CourseMiscFragment()
            FragmentName.CAMPAIGN_HOME -> HomeFragment()
            FragmentName.CAMPAIGN_RULES -> RulesFragment()
            FragmentName.CAMPAIGN_LEADERBOARD -> LeaderBoardFragment()
            FragmentName.CAMPAIGN_WINNING -> WinningsFragment()
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
        LIBRARY,
        COURSE_OVERVIEW,
        COURSE_CURRICULUM,
        COURSE_LIBRARY,
        COURSE_MISC,
        CAMPAIGN_HOME,
        CAMPAIGN_WINNING,
        CAMPAIGN_LEADERBOARD,
        CAMPAIGN_RULES
    }
}
