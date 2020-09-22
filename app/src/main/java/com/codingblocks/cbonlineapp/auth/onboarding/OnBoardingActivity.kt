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

        initializeUI()
    }

    private fun initializeUI() {
        setupIntroSlider(buildIntroData())
        browseBtn.setOnClickListener {
            startActivity(DashboardActivity.createDashboardActivityIntent(this, false))
        }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
        }
    }

    /**
     * Sets up the intro slider with viewpager 2 and recyclerview adapter.
     * @param introArray introArray is data to be shown on slider
     */
    private fun setupIntroSlider(introArray: Array<Intro>) {
        val dotsCount = introArray.size
        val dots = Array(dotsCount) {
            ImageView(this)
        }

        setupDotsOnIntroScreen(dots)
        viewPager2.apply {
            adapter = IntroPagerAdapter(introArray)
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


    /**
     * Adds the dot views for each intro screen.
     * @param dots each dot is of ImageView type
     */
    private fun setupDotsOnIntroScreen(dots: Array<ImageView>) {
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

    /**
     * Builds intro screen data with help of resources
     * @return Intro screen array
     */
    private fun buildIntroData(): Array<Intro> {
        val introImages = resources.obtainTypedArray(R.array.intro_images)
        val introTitles = resources.obtainTypedArray(R.array.intro_titles)
        val introDescription = resources.obtainTypedArray(R.array.intro_descriptions)
        val introArray = Array(3) {
            Intro(
                introTitles.getResourceId(it, -1),
                introDescription.getResourceId(it, -1),
                introImages.getResourceId(it, -1)
            )
        }
        /**
         * Recycles the TypedArray, to be re-used by a later caller.
         * After calling this function you must not ever touch the typed array again.
         */
        introImages.recycle()
        introTitles.recycle()
        introDescription.recycle()

        return introArray
    }
}
