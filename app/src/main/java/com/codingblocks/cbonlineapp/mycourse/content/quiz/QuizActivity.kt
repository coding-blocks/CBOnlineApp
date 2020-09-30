package com.codingblocks.cbonlineapp.mycourse.content.quiz

import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.CustomDialog
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_quiz.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizActivity : BaseCBActivity() {

    private val viewModel by viewModel<QuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setToolbar(quizToolbar, hasUpEnabled = true, homeButtonEnabled = true)
        viewModel.contentId = intent.getStringExtra(CONTENT_ID) ?: ""
        viewModel.sectionId = intent.getStringExtra(SECTION_ID) ?: ""
        replaceFragmentSafely(AboutQuizFragment(), containerViewId = R.id.quizContainer)

        viewModel.bookmark.observer(this) {
            quizBookmarkBtn.isActivated = if (it == null) false else it.bookmarkUid.isNotEmpty()
        }

        viewModel.bookmarkLiveData.observer(this) {
            quizBookmarkBtn.isActivated = it
        }

        quizBookmarkBtn.setOnClickListener { view ->
            if (quizBookmarkBtn.isActivated)
                viewModel.removeBookmark()
            else {
                viewModel.markBookmark()
            }
        }

        viewModel.offlineSnackbar.observer(this) {
            quizToolbar.showSnackbar(it, Snackbar.LENGTH_SHORT, action = false)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            CustomDialog.showConfirmation(this, "leave")
        } else {
            finish()
        }
    }
}
