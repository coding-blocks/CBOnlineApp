package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.fragments.AboutQuizFragment
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.QUIZ_ID
import com.codingblocks.cbonlineapp.util.QUIZ_QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_quiz.quiz_toolbar

class QuizActivity : AppCompatActivity() {
    private val quizId: String by lazy {
        intent.getStringExtra(QUIZ_ID)
    }
    private val qnaId: String by lazy {
        intent.getStringExtra(QUIZ_QNA)
    }
    private val attemptId: String by lazy {
        intent.getStringExtra(RUN_ATTEMPT_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setSupportActionBar(quiz_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        quizId.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.framelayout_quiz, AboutQuizFragment.newInstance(it, attemptId, qnaId))
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
                .commit()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            Components.showconfirmation(this, "exit")
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
