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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)
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
