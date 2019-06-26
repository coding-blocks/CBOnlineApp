package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoDoubtsFragmentTest {


    @Before
    fun setUp() {
        FragmentScenario.launchInContainer(VideoDoubtFragment::class.java, Bundle().apply {
            putString(com.codingblocks.cbonlineapp.util.ARG_ATTEMPT_ID, "22685")
        }, R.style.Theme_CB_Course, null)
        Thread.sleep(10000)
    }

    @Test
    fun testEmptyText() {
        onView(withId(R.id.emptyTv)).check(matches(isDisplayed()))
    }
}
