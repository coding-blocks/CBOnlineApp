package com.codingblocks.cbonlineapp.mycourse.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.CustomDialog
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.activity_quiz.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class QuizActivity : BaseCBActivity() {

    private val viewModel:QuizViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setToolbar(quizToolbar)
        intent.getStringExtra(CONTENT_ID)?.let {
            viewModel.contentId = it
        }
        viewModel.content.observer(this) {
            viewModel.attemptId = it.attempt_id
            viewModel.quizId = it.contentQna.qnaUid
            viewModel.quizQuestionId = it.contentQna.qnaQid.toString()
            replaceFragmentSafely(AboutQuizFragment(), containerViewId = R.id.quizContainer)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            CustomDialog.showConfirmation(this, "leave")
        } else {
            finish()
        }
    }

    companion object {

        fun createQuizActivityIntent(context: Context, contentId: String, sectionId: String): Intent {
            return context.intentFor<QuizActivity>(
                CONTENT_ID to contentId,
                SECTION_ID to sectionId).singleTop()
        }
    }
}
