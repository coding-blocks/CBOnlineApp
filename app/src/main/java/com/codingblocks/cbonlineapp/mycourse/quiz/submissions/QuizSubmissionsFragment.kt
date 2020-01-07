package com.codingblocks.cbonlineapp.mycourse.quiz.submissions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizViewModel
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.fragment_quiz_submissions.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class QuizSubmissionsFragment : Fragment() {

    private val quizSubmissionListAdapter = QuizSubmissionListAdapter()
    private val vm by sharedViewModel<QuizViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_quiz_submissions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizAttemptRv.setRv(requireContext(), quizSubmissionListAdapter, true)

        vm.quizAttempts.observer(viewLifecycleOwner) {
            quizSubmissionListAdapter.submitList(it)
        }
    }
}
