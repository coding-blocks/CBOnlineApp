package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import kotlinx.android.synthetic.main.activity_on_boarding.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class OnBoardingActivity : BaseCBActivity() {

    private val sharedPrefs by inject<PreferenceHelper>()
    private val mAdapter by lazy {
        IntroPagerAdapter(this)
    }
    var dotsCount = 0
    private val dots by lazy {
        Array(3) {
            ImageView(this)
        }
    }
    var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        setAdapter()

        browseBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
        }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
        }
    }

    private fun setAdapter() {
        viewPager.apply {
            adapter = mAdapter
            currentItem = 0
            offscreenPageLimit = 1
            addOnPageChangeListener(
                pageChangeCallback { pos, fl, i2 ->
                    for (i in 0 until dotsCount) {
                        dots[i].isSelected = false
                    }
                    dots[pos].isSelected = true
                }
            )
            setScrollDuration(200)
        }
        dotsCount = mAdapter.count
        for (i in 0..2) {
            dots[i] = ImageView(this)
            dots[i].setImageDrawable(getDrawable(R.drawable.dots))
            dots[i].isSelected = false
            val params = LinearLayout.LayoutParams(20, 20, 1f)
            params.setMargins(10, 0, 10, 0)
            viewPagerCountDots.addView(dots[i], params)
        }
        dots[0].isSelected = true
    }
}
