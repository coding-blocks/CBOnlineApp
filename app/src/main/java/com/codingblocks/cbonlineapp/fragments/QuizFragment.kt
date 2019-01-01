package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R

private const val ARG__QUIZ_ID = "quiz_id"
private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__QUIZ_ATTEMPT_ID = "quiz_attempt_id"


class QuizFragment : Fragment() {

    private lateinit var quizId: String
    private lateinit var attemptId: String
    private lateinit var quizAttemptId: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_quiz, container, false).apply {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizId = it.getString(ARG__QUIZ_ID)!!
            attemptId = it.getString(ARG__ATTEMPT_ID)!!
            quizAttemptId = it.getString(ARG__QUIZ_ATTEMPT_ID)!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(quizId: String, attemptId: String, quizAttemptId: String) =
                QuizFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__QUIZ_ID, quizId)
                        putString(ARG__ATTEMPT_ID, attemptId)
                        putString(ARG__QUIZ_ATTEMPT_ID, quizAttemptId)


                    }
                }
    }


}
