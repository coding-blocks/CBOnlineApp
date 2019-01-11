package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.fragments.AboutQuizFragment
import com.codingblocks.cbonlineapp.utils.Components
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {

    private lateinit var quizId: String
    private lateinit var attemptId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setSupportActionBar(quiz_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.getStringExtra("quizId").let {
            attemptId = intent.getStringExtra("attemptId")
            quizId = it
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.framelayout_quiz, AboutQuizFragment.newInstance(it,attemptId))
                    .commit()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        Components.showconfirmation(this,"exit")
    }
}
