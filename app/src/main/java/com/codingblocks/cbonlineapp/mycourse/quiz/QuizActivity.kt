package com.codingblocks.cbonlineapp.mycourse.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_quiz.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizActivity : AppCompatActivity() {

    private val viewModel by viewModel<QuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setToolbar(quizToolbar, hasUpEnabled = true, homeButtonEnabled = true)
        viewModel.contentId = intent.getStringExtra(CONTENT_ID) ?: ""

        replaceFragmentSafely(AboutQuizFragment(), containerViewId = R.id.quizContainer)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            Components.showConfirmation(this, "exit")
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
