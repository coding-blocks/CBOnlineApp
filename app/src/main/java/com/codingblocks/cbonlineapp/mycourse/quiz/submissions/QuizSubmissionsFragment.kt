package com.codingblocks.cbonlineapp.mycourse.quiz.submissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizFragment
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizViewModel
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.onlineapi.models.QuizAttempt
import kotlinx.android.synthetic.main.fragment_quiz_submissions.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class QuizSubmissionsFragment : BaseCBFragment() {

    private val quizSubmissionListAdapter = QuizSubmissionListAdapter()
    private val vm by sharedViewModel<QuizViewModel>()

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(quizAttempt: QuizAttempt) {
                vm.quizAttempt.value = quizAttempt
                vm.quizAttemptId = quizAttempt.id
                replaceFragmentSafely(
                    QuizFragment(),
                    "quiz",
                    containerViewId = R.id.quizContainer,
                    addToStack = true
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_quiz_submissions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizAttemptRv.setRv(requireContext(), quizSubmissionListAdapter, true)

        vm.quizAttempts.observer(viewLifecycleOwner) {
            quizSubmissionListAdapter.submitList(it)
        }
        quizSubmissionListAdapter.apply {
            onItemClick = itemClickListener
        }
    }

    override fun onDestroyView() {
        quizSubmissionListAdapter.apply {
            onItemClick = null
        }
        super.onDestroyView()
    }
}
