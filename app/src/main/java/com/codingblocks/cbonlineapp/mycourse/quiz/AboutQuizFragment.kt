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
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.fragment_about_quiz.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AboutQuizFragment : BaseCBFragment() {

    private val vm by sharedViewModel<QuizViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_about_quiz, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = TabLayoutAdapter(childFragmentManager)
        adapter.add(QuizInfoFragment(), "About")
        adapter.add(QuizSubmissionsFragment(), "Submissions")
        quizViewPager.adapter = adapter
        quizTabs.setupWithViewPager(quizViewPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        quizStartBtn.setOnClickListener {
            quizStartBtn.isEnabled = false
            vm.startQuiz().observer(viewLifecycleOwner) {
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
