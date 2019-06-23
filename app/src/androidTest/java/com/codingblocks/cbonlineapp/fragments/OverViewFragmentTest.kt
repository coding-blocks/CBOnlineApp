package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.Clients
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OverViewFragmentTest {

    @Before
    fun setUp() {
        FragmentScenario.launchInContainer(OverviewFragment::class.java, Bundle().apply {
            putString("attempt_id", "22685")
            putString("run_id", "22685")
        }, R.style.Theme_CB_Course, null)
        Thread.sleep(10000)
    }

    @Test
    fun testRootViewVisibility() {
        onView(isRoot()).check(matches(isDisplayed()))
    }

    @Test
    fun testCardViewVisibility() {
        onView(withId(R.id.materialCardView)).check(matches(isDisplayed()))
    }

    @Test
    fun testFAQAssignment() {
        onView((withId(R.id.faqassignmentDescription))).check(matches(isDisplayed()))
    }

    @Test
    fun testFAQLectureDescription() {
        onView(withId(R.id.faqLectureDescription)).check(matches(isDisplayed()))
    }
}
