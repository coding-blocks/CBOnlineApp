package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.QUIZ_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizFragmentTest {

    @Before
    fun setUp() {
        FragmentScenario.launchInContainer(QuizFragment::class.java, Bundle().apply {
            putString(QUIZ_ID, "23")
            putString(QUIZ_QNA, "20")
            putString(RUN_ATTEMPT_ID, "22685")
            putString(QUIZ_ATTEMPT_ID, "7767")
        })
        Thread.sleep(10000)
    }

    @Test
    fun testQuizViewPager() {
        onView(withId(R.id.quizViewPager)).check(matches(isDisplayed()))
    }
}
