package com.codingblocks.cbonlineapp.mycourse.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.quiz.info.QuizInfoFragment
import com.codingblocks.cbonlineapp.mycourse.quiz.submissions.QuizSubmissionsFragment
import kotlinx.android.synthetic.main.fragment_about_quiz.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AboutQuizFragment : Fragment(), AnkoLogger {

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
        vm.fetchQuiz()

        quizStartBtn.setOnClickListener {
            quizStartBtn.isEnabled = false
            vm.startQuiz {
                runOnUiThread {
                    quizStartBtn.isEnabled = true
                    val fragmentManager = fragmentManager!!
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
                    fragmentTransaction.replace(R.id.quizContainer, QuizFragment(), "quiz")
                    fragmentTransaction.addToBackStack("")
                    fragmentTransaction.commit()
                }
            }
        }
    }

    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(fragmentManager!!)
        adapter.add(QuizInfoFragment(), "About")
        adapter.add(QuizSubmissionsFragment(), "Submissions")
        quizViewPager.adapter = adapter
        quizTabs.setupWithViewPager(quizViewPager)
    }
}
