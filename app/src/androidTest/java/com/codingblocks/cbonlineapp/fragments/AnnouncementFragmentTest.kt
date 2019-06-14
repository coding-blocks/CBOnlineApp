package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.ARG_COURSE_ID
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnnouncementFragmentTest{

    @Before
    fun setUp(){
        FragmentScenario.launchInContainer(AnnouncementsFragment::class.java, Bundle().apply {
            putString(ARG_COURSE_ID,"45")
        })
        Thread.sleep(30000)
    }

    @Test
    fun testAboutInstructorText(){
        onView(withId(R.id.aboutTv)).check(matches(isDisplayed()))
    }

    @Test
    fun testInstructorRecyclerView(){
        onView(withId(R.id.instructorRv)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0)).check(matches(isDisplayed()))
    }

}
