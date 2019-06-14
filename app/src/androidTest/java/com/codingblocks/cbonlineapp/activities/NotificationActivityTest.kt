package com.codingblocks.cbonlineapp.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationActivityTest{

    @get:Rule
    val actRule = ActivityTestRule<NotificationsActivity>(NotificationsActivity::class.java)

    @Test
    fun testToolBar(){
        onView(withId(R.id.notificationToolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyVisibility(){
        onView(withId(R.id.emptyTv)).check(matches(isDisplayed()))
    }

}
