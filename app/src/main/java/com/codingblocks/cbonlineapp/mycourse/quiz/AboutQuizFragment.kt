package com.codingblocks.cbonlineapp.mycourse.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.quiz.info.QuizInfoFragment
import com.codingblocks.cbonlineapp.mycourse.quiz.submissions.QuizSubmissionsFragment
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_about_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AboutQuizFragment : BaseCBFragment(), AnkoLogger {

    private val vm by sharedViewModel<QuizViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_about_quiz, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.content.observer(this) {
            vm.quiz = it.contentQna
            vm.attemptId = it.attempt_id
            vm.fetchQuiz()
        }

        quizStartBtn.setOnClickListener {
            quizStartBtn.isEnabled = false
            vm.startQuiz {
                runOnUiThread {
                    quizStartBtn.isEnabled = true
                    replaceFragmentSafely(
                        QuizFragment(),
                        "quiz",
                        containerViewId = R.id.quizContainer,
                        addToStack = true
                    )
                }
            }
        }
    }

    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(childFragmentManager)
        adapter.add(QuizInfoFragment(), "About")
        adapter.add(QuizSubmissionsFragment(), "Submissions")
        quizViewPager.adapter = adapter
//   FragmentManager is already executing transactions
//   quizViewPager.offscreenPageLimit = 2
        quizTabs.setupWithViewPager(quizViewPager)
    }
}
