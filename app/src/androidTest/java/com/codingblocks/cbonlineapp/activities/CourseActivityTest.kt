package com.codingblocks.cbonlineapp.activities

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.PerformException
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.action.ViewActions.click
import org.hamcrest.Matcher
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.codingblocks.cbonlineapp.viewactions.CustomViewAction
import org.hamcrest.Matchers

@SmallTest
@RunWith(AndroidJUnit4::class)
class CourseActivityTest {

    @get:Rule
    public val actRule = ActivityTestRule<CourseActivity>(CourseActivity::class.java, true, false)

    fun nestedScrollTo(): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(
                    isDescendantOfA(isAssignableFrom(NestedScrollView::class.java)),
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
            }

            override fun getDescription(): String {
                return "View is not NestedScrollView"
            }

            override fun perform(uiController: UiController, view: View) {
                try {
                    val nestedScrollView = findFirstParentLayoutOfClass(view, NestedScrollView::class.java) as NestedScrollView?
                    if (nestedScrollView != null) {
                        nestedScrollView.scrollTo(0, view.top)
                    } else {
                        throw Exception("Unable to find NestedScrollView parent.")
                    }
                } catch (e: Exception) {
                    throw PerformException.Builder()
                        .withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(e)
                        .build()
                }

                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    private fun findFirstParentLayoutOfClass(view: View, parentClass: Class<out View>): View? {
        var parent: ViewParent? = FrameLayout(view.context)
        var incrementView: ViewParent? = null
        var i = 0
        while (parent != null && parent.javaClass != parentClass) {
            if (i == 0) {
                parent = findParent(view)
            } else {
                parent = findParent(incrementView!!)
            }
            incrementView = parent
            i++
        }
        return parent as View?
    }

    private fun findParent(view: View): ViewParent {
        return view.parent
    }

    private fun findParent(view: ViewParent): ViewParent {
        return view.parent
    }

    @Before
    fun setUp() {
        val intent = Intent()
        intent.putExtra("courseId", "40")
        intent.putExtra("courseName", "Machine Learning Master Course")
        actRule.launchActivity(intent)
        Thread.sleep(30000)
    }

    @Test
    fun testToolBarVisibility() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testBuyButtonUnderVideo() {
        onView(withId(R.id.buyBtn)).check(matches(isDisplayed())).perform(click()).perform()
    }

    @Test
    fun testYoutubePlayerVisible() {
        onView(withId(R.id.displayYoutubeVideo)).perform(nestedScrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun testTrialUnderYoutubeClick() {
        onView(withId(R.id.trialBtn)).check(matches(isDisplayed())).perform(click()).perform()
    }

    @Test
    fun testExpandableRecyclerView() {
        onView(withId(R.id.rvExpendableView)).perform(nestedScrollTo()).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @Test
    fun testInstructorsRecyclerView() {
        onView(withId(R.id.instructorRv)).perform(nestedScrollTo()).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0)).check(matches(isDisplayed()))
    }

    @Test
    fun testBatchesRecyclerView() {
        onView(withId(R.id.batchRv)).perform(nestedScrollTo()).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, CustomViewAction().clickChildViewWithId("ENROLL NOW")))
    }

    @Test
    fun testBottomSheet() {
        onView(withId(R.id.batchRv)).perform(nestedScrollTo()).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, CustomViewAction().clickChildViewWithId("ENROLL NOW")))
        Thread.sleep(5000)
        onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckoutButton() {
        onView(withId(R.id.batchRv)).perform(nestedScrollTo()).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, CustomViewAction().clickChildViewWithId("ENROLL NOW")))
        Thread.sleep(5000)
        onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()))
        onView((withId(R.id.checkoutBtn))).check(matches(isDisplayed())).perform(click()).perform()
    }

    @Test
    fun testContinueButton() {
        onView(withId(R.id.batchRv)).perform(nestedScrollTo()).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, CustomViewAction().clickChildViewWithId("ENROLL NOW")))
        Thread.sleep(5000)
        onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()))
        onView(withId(R.id.continueBtn)).check(matches(isDisplayed())).perform(click()).perform()
    }
}
