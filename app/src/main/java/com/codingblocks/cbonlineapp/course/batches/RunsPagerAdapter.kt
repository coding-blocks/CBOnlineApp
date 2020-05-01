package com.codingblocks.cbonlineapp.course.batches

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class RunsPagerAdapter(fm: FragmentManager, var filter: List<String>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return RunFragment.newInstance(filter[position])
    }

    override fun getCount(): Int {
        return filter.size
    }
}
