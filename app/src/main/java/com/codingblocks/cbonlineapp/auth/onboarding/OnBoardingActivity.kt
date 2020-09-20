package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.livedata.pageChangeCallback
import kotlinx.android.synthetic.main.activity_on_boarding.*
import org.jetbrains.anko.intentFor

class OnBoardingActivity : BaseCBActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        initUi()
    }

    private fun initUi() {
        setupIntroSlider()
        browseBtn.setOnClickListener {
            startActivity(DashboardActivity.createDashboardActivityIntent(this, false))
        }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
        }
    }

    private fun setupIntroSlider() {
        setUpViewPagerWithDots(getIntroList())
    }

    private fun setUpViewPagerWithDots(introList: List<Intro>) {
        val dotsCount = introList.size
        val dots = Array(dotsCount) {
            ImageView(this)
        }

        initDots(dots)
        viewPager2.apply {
            adapter = IntroPagerAdapter(introList)
            currentItem = 0
            offscreenPageLimit = 1
            registerOnPageChangeCallback(
                pageChangeCallback { pos, _, _ ->
                    for (i in 0 until dotsCount) {
                        dots[i].isSelected = false
                    }
                    dots[pos].isSelected = true
                })
        }
    }

    private fun initDots(dots: Array<ImageView>) {
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dots))
            dots[i].isSelected = false
            val params = LinearLayout.LayoutParams(20, 20, 1f)
            params.setMargins(10, 0, 10, 0)
            viewPagerCountDots.addView(dots[i], params)
        }
        dots[0].isSelected = true
    }

    private fun getIntroList(): List<Intro> {
        return listOf(
            Intro(
                getString(R.string.intro_slide_title_1),
                getString(R.string.intro_slide_message_1),
                R.drawable.intro1
            ),
            Intro(
                getString(R.string.intro_slide_title_2),
                getString(R.string.intro_slide_message_1),
                R.drawable.intro2
            ),
            Intro(
                getString(R.string.intro_slide_title_3),
                getString(R.string.intro_slide_message_1),
                R.drawable.intro3
            )
        )
    }
}
