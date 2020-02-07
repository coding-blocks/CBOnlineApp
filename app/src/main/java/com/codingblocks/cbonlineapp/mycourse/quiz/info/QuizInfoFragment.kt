package com.codingblocks.cbonlineapp.mycourse.quiz.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizViewModel
import com.codingblocks.cbonlineapp.util.extensions.observer
import kotlinx.android.synthetic.main.fragment_quiz_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class QuizInfoFragment : BaseCBFragment() {

    private val vm by sharedViewModel<QuizViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_quiz_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.quizDetails.observer(viewLifecycleOwner) {
            it
            quizType.text = "MCQ"
            quizMarks.text = "${it.questions!!.size * 10}"
            quizQuestion.text = "${it.questions!!.size}"
            requireActivity().title = it.title
        }
    }
}
