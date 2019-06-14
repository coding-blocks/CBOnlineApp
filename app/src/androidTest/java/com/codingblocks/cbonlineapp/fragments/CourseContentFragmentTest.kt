package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CourseContentFragmentTest {

    @Before
    fun setup() {
        FragmentScenario.launchInContainer(CourseContentFragment::class.java, Bundle().apply {
            putString(RUN_ATTEMPT_ID, "22685")
        }, R.style.Theme_CB_Course, null)
        Thread.sleep(30000)
    }

    @Test
    fun testCourseContentRecyclerView() {
        onView(withId(R.id.rvExpendableView)).check(matches(isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())).perform()
    }
}
