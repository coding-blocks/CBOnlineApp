package com.codingblocks.cbonlineapp.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutActivityTest {

    @get:Rule
    val actRule = ActivityTestRule<AboutActivity>(AboutActivity::class.java)

    @Test
    fun testToolBarVisibility() {
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testPhoneNumber() {
        onView(withId(R.id.callTv)).check(matches(withText(R.string.tollfree_number)))
    }

    @Test
    fun testPitampuraAddress() {
        onView(withId(R.id.textView1_21)).check(matches(withText(R.string.pitampura_address)))
    }

    @Test
    fun testNoidaAddress() {
        onView(withId(R.id.textView2_21)).check(matches(withText(R.string.noida_address)))
    }
}
