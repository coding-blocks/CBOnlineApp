package com.codingblocks.cbonlineapp.activities

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import kotlinx.android.synthetic.main.activity_my_course.*
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MyCourseActivityTest {

    @get:Rule
    val actRule = ActivityTestRule<MyCourseActivity>(MyCourseActivity::class.java, true, false)

    @Before
    fun setUp(){
        val intent = Intent()
        intent.putExtra(COURSE_ID,"17")
        intent.putExtra(RUN_ATTEMPT_ID,"23092")
        intent.putExtra(COURSE_NAME,"Competitive Programming Online")
        intent.putExtra(RUN_ID,"209")
        actRule.launchActivity(intent)
        Thread.sleep(30000)
    }

    @Test
    fun testYoutubeVideoVisibility(){
        onView(withId(R.id.displayYoutubeVideo)).check(matches(isDisplayed()))
    }

    @Test
    fun testViewPager(){
        onView(withId(R.id.htab_viewpager)).check(matches(isDisplayed()))
    }

}
