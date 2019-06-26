package com.codingblocks.cbonlineapp.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {


    @get:Rule
    val actRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java, true, true)

    @Test
    fun testLoginButton() {
        assertNotNull(onView(withId(R.id.loginBtn)).perform(click()).perform())
    }

    @Test
    fun testSkipButton() {
        assertNotNull(onView(withId(R.id.skipBtn)).perform(click()).perform())
    }
}
