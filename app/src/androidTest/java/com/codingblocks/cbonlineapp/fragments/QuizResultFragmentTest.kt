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
class QuizResultFragmentTest {


    @Before
    fun setUp() {
        FragmentScenario.launchInContainer(QuizResultFragment::class.java, Bundle().apply {
            putString(QUIZ_ID, "23")
            putString(QUIZ_QNA, "20")
            putString(RUN_ATTEMPT_ID, "22685")
            putString(QUIZ_ATTEMPT_ID, "7772")
        })
        Thread.sleep(10000)
    }

    @Test
    fun testCorrectAnswers() {
        onView(withId(R.id.correct_answers_score_image)).check(matches(isDisplayed()))
    }

    @Test
    fun testTotalQuestions() {
        onView(withId(R.id.total_questions_image)).check(matches(isDisplayed()))
    }

    @Test
    fun testWrongAnswers() {
        onView(withId(R.id.wrong_answers_image)).check(matches(isDisplayed()))
    }

    @Test
    fun testGoBackButton() {
        onView(withId(R.id.quizResultGoBackBtn)).check(matches(isDisplayed()))
    }
}
