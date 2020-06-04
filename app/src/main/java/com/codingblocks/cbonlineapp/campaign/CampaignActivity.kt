package com.codingblocks.cbonlineapp.campaign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.library.CourseLibraryFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import kotlinx.android.synthetic.main.activity_spin_win.*
import org.jetbrains.anko.intentFor
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class CampaignActivity : BaseCBActivity() {

    val vm: CampaignViewModel by stateViewModel()
    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_win)
        myCampaignTabs.setupWithViewPager(campaignPager)
        pagerAdapter.apply {
            add(HomeFragment(), getString(R.string.spin_wheel))
        }

        campaignPager.apply {
            setPagingEnabled(true)
            adapter = pagerAdapter
            currentItem = 0
            offscreenPageLimit = 4
        }

    }

    companion object {

        fun createCampaignActivityIntent(context: Context): Intent {
            return context.intentFor<CampaignActivity>()
        }
    }
}
