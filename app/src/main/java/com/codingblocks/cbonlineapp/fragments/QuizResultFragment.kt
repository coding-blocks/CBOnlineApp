package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.ViewPagerAdapter
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_quiz.*

private const val ARG_QUIZ_ATTEMPT_ID = "quiz_attempt_id"

class QuizResultFragment : Fragment() {

    private lateinit var quizAttemptId: String
    private var totalQuestions : Int = 0
    private var correctAnswers : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_quiz_result, container, false).apply {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            quizAttemptId = it.getString(ARG_QUIZ_ATTEMPT_ID)!!
        }

        Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId).enqueue(retrofitCallback { _, response ->
            Log.v("Created:", response?.body()?.createdAt)
            Log.v("Status:", response?.body()?.status)
            Log.v("Score:", response?.body()?.result?.score.toString())
            Log.v("Size:", response?.body()?.result?.questions?.size.toString())
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(attemptID : String) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUIZ_ATTEMPT_ID, attemptID)
                }
            }
    }

}
