package com.codingblocks.cbonlineapp.activities

import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    fun openDrawer() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open())
    }

    @get:Rule
    public val actRule = ActivityTestRule<HomeActivity>(HomeActivity::class.java, false, true)

    @Before
    fun setUp() {
        Thread.sleep(5000)
    }

    @Test
    fun testAppBarVisibility() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavigationItemShowsHome() {
        openDrawer()
        assertNotNull(onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home)))
    }

    @Test
    fun clickOnNavigationItemShowsMyCourses() {
        openDrawer()
        assertNotNull(onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_my_courses)))
    }

    @Test
    fun clickOnNavigationItemOpenSettings() {
        openDrawer()
        assertNotNull(onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_preferences)))
    }

    @Test
    fun clickONNavigationItemOpensContactUs() {
        openDrawer()
        assertNotNull(onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_contactUs)))
    }

    @Test
    fun testFragmentHolderVisibility() {
        onView(withId(R.id.fragment_holder)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnNavigationItemShowsAllCourses() {
        openDrawer()
        assertNotNull(onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_all_courses)))
    }

    @After
    fun tearDown() {
        actRule.finishActivity()
    }
}
